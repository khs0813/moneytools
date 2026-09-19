/**
 * 머니계산기 핵심 계산 엔진 (2026 정책 기준)
 * 브라우저 및 Node.js 테스트 환경에서 공용으로 사용됩니다.
 */

export const Policy2026 = {
  NATIONAL_PENSION_EMPLOYEE_RATE: 0.0475,
  NATIONAL_PENSION_LOWER_BOUND_BEFORE_JULY_2026: 400_000.0,
  NATIONAL_PENSION_UPPER_BOUND_BEFORE_JULY_2026: 6_370_000.0,
  NATIONAL_PENSION_LOWER_BOUND_FROM_JULY_2026: 410_000.0,
  NATIONAL_PENSION_UPPER_BOUND_FROM_JULY_2026: 6_590_000.0,

  HEALTH_INSURANCE_TOTAL_RATE: 0.0719,
  HEALTH_INSURANCE_EMPLOYEE_RATE: 0.0719 / 2.0, // 0.03595

  LONG_TERM_CARE_TOTAL_RATE: 0.009448,
  LONG_TERM_CARE_RATE_OF_HEALTH: 0.009448 / 0.0719,
  LONG_TERM_CARE_EMPLOYEE_RATE: 0.009448 / 2.0, // 0.004724

  EMPLOYMENT_INSURANCE_EMPLOYEE_RATE: 0.009,

  BASIC_PERSONAL_DEDUCTION_PER_PERSON: 1_500_000.0,
  WITHHOLDING_CHILD_TAX_CREDIT_ONE: 20_830.0,
  WITHHOLDING_CHILD_TAX_CREDIT_TWO: 45_830.0,
  WITHHOLDING_CHILD_TAX_CREDIT_ADDITIONAL: 33_330.0,

  OVERSEAS_STOCK_BASIC_DEDUCTION_KRW: 2_500_000.0,
  OVERSEAS_STOCK_DEFAULT_CAPITAL_GAINS_TAX_RATE: 22.0,
  OVERSEAS_STOCK_DEFAULT_DIVIDEND_TAX_RATE: 15.4
};

export const roundWon = (val) => Math.round(Number(val) || 0);
export const clamp = (val, min, max) => Math.min(Math.max(val, min), max);

/* ----------------------------------------------------
   1. 대출이자 계산기
---------------------------------------------------- */
export function calculateLoan({ principal, annualRate, years, repaymentType }) {
  const p = Math.max(0, Number(principal) || 0);
  const monthlyRate = (Number(annualRate) || 0) / 100.0 / 12.0;
  const months = Math.max(1, (Number(years) || 0) * 12);
  let schedule = [];

  if (repaymentType === 'EQUAL_PRINCIPAL') {
    let remaining = p;
    const fixedPrincipal = p / months;
    for (let month = 1; month <= months; month++) {
      const interest = remaining * monthlyRate;
      const principalPayment = Math.min(remaining, fixedPrincipal);
      remaining = Math.max(0, remaining - principalPayment);
      const payment = principalPayment + interest;
      schedule.push({ month, payment, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
    }
  } else if (repaymentType === 'BULLET') {
    for (let month = 1; month <= months; month++) {
      const interest = p * monthlyRate;
      const principalPayment = month === months ? p : 0.0;
      const payment = interest + principalPayment;
      const remaining = month === months ? 0.0 : p;
      schedule.push({ month, payment, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
    }
  } else {
    // EQUAL_PAYMENT (원리금균등)
    let remaining = p;
    const monthlyPayment = monthlyRate === 0
      ? p / months
      : p * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1);

    for (let month = 1; month <= months; month++) {
      const interest = remaining * monthlyRate;
      const principalPayment = Math.min(remaining, monthlyPayment - interest);
      remaining = Math.max(0, remaining - principalPayment);
      const payment = principalPayment + interest;
      schedule.push({ month, payment, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
    }
  }

  const totalPayment = schedule.reduce((sum, r) => sum + r.payment, 0);
  const totalInterest = schedule.reduce((sum, r) => sum + r.interestPayment, 0);
  const firstMonthlyPayment = schedule.length > 0 ? schedule[0].payment : 0.0;
  const averageMonthlyPayment = months === 0 ? 0.0 : totalPayment / months;

  return {
    firstMonthlyPayment,
    averageMonthlyPayment,
    totalInterest,
    totalPayment,
    totalMonths: months,
    schedule
  };
}

/* ----------------------------------------------------
   2. 물타기 / 주식 평균단가 계산기
---------------------------------------------------- */
export function calculateStockAverage({ currentShares, currentAveragePrice, additionalShares, additionalPrice }) {
  const curShares = Math.max(0, Number(currentShares) || 0);
  const addShares = Math.max(0, Number(additionalShares) || 0);
  const curAvg = Math.max(0, Number(currentAveragePrice) || 0);
  const addP = Math.max(0, Number(additionalPrice) || 0);

  const currentInvestment = curShares * curAvg;
  const additionalInvestment = addShares * addP;
  const totalShares = curShares + addShares;
  const totalInvestment = currentInvestment + additionalInvestment;
  const newAveragePrice = totalShares === 0 ? 0 : totalInvestment / totalShares;
  const averagePriceChange = newAveragePrice - curAvg;
  const averagePriceChangeRate = curAvg === 0 ? 0 : (averagePriceChange / curAvg) * 100.0;

  return {
    currentInvestment,
    additionalInvestment,
    totalShares,
    totalInvestment,
    newAveragePrice,
    averagePriceChange,
    averagePriceChangeRate
  };
}

/* ----------------------------------------------------
   3. 대출 갈아타기 계산기
---------------------------------------------------- */
export function calculateLoanRefinance({
  currentBalance,
  currentAnnualRate,
  currentRemainingYears,
  newAnnualRate,
  newYears,
  repaymentType,
  prepaymentPenaltyRate,
  additionalCost
}) {
  const balance = Number(currentBalance) || 0;
  const currentMonths = Math.max(1, (Number(currentRemainingYears) || 0) * 12);
  const newMonths = Math.max(1, (Number(newYears) || 0) * 12);
  const curMonthlyRate = (Number(currentAnnualRate) || 0) / 100.0 / 12.0;
  const newMonthlyRate = (Number(newAnnualRate) || 0) / 100.0 / 12.0;

  const getMonthlyPayment = (principal, monthlyRate, months, type) => {
    if (type === 'EQUAL_PRINCIPAL') return principal / months + principal * monthlyRate;
    if (monthlyRate === 0) return principal / months;
    const factor = Math.pow(1 + monthlyRate, months);
    return principal * monthlyRate * factor / (factor - 1);
  };

  const getTotalInterest = (principal, monthlyRate, months, type) => {
    if (type === 'EQUAL_PRINCIPAL') {
      let interest = 0;
      const fixedPrincipal = principal / months;
      let remaining = principal;
      for (let m = 1; m <= months; m++) {
        interest += remaining * monthlyRate;
        remaining = Math.max(0, remaining - fixedPrincipal);
      }
      return interest;
    }
    return getMonthlyPayment(principal, monthlyRate, months, type) * months - principal;
  };

  const currentMonthlyPayment = getMonthlyPayment(balance, curMonthlyRate, currentMonths, repaymentType);
  const newMonthlyPayment = getMonthlyPayment(balance, newMonthlyRate, newMonths, repaymentType);
  const monthlySavings = currentMonthlyPayment - newMonthlyPayment;
  const currentTotalInterest = getTotalInterest(balance, curMonthlyRate, currentMonths, repaymentType);
  const newTotalInterest = getTotalInterest(balance, newMonthlyRate, newMonths, repaymentType);
  const totalInterestSavings = currentTotalInterest - newTotalInterest;
  const prepaymentPenalty = balance * (Number(prepaymentPenaltyRate) || 0) / 100.0;
  const totalSwitchingCost = prepaymentPenalty + (Number(additionalCost) || 0);
  const netSavings = totalInterestSavings - totalSwitchingCost;
  const breakEvenMonths = monthlySavings > 0 ? Math.ceil(totalSwitchingCost / monthlySavings) : -1;

  let recommendation = '갈아타기 불리';
  let recommendationLevel = 'danger';
  if (netSavings > 0) {
    if (breakEvenMonths > 0 && breakEvenMonths <= 24) {
      recommendation = '갈아타기 유리';
      recommendationLevel = 'safe';
    } else {
      recommendation = '장기 보유 시 유리';
      recommendationLevel = 'caution';
    }
  }

  return {
    currentMonthlyPayment,
    newMonthlyPayment,
    monthlySavings,
    currentTotalInterest,
    newTotalInterest,
    totalInterestSavings,
    prepaymentPenalty,
    totalSwitchingCost,
    netSavings,
    breakEvenMonths,
    recommendation,
    recommendationLevel
  };
}

/* ----------------------------------------------------
   4. 주택담보대출 월납입 계산기
---------------------------------------------------- */
export function calculateMortgage({
  housePrice,
  cashOnHand,
  expectedLoanAmount,
  annualRate,
  years,
  repaymentType,
  ltvRatio,
  annualIncome,
  existingMonthlyDebtPayment
}) {
  const principal = Number(expectedLoanAmount) || 0;
  const monthlyRate = (Number(annualRate) || 0) / 100.0 / 12.0;
  const months = Math.max(1, (Number(years) || 0) * 12);

  const getMonthlyPayment = () => {
    if (repaymentType === 'EQUAL_PRINCIPAL') return principal / months + principal * monthlyRate;
    if (repaymentType === 'BULLET') return principal * monthlyRate;
    if (monthlyRate === 0) return principal / months;
    const factor = Math.pow(1 + monthlyRate, months);
    return principal * monthlyRate * factor / (factor - 1);
  };

  const getTotalInterest = () => {
    if (repaymentType === 'EQUAL_PRINCIPAL') {
      let interest = 0;
      const fixedPrincipal = principal / months;
      let remaining = principal;
      for (let m = 1; m <= months; m++) {
        interest += remaining * monthlyRate;
        remaining = Math.max(0, remaining - fixedPrincipal);
      }
      return interest;
    }
    if (repaymentType === 'BULLET') return principal * monthlyRate * months;
    return getMonthlyPayment() * months - principal;
  };

  const monthlyPayment = getMonthlyPayment();
  const totalInterest = getTotalInterest();
  const totalPayment = principal + totalInterest;
  const requiredEquity = Math.max(0, (Number(housePrice) || 0) - principal);
  const cashShortfall = Math.max(0, requiredEquity - (Number(cashOnHand) || 0));
  const maxLoanByLtv = (Number(housePrice) || 0) * (Number(ltvRatio) || 0) / 100.0;
  const monthlyIncome = (Number(annualIncome) || 0) / 12.0;
  const burdenRate = monthlyIncome === 0 ? 0 : (monthlyPayment / monthlyIncome) * 100.0;
  const burdenRateWithDebt = monthlyIncome === 0
    ? 0
    : ((monthlyPayment + (Number(existingMonthlyDebtPayment) || 0)) / monthlyIncome) * 100.0;

  let riskLabel = '안전';
  let riskLevel = 'safe';
  if (burdenRateWithDebt > 40.0) {
    riskLabel = '위험';
    riskLevel = 'danger';
  } else if (burdenRateWithDebt > 25.0) {
    riskLabel = '주의';
    riskLevel = 'caution';
  }

  return {
    estimatedMonthlyPayment: monthlyPayment,
    totalInterest,
    totalPayment,
    requiredEquity,
    cashShortfall,
    maxLoanByLtv,
    burdenRate,
    burdenRateWithDebt,
    riskLabel,
    riskLevel
  };
}

/* ----------------------------------------------------
   5. 배당금 계산기
---------------------------------------------------- */
export function calculateDividend({ shares, dividendPerShare, period, taxApplied, taxRate }) {
  const periodMultiplier = {
    MONTHLY: 12,
    SEMI_ANNUAL: 2,
    ANNUAL: 1,
    QUARTERLY: 4
  }[period] || 4;

  const gross = (Number(shares) || 0) * (Number(dividendPerShare) || 0);
  const taxMultiplier = taxApplied ? Math.max(0, 1 - (Number(taxRate) || 0) / 100.0) : 1.0;
  const net = gross * taxMultiplier;
  const annualGross = gross * periodMultiplier;
  const annualNet = net * periodMultiplier;

  return {
    gross,
    net,
    monthlyGross: annualGross / 12.0,
    monthlyNet: annualNet / 12.0,
    annualGross,
    annualNet,
    paymentsPerYear: periodMultiplier
  };
}

/* ----------------------------------------------------
   6. 월급 실수령액 계산기 (2026년 기준)
---------------------------------------------------- */
function earnedIncomeDeduction(annualTaxableGross) {
  if (annualTaxableGross <= 5_000_000) return annualTaxableGross * 0.7;
  if (annualTaxableGross <= 15_000_000) return 3_500_000 + (annualTaxableGross - 5_000_000) * 0.4;
  if (annualTaxableGross <= 45_000_000) return 7_500_000 + (annualTaxableGross - 15_000_000) * 0.15;
  if (annualTaxableGross <= 100_000_000) return 12_000_000 + (annualTaxableGross - 45_000_000) * 0.05;
  return 14_750_000 + (annualTaxableGross - 100_000_000) * 0.02;
}

function proxySpecialDeduction(annualTaxableGross, familyCount) {
  let baseSpecialDeduction;
  if (familyCount === 1) {
    if (annualTaxableGross <= 30_000_000) {
      baseSpecialDeduction = 3_100_000 + annualTaxableGross * 0.04;
    } else if (annualTaxableGross <= 45_000_000) {
      baseSpecialDeduction = 3_100_000 + annualTaxableGross * 0.04 - (annualTaxableGross - 30_000_000) * 0.05;
    } else if (annualTaxableGross <= 70_000_000) {
      baseSpecialDeduction = 3_100_000 + annualTaxableGross * 0.015;
    } else if (annualTaxableGross <= 120_000_000) {
      baseSpecialDeduction = 3_100_000 + annualTaxableGross * 0.005;
    } else {
      baseSpecialDeduction = 3_700_000;
    }
  } else if (familyCount === 2) {
    if (annualTaxableGross <= 30_000_000) {
      baseSpecialDeduction = 3_600_000 + annualTaxableGross * 0.04;
    } else if (annualTaxableGross <= 45_000_000) {
      baseSpecialDeduction = 3_600_000 + annualTaxableGross * 0.04 - (annualTaxableGross - 30_000_000) * 0.05;
    } else if (annualTaxableGross <= 70_000_000) {
      baseSpecialDeduction = 3_600_000 + annualTaxableGross * 0.02;
    } else if (annualTaxableGross <= 120_000_000) {
      baseSpecialDeduction = 3_600_000 + annualTaxableGross * 0.01;
    } else {
      baseSpecialDeduction = 4_800_000;
    }
  } else {
    if (annualTaxableGross <= 30_000_000) {
      baseSpecialDeduction = 5_000_000 + annualTaxableGross * 0.07;
    } else if (annualTaxableGross <= 45_000_000) {
      baseSpecialDeduction = 5_000_000 + annualTaxableGross * 0.07 - (annualTaxableGross - 30_000_000) * 0.05;
    } else if (annualTaxableGross <= 70_000_000) {
      baseSpecialDeduction = 5_000_000 + annualTaxableGross * 0.05;
    } else if (annualTaxableGross <= 120_000_000) {
      baseSpecialDeduction = 5_000_000 + annualTaxableGross * 0.03;
    } else {
      baseSpecialDeduction = 8_600_000;
    }
  }
  return Math.max(0, baseSpecialDeduction);
}

function progressiveIncomeTax(taxBase) {
  if (taxBase <= 0) return 0;
  if (taxBase <= 14_000_000) return taxBase * 0.06;
  if (taxBase <= 50_000_000) return 840_000 + (taxBase - 14_000_000) * 0.15;
  if (taxBase <= 88_000_000) return 6_240_000 + (taxBase - 50_000_000) * 0.24;
  if (taxBase <= 150_000_000) return 15_360_000 + (taxBase - 88_000_000) * 0.35;
  if (taxBase <= 300_000_000) return 37_060_000 + (taxBase - 150_000_000) * 0.38;
  if (taxBase <= 500_000_000) return 94_060_000 + (taxBase - 300_000_000) * 0.40;
  if (taxBase <= 1_000_000_000) return 174_060_000 + (taxBase - 500_000_000) * 0.42;
  return 384_060_000 + (taxBase - 1_000_000_000) * 0.45;
}

function earnedIncomeTaxCredit(calculatedTax, annualTaxableGross) {
  if (calculatedTax <= 0) return 0;
  let rawCredit = calculatedTax <= 1_300_000
    ? calculatedTax * 0.55
    : 715_000 + (calculatedTax - 1_300_000) * 0.30;

  let limit;
  if (annualTaxableGross <= 33_000_000) {
    limit = 740_000;
  } else if (annualTaxableGross <= 70_000_000) {
    limit = Math.max(660_000, 740_000 - (annualTaxableGross - 33_000_000) * 0.008);
  } else if (annualTaxableGross <= 120_000_000) {
    limit = Math.max(500_000, 660_000 - (annualTaxableGross - 70_000_000) * 0.50);
  } else {
    limit = Math.max(200_000, 500_000 - (annualTaxableGross - 120_000_000) * 0.50);
  }

  return Math.min(rawCredit, limit);
}

export function calculateSalary({ incomeType, amount, taxFreeAmount, dependents, children, applyInsurance }) {
  const grossMonthly = incomeType === 'ANNUAL'
    ? (Number(amount) || 0) / 12.0
    : (Number(amount) || 0);

  const taxableMonthly = Math.max(0, grossMonthly - (Number(taxFreeAmount) || 0));

  // 오늘 날짜 기준 7월 이전/이후 (기본 7월 이전 상하한)
  const now = new Date();
  const isAfterJuly = now.getFullYear() > 2026 || (now.getFullYear() === 2026 && now.getMonth() >= 6);
  const pensionLower = isAfterJuly ? Policy2026.NATIONAL_PENSION_LOWER_BOUND_FROM_JULY_2026 : Policy2026.NATIONAL_PENSION_LOWER_BOUND_BEFORE_JULY_2026;
  const pensionUpper = isAfterJuly ? Policy2026.NATIONAL_PENSION_UPPER_BOUND_FROM_JULY_2026 : Policy2026.NATIONAL_PENSION_UPPER_BOUND_BEFORE_JULY_2026;

  let nationalPension = 0;
  let healthInsurance = 0;
  let longTermCareInsurance = 0;
  let employmentInsurance = 0;

  if (applyInsurance) {
    if (taxableMonthly > 0) {
      const pensionBase = clamp(taxableMonthly, pensionLower, pensionUpper);
      nationalPension = roundWon(pensionBase * Policy2026.NATIONAL_PENSION_EMPLOYEE_RATE);
      healthInsurance = roundWon(taxableMonthly * Policy2026.HEALTH_INSURANCE_EMPLOYEE_RATE);
      longTermCareInsurance = roundWon(healthInsurance * Policy2026.LONG_TERM_CARE_RATE_OF_HEALTH);
      employmentInsurance = roundWon(taxableMonthly * Policy2026.EMPLOYMENT_INSURANCE_EMPLOYEE_RATE);
    }
  }

  const familyCount = Math.max(1, Number(dependents) || 1);
  const rawChildren = Math.max(0, Number(children) || 0);
  const eligibleChildren = Math.min(rawChildren, Math.max(0, familyCount - 1));

  const annualTaxableGross = taxableMonthly * 12.0;
  const annualEmployeePension = nationalPension * 12.0;

  let annualIncomeTax = 0;
  if (annualTaxableGross > 0) {
    const eDeduction = earnedIncomeDeduction(annualTaxableGross);
    const basicDeduction = familyCount * Policy2026.BASIC_PERSONAL_DEDUCTION_PER_PERSON;
    const specialDeduction = proxySpecialDeduction(annualTaxableGross, familyCount);
    const taxBase = Math.max(0, annualTaxableGross - eDeduction - basicDeduction - specialDeduction - annualEmployeePension);
    const calculatedTax = progressiveIncomeTax(taxBase);
    const taxCredit = earnedIncomeTaxCredit(calculatedTax, annualTaxableGross);
    annualIncomeTax = Math.max(0, calculatedTax - taxCredit);
  }

  const incomeTaxBeforeChildCredit = roundWon(annualIncomeTax / 12.0);
  let childCredit = 0;
  if (eligibleChildren === 1) childCredit = Policy2026.WITHHOLDING_CHILD_TAX_CREDIT_ONE;
  else if (eligibleChildren >= 2) {
    childCredit = Policy2026.WITHHOLDING_CHILD_TAX_CREDIT_TWO + (eligibleChildren - 2) * Policy2026.WITHHOLDING_CHILD_TAX_CREDIT_ADDITIONAL;
  }

  const incomeTax = Math.max(0, incomeTaxBeforeChildCredit - childCredit);
  const localIncomeTax = roundWon(incomeTax * 0.1);
  const totalDeduction = nationalPension + healthInsurance + longTermCareInsurance + employmentInsurance + incomeTax + localIncomeTax;
  const netMonthly = Math.max(0, grossMonthly - totalDeduction);

  return {
    grossMonthly,
    nationalPension,
    healthInsurance,
    longTermCareInsurance,
    employmentInsurance,
    incomeTax,
    localIncomeTax,
    totalDeduction,
    netMonthly,
    netAnnual: netMonthly * 12.0
  };
}

/* ----------------------------------------------------
   7. 퇴직금 계산기
---------------------------------------------------- */
export function calculateSeverance({
  startDate,
  endDate,
  totalWageForLastThreeMonths,
  annualBonus,
  annualLeaveAllowance,
  ordinaryDailyWage
}) {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const msPerDay = 1000 * 60 * 60 * 24;

  const serviceDays = Math.max(0, Math.round((end.getTime() - start.getTime()) / msPerDay) + 1);

  // 3개월 전 날짜 계산
  const periodStart = new Date(end);
  periodStart.setMonth(periodStart.getMonth() - 3);
  const calculationPeriodDays = Math.max(1, Math.round((end.getTime() - periodStart.getTime()) / msPerDay));

  const bonusIncluded = (Number(annualBonus) || 0) * 3.0 / 12.0;
  const leaveIncluded = (Number(annualLeaveAllowance) || 0) * 3.0 / 12.0;
  const threeMonthTotal = (Number(totalWageForLastThreeMonths) || 0) + bonusIncluded + leaveIncluded;

  const averageDailyWage = threeMonthTotal / calculationPeriodDays;
  const ordDailyWage = Math.max(0, Number(ordinaryDailyWage) || 0);
  const appliedDailyWage = Math.max(averageDailyWage, ordDailyWage);
  const severance = serviceDays < 365 ? 0.0 : (appliedDailyWage * 30.0 * serviceDays / 365.0);

  return {
    serviceDays,
    calculationPeriodDays,
    averageDailyWage,
    ordinaryDailyWage: ordDailyWage,
    appliedDailyWage,
    severance
  };
}

/* ----------------------------------------------------
   8. 연차수당 계산기
---------------------------------------------------- */
export function calculateAnnualLeave({
  startDate,
  calculationDate,
  dailyOrdinaryWage,
  usedLeaveDays
}) {
  const start = new Date(startDate);
  const calc = new Date(calculationDate);

  let months = (calc.getFullYear() - start.getFullYear()) * 12 + (calc.getMonth() - start.getMonth());
  if (calc.getDate() < start.getDate()) {
    months -= 1;
  }
  months = Math.max(0, months);

  let generated = 0;
  if (months < 12) {
    generated = Math.min(11, months);
  } else {
    const years = Math.floor(months / 12);
    const additional = Math.max(0, Math.floor((years - 1) / 2));
    generated = Math.min(25, 15 + additional);
  }

  const used = Math.max(0, Number(usedLeaveDays) || 0);
  const remaining = Math.max(0, generated - used);
  const allowance = remaining * (Number(dailyOrdinaryWage) || 0);

  return {
    serviceMonths: months,
    generatedLeaveDays: generated,
    usedLeaveDays: used,
    remainingLeaveDays: remaining,
    estimatedAllowance: allowance
  };
}

/* ----------------------------------------------------
   9. 적정주가 계산기
---------------------------------------------------- */
export function calculateFairValue({ eps, targetPer, growthRate, discountRate, safetyMargin }) {
  const base = (Number(eps) || 0) * (Number(targetPer) || 0);
  const growthAdjusted = base * (1 + (Number(growthRate) || 0) / 100.0) / (1 + (Number(discountRate) || 0) / 100.0);
  const safeBuy = growthAdjusted * (1 - (Number(safetyMargin) || 0) / 100.0);

  return {
    baseFairValue: base,
    growthAdjustedFairValue: growthAdjusted,
    safeBuyPrice: safeBuy,
    safeBuyLow: safeBuy * 0.85,
    safeBuyTarget: safeBuy,
    safeBuyHigh: safeBuy * 1.15
  };
}

/* ----------------------------------------------------
   10. 환율 계산기
---------------------------------------------------- */
export function calculateExchange({ amount, exchangeRate, feeRate }) {
  const beforeFee = (Number(amount) || 0) * (Number(exchangeRate) || 0);
  const fee = beforeFee * (Number(feeRate) || 0) / 100.0;
  const afterFee = Math.max(0, beforeFee - fee);

  return {
    convertedBeforeFee: beforeFee,
    feeAmount: fee,
    convertedAfterFee: afterFee
  };
}

/* ----------------------------------------------------
   11. 전기요금 계산기
---------------------------------------------------- */
export function calculateElectricityBill({ usageKwh, season }) {
  const usage = Math.max(0, Number(usageKwh) || 0);
  let tier1Limit = 200, tier2Limit = 400, base1 = 910, base2 = 1600, base3 = 7300;
  let rate1 = 120.0, rate2 = 214.6, rate3 = 307.3;

  if (season === 'SUMMER') {
    tier1Limit = 300;
    tier2Limit = 450;
  }

  const tier1Usage = Math.min(usage, tier1Limit);
  const tier2Usage = Math.min(Math.max(usage - tier1Limit, 0), tier2Limit - tier1Limit);
  const tier3Usage = Math.max(usage - tier2Limit, 0);

  const energyCharge = tier1Usage * rate1 + tier2Usage * rate2 + tier3Usage * rate3;
  const baseFee = usage <= tier1Limit ? base1 : usage <= tier2Limit ? base2 : base3;
  const climateCharge = usage * 9.0;
  const fuelAdjustment = usage * 5.0;
  const subtotal = baseFee + energyCharge + climateCharge + fuelAdjustment;
  const vat = Math.round(subtotal * 0.1);
  const fund = Math.floor(subtotal * 0.037 / 10.0) * 10.0;
  const total = subtotal + vat + fund;
  const avgUnitPrice = usage > 0 ? total / usage : 0.0;

  return {
    usageKwh: usage,
    baseFee,
    energyCharge,
    climateCharge,
    fuelAdjustment,
    subtotal,
    vat,
    electricFund: fund,
    totalBill: total,
    averageUnitPrice: avgUnitPrice
  };
}

/* ----------------------------------------------------
   12. 에어컨 전기세 계산기
---------------------------------------------------- */
export function calculateAirConditionerCost({
  powerWatts,
  standbyWatts,
  hoursPerDay,
  daysPerMonth,
  electricityRatePerKwh
}) {
  const pWatts = Number(powerWatts) || 0;
  const sWatts = Number(standbyWatts) || 0;
  const hPerDay = Number(hoursPerDay) || 0;
  const dPerMonth = Number(daysPerMonth) || 0;
  const rate = Number(electricityRatePerKwh) || 0;

  const activeUsageKwh = (pWatts / 1000.0) * hPerDay * dPerMonth;
  const standbyUsageKwh = (sWatts / 1000.0) * 24.0 * dPerMonth;
  const totalUsageKwh = activeUsageKwh + standbyUsageKwh;
  const estimatedCost = totalUsageKwh * rate;
  const dailyCost = dPerMonth > 0 ? estimatedCost / dPerMonth : 0;
  const totalHours = hPerDay * dPerMonth;
  const hourlyCost = totalHours > 0 ? estimatedCost / totalHours : 0;

  return {
    activeUsageKwh,
    standbyUsageKwh,
    totalUsageKwh,
    estimatedMonthlyCost: estimatedCost,
    dailyCost,
    hourlyCost
  };
}

/* ----------------------------------------------------
   13. 자동차 유지비 계산기
---------------------------------------------------- */
export function calculateCarMaintenance({
  monthlyDistanceKm,
  fuelEfficiencyKmPerLiter,
  fuelPricePerLiter,
  parkingFeeMonthly,
  insuranceAnnual,
  taxAnnual,
  installmentMonthly,
  maintenanceAnnual,
  tollMonthly
}) {
  const dist = Number(monthlyDistanceKm) || 0;
  const eff = Number(fuelEfficiencyKmPerLiter) || 1;
  const fuelP = Number(fuelPricePerLiter) || 0;

  const fuelCostMonthly = eff > 0 ? (dist / eff) * fuelP : 0;
  const fixedCostMonthly = (Number(parkingFeeMonthly) || 0)
    + (Number(insuranceAnnual) || 0) / 12.0
    + (Number(taxAnnual) || 0) / 12.0
    + (Number(installmentMonthly) || 0);

  const variableCostMonthly = fuelCostMonthly
    + (Number(maintenanceAnnual) || 0) / 12.0
    + (Number(tollMonthly) || 0);

  const totalCostMonthly = fixedCostMonthly + variableCostMonthly;
  const totalCostAnnual = totalCostMonthly * 12.0;
  const costPerKm = dist > 0 ? totalCostMonthly / dist : 0.0;

  return {
    fuelCostMonthly,
    fixedCostMonthly,
    variableCostMonthly,
    totalCostMonthly,
    totalCostAnnual,
    costPerKm
  };
}

/* ----------------------------------------------------
   14. 월 생활비 계산기
---------------------------------------------------- */
export function calculateMonthlyBudget({
  monthlyIncome,
  housing,
  communication,
  insurance,
  education,
  subscriptions,
  food,
  transport,
  leisure,
  other,
  savingsGoal
}) {
  const income = Number(monthlyIncome) || 0;
  const fixedExpenses = (Number(housing) || 0)
    + (Number(communication) || 0)
    + (Number(insurance) || 0)
    + (Number(education) || 0)
    + (Number(subscriptions) || 0);

  const variableExpenses = (Number(food) || 0)
    + (Number(transport) || 0)
    + (Number(leisure) || 0)
    + (Number(other) || 0);

  const totalExpenses = fixedExpenses + variableExpenses;
  const remainingAfterExpenses = income - totalExpenses;
  const savings = Number(savingsGoal) || 0;
  const remainingAfterSavingsGoal = remainingAfterExpenses - savings;
  const expenseRatio = income > 0 ? (totalExpenses / income) * 100.0 : 0.0;
  const savingsGoalRatio = income > 0 ? (savings / income) * 100.0 : 0.0;

  return {
    fixedExpenses,
    variableExpenses,
    totalExpenses,
    remainingAfterExpenses,
    remainingAfterSavingsGoal,
    expenseRatio,
    savingsGoalRatio
  };
}

/* ----------------------------------------------------
   15. 해외주식 세금 계산기
---------------------------------------------------- */
export function calculateOverseasStockTax({
  buyAmountForeign,
  buyExchangeRate,
  sellAmountForeign,
  sellExchangeRate,
  feeKrw,
  applyBasicDeduction,
  basicDeductionKrw,
  capitalGainsTaxRate,
  dividendForeign,
  dividendExchangeRate,
  dividendTaxRate
}) {
  const buyKrw = (Number(buyAmountForeign) || 0) * (Number(buyExchangeRate) || 0);
  const sellKrw = (Number(sellAmountForeign) || 0) * (Number(sellExchangeRate) || 0);
  const capitalGain = sellKrw - buyKrw - (Number(feeKrw) || 0);

  const deduction = applyBasicDeduction ? (Number(basicDeductionKrw) || 0) : 0.0;
  const taxableCapitalGain = Math.max(0, capitalGain - deduction);
  const capitalTax = taxableCapitalGain * (Number(capitalGainsTaxRate) || 0) / 100.0;

  const dividendKrw = (Number(dividendForeign) || 0) * (Number(dividendExchangeRate) || 0);
  const dividendTax = dividendKrw * (Number(dividendTaxRate) || 0) / 100.0;

  const totalTax = capitalTax + dividendTax;
  const afterTaxProfit = capitalGain + dividendKrw - totalTax;

  return {
    buyKrw,
    sellKrw,
    capitalGain,
    taxableCapitalGain,
    capitalTax,
    dividendKrw,
    dividendTax,
    totalTax,
    afterTaxProfit
  };
}
