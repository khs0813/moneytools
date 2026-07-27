# Formula Inventory

Audit date: 2026-07-28 Asia/Seoul

## Summary of Fixes Applied

- `/exchange-calculator`: replaced direction-agnostic `amount * exchangeRate` with currency-direction logic using `1 foreign = X KRW`; added same-currency no-op, foreign-to-foreign KRW cross rate, fee currency, KRW equivalent outputs, and positive-rate validation.
- `/air-conditioner-electricity-calculator`: replaced 24-hour standby calculation with `standbyHours = 24 - activeHours`; added `loadFactor` for inverter estimates and reused the same service for comparison rows.
- `/electricity-bill-calculator`: changed electric power industry fund from 3.7% to 2.7% for 2026 and made 10-won final floor explicit.
- `/salary-calculator`, `/annual-salary-net-calculator`: updated 2026-03-01 child withholding credits in Java and duplicated JS constants.
- `/loan-refinance-calculator`: connected `refinanceDate` to `breakEvenDate`.
- `/domestic-stock-tax-calculator`: changed loss handling from clamping to 0 to signed profit/loss display.
- `/overseas-stock-tax-calculator`: blocked zero exchange rates and clarified KRW-per-foreign labels.

## Route Formulas

### `/loan-interest-calculator`

- Service: `LoanCalculatorService`
- Formula:
  - `monthlyRate = annualRatePercent / 100 / 12`
  - `months = years * 12`
  - Equal payment: `principal * r * (1 + r)^n / ((1 + r)^n - 1)`
  - Equal principal: fixed principal `principal / months`; monthly interest on remaining balance
  - Bullet: monthly interest only, principal at final month
- Rounding policy: service returns double; Thymeleaf displays KRW with 0 decimal places.
- Policy effective date: financial math, no statutory policy.

### `/stock-average-calculator`

- Service: `StockAverageCalculatorService`
- Formula:
  - `currentInvestment = currentShares * currentAveragePrice`
  - `additionalInvestment = additionalShares * additionalPrice`
  - `newAveragePrice = (currentInvestment + additionalInvestment) / totalShares`
  - `averagePriceChangeRate = averagePriceChange / currentAveragePrice * 100`
- Rounding policy: service returns double; display rounds price to 2 decimals.
- Policy effective date: financial math, no statutory policy.

### `/loan-refinance-calculator`

- Service: `LoanRefinanceCalculatorService`
- Formula:
  - Current/new monthly payment uses same annuity or equal-principal first-payment logic.
  - `totalInterestSavings = currentTotalInterest - newTotalInterest`
  - `prepaymentPenalty = currentBalance * prepaymentPenaltyRatePercent / 100`
  - `totalSwitchingCost = prepaymentPenalty + additionalCost`
  - `netSavings = totalInterestSavings - totalSwitchingCost`
  - `breakEvenMonths = ceil(totalSwitchingCost / monthlySavings)` if monthly savings is positive
  - `breakEvenDate = refinanceDate.plusMonths(breakEvenMonths)` after this task.
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: product math, no universal statutory policy.

### `/stock-tax-calculator`

- Service: none
- Formula: none. This is a domestic/overseas stock tax hub.
- Rounding policy: none.
- Policy effective date: 2026 explanatory content, no executable calculation.

### `/domestic-stock-tax-calculator`

- Service/function: `renderDomesticStockTaxResult()` in `calculator.js`
- Formula:
  - `capitalGain = sellAmount - buyAmount - feeAmount`
  - `taxableCapitalGain = max(0, capitalGain)`
  - `transactionTax = sellAmount * transactionTaxRate`
  - `capitalGainsTax = applyCapitalGainsTax ? taxableCapitalGain * capitalGainsTaxRate : 0`
  - `afterTaxProfit = capitalGain - transactionTax - capitalGainsTax`
- Rounding policy: JS `formatWon()` rounds to nearest KRW and now preserves negative values.
- Policy effective date: default transaction tax rate is a user-editable estimate; user must verify current market/taxpayer status.

### `/overseas-stock-tax-calculator`

- Service: `OverseasStockTaxCalculatorService`
- Formula:
  - `buyKrw = buyAmountForeign * buyExchangeRateKrwPerForeign`
  - `sellKrw = sellAmountForeign * sellExchangeRateKrwPerForeign`
  - `capitalGain = sellKrw - buyKrw - feeKrw`
  - `taxableCapitalGain = max(0, capitalGain - basicDeductionKrw)`
  - `capitalTax = taxableCapitalGain * capitalGainsTaxRatePercent / 100`
  - `dividendKrw = dividendForeign * dividendExchangeRateKrwPerForeign`
  - `dividendTax = dividendKrw * dividendTaxRatePercent / 100`
  - `afterTaxProfit = capitalGain + dividendKrw - capitalTax - dividendTax`
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: 2026 defaults in `Policy2026`; overseas stock basic deduction KRW 2,500,000 and default capital gains tax rate 22%.

### `/mortgage-monthly-payment-calculator`

- Service: `MortgageCalculatorService`
- Formula:
  - Monthly payment uses repayment type formula matching loan interest calculator.
  - `requiredEquity = housePrice - expectedLoanAmount`
  - `cashShortfall = max(0, requiredEquity - cashOnHand)`
  - `maxLoanByLtv = housePrice * ltvRatioPercent / 100`
  - `monthlyBurdenRate = monthlyPayment / (annualIncome / 12) * 100`
  - Existing debt version adds `existingMonthlyDebtPayment`.
- Rounding policy: service returns double; display rounds KRW to 0 decimals and burden rates to 1 decimal.
- Policy effective date: LTV/affordability estimate only; no binding DSR regulation engine.

### `/dividend-calculator`

- Service: `DividendCalculatorService`
- Formula:
  - `paymentsPerYear`: monthly 12, quarterly 4, semi-annual 2, annual 1
  - `oneTimeGross = shares * dividendPerShare`
  - `oneTimeNet = oneTimeGross * (1 - taxRatePercent / 100)` if tax applied
  - Annual and monthly values are period conversions.
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: tax rate is user input; default 15.4% is a common Korean withholding assumption.

### `/annual-salary-net-calculator`

- Service/function: `calculateAnnualSalaryNetPay()` in `calculator.js`
- Formula:
  - `baseMonthlySalary = annualSalary / 13` if retirement included, else `/ 12`
  - `grossMonthly = baseMonthlySalary + monthlyBonus`
  - `taxableMonthlyIncome = max(0, grossMonthly - monthlyTaxFreeMeal)`
  - Insurance and estimated income tax mirror `SalaryCalculatorService`.
- Rounding policy: JS `roundWon()` for deductions; result display rounds to KRW.
- Policy effective date: 2026 insurance rates, 2026-03-01 child withholding credits.
- Duplication note: this calculator duplicates server salary policy constants in JS.

### `/salary-calculator`

- Service: `SalaryCalculatorService`
- Formula:
  - Annual input divides amount by 12; monthly input uses amount as monthly gross.
  - Taxable monthly amount subtracts monthly tax-free amount.
  - National pension clamps taxable monthly amount to the applicable 2026 lower/upper bound.
  - Health, long-term care, employment insurance use `Policy2026`.
  - Estimated income tax uses project approximation of earned income deduction, basic deduction, special deduction proxy, progressive tax, earned income tax credit, and child withholding credit.
- Rounding policy: `RoundingPolicy.roundToWon()` for monthly deductions and local income tax.
- Policy effective date: national pension bounds change on 2026-07-01; withholding child credit effective 2026-03-01.

### `/severance-pay-calculator`

- Service: `SeveranceCalculatorService`
- Formula:
  - `serviceDays = daysBetween(startDate, endDate) + 1`
  - `calculationPeriodDays = daysBetween(endDate.minusMonths(3), endDate)`
  - `threeMonthTotal = totalWageForLastThreeMonths + annualBonus * 3/12 + annualLeaveAllowance * 3/12`
  - `averageDailyWage = threeMonthTotal / calculationPeriodDays`
  - `appliedDailyWage = max(averageDailyWage, ordinaryDailyWage)`
  - `severance = appliedDailyWage * 30 * serviceDays / 365` if serviceDays >= 365
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: statutory severance structure, simplified input model.

### `/annual-leave-pay-calculator`

- Service: `AnnualLeaveCalculatorService`
- Formula:
  - `serviceMonths = monthsBetween(startDate, calculationDate)`
  - Under 12 months: generated leave days are capped at 11.
  - 12 months or more: starts at 15 days, adds 1 day every 2 service years after the first year, capped at 25.
  - `remaining = max(0, generated - used)`
  - `allowance = remaining * dailyOrdinaryWage`
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: simplified annual leave accrual model.

### `/fair-value-calculator`

- Service: `FairValueCalculatorService`
- Formula:
  - `baseFairValue = eps * targetPer`
  - `growthAdjustedFairValue = baseFairValue * (1 + growthRatePercent/100) / (1 + discountRatePercent/100)`
  - `safeBuyPrice = growthAdjustedFairValue * (1 - safetyMarginPercent/100)`
  - Scenario values are `safeBuyPrice * 0.85`, `safeBuyPrice`, `safeBuyPrice * 1.15`.
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: valuation estimate, no statutory policy.

### `/exchange-calculator`

- Service: `ExchangeCalculatorService`
- Quote standard: `1 foreign currency unit = X KRW`.
- Formula:
  - Same currency: no conversion, no fee.
  - Foreign to KRW: `resultKrw = foreignAmount * krwPerForeign`.
  - KRW to foreign: `resultForeign = krwAmount / krwPerForeign`.
  - Foreign to foreign: `sourceKrw = sourceForeignAmount * sourceKrwPerForeign`; `targetForeign = sourceKrw / targetKrwPerForeign`.
  - Fee is calculated in the result/target currency and also reported as KRW equivalent.
- Rounding policy: service uses BigDecimal internally, returns double; display shows 2 decimals for target currency and 0 decimals for KRW equivalents.
- Policy effective date: market quote convention, no statutory policy.

### `/electricity-bill-calculator`

- Service: `ElectricityBillCalculatorService`
- Formula:
  - Tiered energy charge from season policy.
  - `climateCharge = usageKwh * 9.0`
  - `fuelAdjustment = usageKwh * 5.0`
  - `subtotal = baseFee + energyCharge + climateCharge + fuelAdjustment`
  - `vat = round(subtotal * 10%)`
  - `industryFund = floorToTenWon(subtotal * 2.7%)`
  - `totalBill = floorToTenWon(subtotal + vat + industryFund)`
- Rounding policy: `RoundingPolicy.roundToWon()` for VAT and `floorToTenWon()` for fund/final bill.
- Policy effective date: electric power industry fund 2.7% from 2025-07-01 onward; used for 2026 estimates.

### `/air-conditioner-electricity-calculator`

- Service: `AirConditionerCostCalculatorService`
- Formula:
  - `activeHours = clamp(hoursPerDay, 0, 24)`
  - `standbyHours = 24 - activeHours`
  - `activeKwh = watts / 1000 * activeHours * days * loadFactor`
  - `standbyKwh = standbyWatts / 1000 * standbyHours * days`
  - `additionalKwh = activeKwh + standbyKwh`
  - Standalone cost uses `additionalKwh * electricityRatePerKwh`.
  - Household incremental cost uses `ElectricityBillCalculatorService` for base and base-plus-air-conditioner usage.
- Rounding policy: service returns double; display rounds kWh to 1 decimal and KRW to 0 decimals.
- Policy effective date: electricity bill policy inherited from `/electricity-bill-calculator`.

### `/car-maintenance-calculator`

- Service: `CarMaintenanceCalculatorService`
- Formula:
  - `fuelCostMonthly = monthlyDistanceKm / fuelEfficiencyKmPerLiter * fuelPricePerLiter`
  - `fixedCostMonthly = parking + insuranceAnnual/12 + taxAnnual/12 + installment`
  - `variableCostMonthly = fuelCost + maintenanceAnnual/12 + tollMonthly`
  - Totals and `costPerKm = totalCostMonthly / monthlyDistanceKm`.
- Rounding policy: service returns double; display rounds KRW to 0 decimals.
- Policy effective date: personal budget estimate, no statutory policy.

### `/monthly-budget-calculator`

- Service: `MonthlyBudgetCalculatorService`
- Formula:
  - Fixed expenses: housing, communication, insurance, education, subscriptions.
  - Variable expenses: food, transport, leisure, other.
  - `totalExpenses = fixed + variable`
  - `remainingAfterExpenses = monthlyIncome - totalExpenses`
  - `remainingAfterSavingsGoal = remainingAfterExpenses - savingsGoal`
  - Ratios divide by monthly income if positive.
- Rounding policy: service returns double; display rounds KRW to 0 decimals and ratios to 1 decimal.
- Policy effective date: personal budget estimate, no statutory policy.
