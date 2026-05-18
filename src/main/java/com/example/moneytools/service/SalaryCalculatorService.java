package com.example.moneytools.service;

import com.example.moneytools.dto.SalaryRequest;
import com.example.moneytools.dto.SalaryResult;
import com.example.moneytools.policy.Policy2026;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class SalaryCalculatorService {
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    public SalaryResult calculate(SalaryRequest request) {
        double grossMonthly = "ANNUAL".equals(request.getIncomeType())
                ? request.getAmount() / 12.0
                : request.getAmount();
        double taxableMonthly = Math.max(0, grossMonthly - request.getTaxFreeAmount());

        LocalDate today = LocalDate.now(KOREA_ZONE);
        double nationalPension = request.isApplyInsurance() ? calculateNationalPension(taxableMonthly, today) : 0.0;
        double healthInsurance = request.isApplyInsurance() ? roundWon(taxableMonthly * Policy2026.HEALTH_INSURANCE_EMPLOYEE_RATE) : 0.0;
        double longTermCareInsurance = request.isApplyInsurance() ? roundWon(healthInsurance * Policy2026.LONG_TERM_CARE_RATE_OF_HEALTH) : 0.0;
        double employmentInsurance = request.isApplyInsurance() ? roundWon(taxableMonthly * Policy2026.EMPLOYMENT_INSURANCE_EMPLOYEE_RATE) : 0.0;

        int familyCount = Math.max(1, request.getDependents());
        int eligibleChildren = eligibleChildCount(request.getChildren(), familyCount);
        double annualEmployeePension = nationalPension * 12.0;
        double annualIncomeTax = estimateAnnualIncomeTax(taxableMonthly * 12.0, familyCount, annualEmployeePension);
        double incomeTaxBeforeChildCredit = roundWon(annualIncomeTax / 12.0);
        double incomeTax = Math.max(0, incomeTaxBeforeChildCredit - withholdingChildTaxCredit(eligibleChildren));
        double localIncomeTax = roundWon(incomeTax * 0.1);

        double totalDeduction = nationalPension + healthInsurance + longTermCareInsurance + employmentInsurance + incomeTax + localIncomeTax;
        double netMonthly = Math.max(0, grossMonthly - totalDeduction);

        return new SalaryResult(
                grossMonthly,
                nationalPension,
                healthInsurance,
                longTermCareInsurance,
                employmentInsurance,
                incomeTax,
                localIncomeTax,
                totalDeduction,
                netMonthly,
                netMonthly * 12.0
        );
    }

    private double calculateNationalPension(double taxableMonthly, LocalDate today) {
        if (taxableMonthly <= 0) {
            return 0.0;
        }
        double lower = Policy2026.nationalPensionLowerBound(today);
        double upper = Policy2026.nationalPensionUpperBound(today);
        double pensionBase = clamp(taxableMonthly, lower, upper);
        return roundWon(pensionBase * Policy2026.NATIONAL_PENSION_EMPLOYEE_RATE);
    }

    private int eligibleChildCount(Integer children, int familyCount) {
        int normalizedChildren = Math.max(0, children == null ? 0 : children);
        return Math.min(normalizedChildren, Math.max(0, familyCount - 1));
    }

    private double withholdingChildTaxCredit(int eligibleChildren) {
        if (eligibleChildren <= 0) {
            return 0.0;
        }
        if (eligibleChildren == 1) {
            return Policy2026.WITHHOLDING_CHILD_TAX_CREDIT_ONE;
        }
        return Policy2026.WITHHOLDING_CHILD_TAX_CREDIT_TWO
                + Math.max(0, eligibleChildren - 2) * Policy2026.WITHHOLDING_CHILD_TAX_CREDIT_ADDITIONAL;
    }

    /**
     * 2026년 3월 1일부터 적용되는 근로소득 간이세액표의 개념을 반영한 추정식입니다.
     * 실제 회사 원천징수세액과 100% 동일하지는 않을 수 있으나,
     * 2026년 세율/근로소득공제/기본공제/근로소득세액공제/연금보험료공제와
     * 간이세액표 설명의 특별소득공제·특별세액공제 일부 반영 구조를 기준으로 계산합니다.
     */
    private double estimateAnnualIncomeTax(double annualTaxableGross, int dependentsIncludingSelf, double annualEmployeePension) {
        if (annualTaxableGross <= 0) {
            return 0.0;
        }

        int familyCount = Math.max(1, dependentsIncludingSelf);
        double earnedIncomeDeduction = earnedIncomeDeduction(annualTaxableGross);
        double basicPersonalDeduction = familyCount * Policy2026.BASIC_PERSONAL_DEDUCTION_PER_PERSON;
        double proxySpecialDeduction = proxySpecialDeduction(annualTaxableGross, familyCount);
        double taxBase = Math.max(0,
                annualTaxableGross
                        - earnedIncomeDeduction
                        - basicPersonalDeduction
                        - proxySpecialDeduction
                        - annualEmployeePension);

        double calculatedTax = progressiveIncomeTax(taxBase);
        double earnedIncomeTaxCredit = earnedIncomeTaxCredit(calculatedTax, annualTaxableGross);
        return Math.max(0, calculatedTax - earnedIncomeTaxCredit);
    }

    private double earnedIncomeDeduction(double annualGross) {
        if (annualGross <= 5_000_000) {
            return annualGross * 0.70;
        }
        if (annualGross <= 15_000_000) {
            return 3_500_000 + (annualGross - 5_000_000) * 0.40;
        }
        if (annualGross <= 45_000_000) {
            return 7_500_000 + (annualGross - 15_000_000) * 0.15;
        }
        if (annualGross <= 100_000_000) {
            return 12_000_000 + (annualGross - 45_000_000) * 0.05;
        }
        return Math.min(20_000_000, 14_750_000 + (annualGross - 100_000_000) * 0.02);
    }

    /**
     * 간이세액표 설명의 "특별소득공제 및 특별세액공제 중 일부" 반영용 보정치.
     * 가족 수는 1인, 2인, 3인 이상 구간으로 계산합니다.
     */
    private double proxySpecialDeduction(double annualGross, int familyCount) {
        int familyBucket = Math.min(familyCount, 3);

        if (annualGross <= 30_000_000) {
            return switch (familyBucket) {
                case 1 -> 3_100_000 + annualGross * 0.04;
                case 2 -> 3_600_000 + annualGross * 0.04;
                default -> 5_000_000 + annualGross * 0.07 + Math.max(0, annualGross - 40_000_000) * 0.04;
            };
        }

        if (annualGross <= 45_000_000) {
            return switch (familyBucket) {
                case 1 -> 3_100_000 + annualGross * 0.04 - (annualGross - 30_000_000) * 0.05;
                case 2 -> 3_600_000 + annualGross * 0.04 - (annualGross - 30_000_000) * 0.05;
                default -> 5_000_000 + annualGross * 0.07 - (annualGross - 30_000_000) * 0.05;
            };
        }

        if (annualGross <= 70_000_000) {
            return switch (familyBucket) {
                case 1 -> 3_100_000 + annualGross * 0.015;
                case 2 -> 3_600_000 + annualGross * 0.02;
                default -> 5_000_000 + annualGross * 0.05;
            };
        }

        return switch (familyBucket) {
            case 1 -> 3_100_000 + annualGross * 0.005;
            case 2 -> 3_600_000 + annualGross * 0.01;
            default -> 5_000_000 + annualGross * 0.03;
        };
    }

    private double progressiveIncomeTax(double taxBase) {
        if (taxBase <= 14_000_000) {
            return taxBase * 0.06;
        }
        if (taxBase <= 50_000_000) {
            return 840_000 + (taxBase - 14_000_000) * 0.15;
        }
        if (taxBase <= 88_000_000) {
            return 6_240_000 + (taxBase - 50_000_000) * 0.24;
        }
        if (taxBase <= 150_000_000) {
            return 15_360_000 + (taxBase - 88_000_000) * 0.35;
        }
        if (taxBase <= 300_000_000) {
            return 37_060_000 + (taxBase - 150_000_000) * 0.38;
        }
        if (taxBase <= 500_000_000) {
            return 94_060_000 + (taxBase - 300_000_000) * 0.40;
        }
        if (taxBase <= 1_000_000_000) {
            return 174_060_000 + (taxBase - 500_000_000) * 0.42;
        }
        return 384_060_000 + (taxBase - 1_000_000_000) * 0.45;
    }

    private double earnedIncomeTaxCredit(double calculatedTax, double annualTaxableGross) {
        double credit = calculatedTax <= 1_300_000
                ? calculatedTax * 0.55
                : 715_000 + (calculatedTax - 1_300_000) * 0.30;

        double cap;
        if (annualTaxableGross <= 33_000_000) {
            cap = 740_000;
        } else if (annualTaxableGross <= 70_000_000) {
            cap = Math.max(660_000, 740_000 - (annualTaxableGross - 33_000_000) * 0.008);
        } else if (annualTaxableGross <= 120_000_000) {
            cap = Math.max(500_000, 660_000 - (annualTaxableGross - 70_000_000) / 2.0);
        } else {
            cap = Math.max(200_000, 500_000 - (annualTaxableGross - 120_000_000) / 2.0);
        }

        return Math.min(credit, cap);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double roundWon(double value) {
        return Math.round(value);
    }
}
