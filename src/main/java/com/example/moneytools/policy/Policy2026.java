package com.example.moneytools.policy;

import java.time.LocalDate;

/**
 * 2026년 서비스 공개 시점 기준으로 프로젝트에 반영한 정책 상수입니다.
 * 매년 또는 법령/고시 개정 시 반드시 재검토해야 합니다.
 */
public final class Policy2026 {
    private Policy2026() {
    }

    public static final LocalDate WITHHOLDING_TABLE_EFFECTIVE_DATE = LocalDate.of(2026, 3, 1);

    public static final double NATIONAL_PENSION_EMPLOYEE_RATE = 0.0475; // 2026년 근로자 부담률 4.75%
    public static final LocalDate NATIONAL_PENSION_CAP_CHANGE_DATE = LocalDate.of(2026, 7, 1);
    public static final double NATIONAL_PENSION_LOWER_BOUND_BEFORE_JULY_2026 = 400_000.0;
    public static final double NATIONAL_PENSION_UPPER_BOUND_BEFORE_JULY_2026 = 6_370_000.0;
    public static final double NATIONAL_PENSION_LOWER_BOUND_FROM_JULY_2026 = 410_000.0;
    public static final double NATIONAL_PENSION_UPPER_BOUND_FROM_JULY_2026 = 6_590_000.0;

    public static final double HEALTH_INSURANCE_TOTAL_RATE = 0.0719; // 2026년 총 7.19%
    public static final double HEALTH_INSURANCE_EMPLOYEE_RATE = HEALTH_INSURANCE_TOTAL_RATE / 2.0;

    public static final double LONG_TERM_CARE_TOTAL_RATE = 0.009448; // 2026년 총 0.9448%
    public static final double LONG_TERM_CARE_RATE_OF_HEALTH = LONG_TERM_CARE_TOTAL_RATE / HEALTH_INSURANCE_TOTAL_RATE;
    public static final double LONG_TERM_CARE_EMPLOYEE_RATE = LONG_TERM_CARE_TOTAL_RATE / 2.0;

    public static final double EMPLOYMENT_INSURANCE_EMPLOYEE_RATE = 0.009; // 실업급여 근로자 부담 0.9%

    public static final double BASIC_PERSONAL_DEDUCTION_PER_PERSON = 1_500_000.0;
    public static final double WITHHOLDING_CHILD_TAX_CREDIT_ONE = 12_500.0;
    public static final double WITHHOLDING_CHILD_TAX_CREDIT_TWO = 29_160.0;
    public static final double WITHHOLDING_CHILD_TAX_CREDIT_ADDITIONAL = 25_000.0;

    public static final double OVERSEAS_STOCK_BASIC_DEDUCTION_KRW = 2_500_000.0;
    public static final double OVERSEAS_STOCK_DEFAULT_CAPITAL_GAINS_TAX_RATE = 22.0;
    public static final double OVERSEAS_STOCK_DEFAULT_DIVIDEND_TAX_RATE = 15.4;
    public static final double FINANCIAL_INCOME_COMPREHENSIVE_THRESHOLD_KRW = 20_000_000.0;

    public static double nationalPensionLowerBound(LocalDate today) {
        return today.isBefore(NATIONAL_PENSION_CAP_CHANGE_DATE)
                ? NATIONAL_PENSION_LOWER_BOUND_BEFORE_JULY_2026
                : NATIONAL_PENSION_LOWER_BOUND_FROM_JULY_2026;
    }

    public static double nationalPensionUpperBound(LocalDate today) {
        return today.isBefore(NATIONAL_PENSION_CAP_CHANGE_DATE)
                ? NATIONAL_PENSION_UPPER_BOUND_BEFORE_JULY_2026
                : NATIONAL_PENSION_UPPER_BOUND_FROM_JULY_2026;
    }
}
