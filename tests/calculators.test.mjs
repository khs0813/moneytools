import test from 'node:test';
import assert from 'node:assert/strict';
import {
  calculateLoan,
  calculateStockAverage,
  calculateLoanRefinance,
  calculateMortgage,
  calculateDividend,
  calculateSalary,
  calculateSeverance,
  calculateAnnualLeave,
  calculateFairValue,
  calculateExchange,
  calculateElectricityBill,
  calculateAirConditionerCost,
  calculateCarMaintenance,
  calculateMonthlyBudget,
  calculateOverseasStockTax
} from '../src/main/resources/static/js/calc-engine.js';

test('dividendCalculatorReturnsAnnualNet', () => {
  const result = calculateDividend({
    shares: 10,
    dividendPerShare: 1000.0,
    period: 'QUARTERLY',
    taxApplied: true,
    taxRate: 15.4
  });

  assert.equal(result.paymentsPerYear, 4);
  assert.ok(result.annualNet > 0);
  assert.equal(result.gross, 10000);
  assert.equal(result.net, 8460);
  assert.equal(result.annualGross, 40000);
  assert.equal(result.annualNet, 33840);
});

test('loanCalculatorReturnsSchedule', () => {
  const result = calculateLoan({
    principal: 10_000_000,
    annualRate: 4.0,
    years: 1,
    repaymentType: 'EQUAL_PAYMENT'
  });

  assert.equal(result.totalMonths, 12);
  assert.equal(result.schedule.length, 12);
  assert.ok(result.totalPayment > 10_000_000);
});

test('stockAverageCalculatorReturnsNewAveragePrice', () => {
  const result = calculateStockAverage({
    currentShares: 100,
    currentAveragePrice: 50_000.0,
    additionalShares: 50,
    additionalPrice: 40_000.0
  });

  assert.equal(result.totalShares, 150);
  assert.equal(result.totalInvestment, 7_000_000.0);
  assert.ok(result.newAveragePrice >= 46666.66 && result.newAveragePrice <= 46666.67);
  assert.ok(result.averagePriceChange < 0.0);
});

test('loanCalculatorSupportsLargePrincipal', () => {
  const result = calculateLoan({
    principal: 20_000_000_000,
    annualRate: 3.8,
    years: 30,
    repaymentType: 'EQUAL_PAYMENT'
  });

  assert.equal(result.totalMonths, 360);
  assert.equal(result.schedule.length, 360);
  assert.ok(result.firstMonthlyPayment > 0.0);
  assert.ok(result.totalPayment > 20_000_000_000);
});

test('salaryCalculatorAppliesEligibleChildWithholdingCredit', () => {
  const baseResult = calculateSalary({
    incomeType: 'MONTHLY',
    amount: 8_000_000.0,
    taxFreeAmount: 0.0,
    dependents: 4,
    children: 0,
    applyInsurance: true
  });

  const childCreditResult = calculateSalary({
    incomeType: 'MONTHLY',
    amount: 8_000_000.0,
    taxFreeAmount: 0.0,
    dependents: 4,
    children: 2,
    applyInsurance: true
  });

  assert.equal(baseResult.incomeTax - childCreditResult.incomeTax, 45830.0);
  assert.ok(baseResult.localIncomeTax > childCreditResult.localIncomeTax);
});

test('mortgageCalculatorEqualPaymentAndBullet', () => {
  const eq = calculateMortgage({
    housePrice: 600_000_000,
    cashOnHand: 200_000_000,
    expectedLoanAmount: 400_000_000,
    annualRate: 4.0,
    years: 30,
    repaymentType: 'EQUAL_PAYMENT',
    ltvRatio: 70.0,
    annualIncome: 80_000_000,
    existingMonthlyDebtPayment: 0
  });

  assert.ok(eq.estimatedMonthlyPayment > 0);
  assert.equal(eq.requiredEquity, 200_000_000);
  assert.equal(eq.maxLoanByLtv, 420_000_000);
  assert.ok(['안전', '주의', '위험'].includes(eq.riskLabel));

  const bullet = calculateMortgage({
    housePrice: 600_000_000,
    cashOnHand: 200_000_000,
    expectedLoanAmount: 300_000_000,
    annualRate: 6.0,
    years: 10,
    repaymentType: 'BULLET',
    ltvRatio: 70.0,
    annualIncome: 80_000_000,
    existingMonthlyDebtPayment: 0
  });

  assert.equal(bullet.estimatedMonthlyPayment, 1_500_000.0);
  assert.equal(bullet.totalInterest, 180_000_000.0);
});

test('loanRefinanceRecommendations', () => {
  const favorable = calculateLoanRefinance({
    currentBalance: 200_000_000,
    currentAnnualRate: 6.0,
    currentRemainingYears: 20,
    newAnnualRate: 3.5,
    newYears: 20,
    repaymentType: 'EQUAL_PAYMENT',
    prepaymentPenaltyRate: 0.5,
    additionalCost: 300_000
  });

  assert.ok(favorable.monthlySavings > 0);
  assert.ok(favorable.netSavings > 0);
  assert.ok(['갈아타기 유리', '장기 보유 시 유리'].includes(favorable.recommendation));

  const unfavorable = calculateLoanRefinance({
    currentBalance: 100_000_000,
    currentAnnualRate: 3.0,
    currentRemainingYears: 10,
    newAnnualRate: 5.0,
    newYears: 10,
    repaymentType: 'EQUAL_PRINCIPAL',
    prepaymentPenaltyRate: 1.0,
    additionalCost: 500_000
  });

  assert.ok(unfavorable.netSavings <= 0);
  assert.equal(unfavorable.recommendation, '갈아타기 불리');
  assert.equal(unfavorable.recommendationLevel, 'danger');
});

test('electricityBillCalculation', () => {
  const summer = calculateElectricityBill({ usageKwh: 350, season: 'SUMMER' });
  assert.ok(summer.totalBill > 0);
  assert.equal(summer.baseFee, 1600); // 300초과 450이하 기본요금
  assert.ok(summer.vat > 0);
  assert.ok(summer.electricFund > 0);
});

test('severanceCalculation', () => {
  const result = calculateSeverance({
    startDate: '2023-01-01',
    endDate: '2025-12-31',
    totalWageForLastThreeMonths: 9_000_000,
    annualBonus: 4_000_000,
    annualLeaveAllowance: 1_000_000,
    ordinaryDailyWage: 100_000
  });

  assert.ok(result.serviceDays >= 1095);
  assert.ok(result.severance > 0);
});

test('annualLeaveCalculation', () => {
  const result = calculateAnnualLeave({
    startDate: '2023-01-01',
    calculationDate: '2025-01-01',
    dailyOrdinaryWage: 100_000,
    usedLeaveDays: 5
  });

  assert.equal(result.serviceMonths, 24);
  assert.equal(result.generatedLeaveDays, 15);
  assert.equal(result.remainingLeaveDays, 10);
  assert.equal(result.estimatedAllowance, 1_000_000);
});

test('fairValueCalculation', () => {
  const result = calculateFairValue({
    eps: 5000,
    targetPer: 15,
    growthRate: 10,
    discountRate: 8,
    safetyMargin: 20
  });

  assert.equal(result.baseFairValue, 75000);
  assert.ok(result.growthAdjustedFairValue > 75000);
  assert.ok(result.safeBuyPrice > 0);
  assert.ok(result.safeBuyLow < result.safeBuyPrice);
});

test('exchangeCalculation', () => {
  const result = calculateExchange({
    amount: 1000,
    exchangeRate: 1350,
    feeRate: 1.0
  });

  assert.equal(result.convertedBeforeFee, 1_350_000);
  assert.equal(result.feeAmount, 13_500);
  assert.equal(result.convertedAfterFee, 1_336_500);
});

test('airConditionerCostCalculation', () => {
  const result = calculateAirConditionerCost({
    powerWatts: 1500,
    standbyWatts: 5,
    hoursPerDay: 8,
    daysPerMonth: 30,
    electricityRatePerKwh: 200
  });

  assert.equal(result.activeUsageKwh, 360);
  assert.ok(result.totalUsageKwh > 360);
  assert.ok(result.estimatedMonthlyCost > 70000);
});

test('carMaintenanceCalculation', () => {
  const result = calculateCarMaintenance({
    monthlyDistanceKm: 1000,
    fuelEfficiencyKmPerLiter: 10,
    fuelPricePerLiter: 1700,
    parkingFeeMonthly: 50000,
    insuranceAnnual: 1200000,
    taxAnnual: 360000,
    installmentMonthly: 0,
    maintenanceAnnual: 600000,
    tollMonthly: 30000
  });

  assert.equal(result.fuelCostMonthly, 170000);
  assert.equal(result.fixedCostMonthly, 180000); // 50000 + 100000 + 30000
  assert.equal(result.variableCostMonthly, 250000); // 170000 + 50000 + 30000
  assert.equal(result.totalCostMonthly, 430000);
  assert.equal(result.totalCostAnnual, 5160000);
});

test('monthlyBudgetCalculation', () => {
  const result = calculateMonthlyBudget({
    monthlyIncome: 4000000,
    housing: 600000,
    communication: 100000,
    insurance: 200000,
    education: 0,
    subscriptions: 50000,
    food: 800000,
    transport: 200000,
    leisure: 300000,
    other: 150000,
    savingsGoal: 1000000
  });

  assert.equal(result.fixedExpenses, 950000);
  assert.equal(result.variableExpenses, 1450000);
  assert.equal(result.totalExpenses, 2400000);
  assert.equal(result.remainingAfterExpenses, 1600000);
  assert.equal(result.remainingAfterSavingsGoal, 600000);
  assert.equal(result.expenseRatio, 60.0);
  assert.equal(result.savingsGoalRatio, 25.0);
});

test('overseasStockTaxCalculation', () => {
  const result = calculateOverseasStockTax({
    buyAmountForeign: 10000,
    buyExchangeRate: 1300,
    sellAmountForeign: 15000,
    sellExchangeRate: 1400,
    feeKrw: 50000,
    applyBasicDeduction: true,
    basicDeductionKrw: 2500000,
    capitalGainsTaxRate: 22.0,
    dividendForeign: 500,
    dividendExchangeRate: 1400,
    dividendTaxRate: 15.4
  });

  assert.equal(result.buyKrw, 13000000);
  assert.equal(result.sellKrw, 21000000);
  assert.equal(result.capitalGain, 7950000); // 21M - 13M - 50k
  assert.equal(result.taxableCapitalGain, 5450000); // 7.95M - 2.5M
  assert.equal(result.capitalTax, 1199000); // 5.45M * 0.22
  assert.equal(result.dividendKrw, 700000);
  assert.equal(result.dividendTax, 107800); // 700k * 0.154
  assert.equal(result.totalTax, 1306800);
  assert.equal(result.afterTaxProfit, 7343200); // 7950000 + 700000 - 1306800
});

