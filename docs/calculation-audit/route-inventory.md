# Calculation Route Inventory

Audit date: 2026-07-28 Asia/Seoul

## Build and Runtime

- Spring Boot: 3.5.14 (`spring-boot-starter-parent`)
- Java source target: 17 (`<java.version>17</java.version>`)
- Build tool: Maven (`pom.xml`)
- Runtime image: `eclipse-temurin:17-jre`
- Docker build command: `mvn -q -DskipTests package`
- Local production-equivalent build command: `mvn -q -DskipTests package`

## Calculation Split

- Server-side Spring MVC + Thymeleaf calculators: loan interest, stock average, loan refinance, mortgage, salary, severance, annual leave, exchange, overseas stock tax, electricity bill, air conditioner electricity, car maintenance, monthly budget, dividend, fair value.
- JavaScript-only calculators: annual salary net (`data-annual-salary-calculator`), domestic stock tax (`data-domestic-stock-tax-calculator`).
- Hub-only page: stock tax calculator (`/stock-tax-calculator`) links users to domestic and overseas calculators and has no executable formula.
- Duplicated calculation: annual salary net JS duplicates the salary service's 2026 insurance/tax approximation constants and formula.
- Shared browser script: `src/main/resources/static/js/calculator.js` handles input formatting, validation, result scrolling, CSV export, analytics events, annual salary calculation, and domestic stock tax calculation.

## Shared Files

- Policy constants: `src/main/java/com/example/moneytools/policy/Policy2026.java`
- Common unit types added in this task: `src/main/java/com/example/moneytools/unit/*`
- Rounding policy added in this task: `src/main/java/com/example/moneytools/util/RoundingPolicy.java`
- Number parsing/display utilities: `NumberTextUtils`, `NumericBindingAdvice`, `NumberViewFormatter`, `InputFieldValueFormatter`
- Tests: JUnit 5 under `src/test/java`

## Routes

| Route | Controller | Request DTO | Result DTO | Service/function | Template/script | Calculation owner |
|---|---|---|---|---|---|---|
| `/loan-interest-calculator` | `CalculatorController` GET/POST | `LoanRequest` | `LoanResult`, `LoanPaymentRow` | `LoanCalculatorService` | `loan-interest-calculator.html` | Server |
| `/stock-average-calculator` | `CalculatorController` GET/POST | `StockAverageRequest` | `StockAverageResult` | `StockAverageCalculatorService` | `stock-average-calculator.html` | Server |
| `/loan-refinance-calculator` | `CalculatorController` GET/POST | `LoanRefinanceRequest` | `LoanRefinanceResult` | `LoanRefinanceCalculatorService` | `loan-refinance-calculator.html` | Server |
| `/stock-tax-calculator` | `PageController` GET | none | none | none | `stock-tax-calculator.html` | Hub only |
| `/domestic-stock-tax-calculator` | `CalculatorController` GET | none | none | `renderDomesticStockTaxResult()` | `domestic-stock-tax-calculator.html`, `calculator.js` | Browser |
| `/overseas-stock-tax-calculator` | `CalculatorController` GET/POST | `OverseasStockTaxRequest` | `OverseasStockTaxResult` | `OverseasStockTaxCalculatorService` | `overseas-stock-tax-calculator.html` | Server |
| `/mortgage-monthly-payment-calculator` | `CalculatorController` GET/POST | `MortgageRequest` | `MortgageResult` | `MortgageCalculatorService` | `mortgage-monthly-payment-calculator.html` | Server |
| `/dividend-calculator` | `CalculatorController` GET/POST | `DividendRequest` | `DividendResult` | `DividendCalculatorService` | `dividend-calculator.html` | Server |
| `/annual-salary-net-calculator` | `CalculatorController` GET | none | none | `calculateAnnualSalaryNetPay()` | `annual-salary-net-calculator.html`, `calculator.js` | Browser |
| `/salary-calculator` | `CalculatorController` GET/POST | `SalaryRequest` | `SalaryResult` | `SalaryCalculatorService` | `salary-calculator.html` | Server |
| `/severance-pay-calculator` | `CalculatorController` GET/POST | `SeveranceRequest` | `SeveranceResult` | `SeveranceCalculatorService` | `severance-pay-calculator.html` | Server |
| `/annual-leave-pay-calculator` | `CalculatorController` GET/POST | `AnnualLeaveRequest` | `AnnualLeaveResult` | `AnnualLeaveCalculatorService` | `annual-leave-pay-calculator.html` | Server |
| `/fair-value-calculator` | `CalculatorController` GET/POST | `FairValueRequest` | `FairValueResult` | `FairValueCalculatorService` | `fair-value-calculator.html` | Server |
| `/exchange-calculator` | `CalculatorController` GET/POST | `ExchangeRequest` | `ExchangeResult` | `ExchangeCalculatorService` | `exchange-calculator.html` | Server |
| `/electricity-bill-calculator` | `CalculatorController` GET/POST | `ElectricityBillRequest` | `ElectricityBillResult` | `ElectricityBillCalculatorService` | `electricity-bill-calculator.html` | Server |
| `/air-conditioner-electricity-calculator` | `CalculatorController` GET/POST | `AirConditionerCostRequest` | `AirConditionerCostResult` | `AirConditionerCostCalculatorService` | `air-conditioner-electricity-calculator.html` | Server |
| `/car-maintenance-calculator` | `CalculatorController` GET/POST | `CarMaintenanceRequest` | `CarMaintenanceResult` | `CarMaintenanceCalculatorService` | `car-maintenance-calculator.html` | Server |
| `/monthly-budget-calculator` | `CalculatorController` GET/POST | `MonthlyBudgetRequest` | `MonthlyBudgetResult` | `MonthlyBudgetCalculatorService` | `monthly-budget-calculator.html` | Server |

## Protected Surfaces Not Changed

The audit intentionally did not change URL slugs, canonical generation, domain redirects, robots metadata, sitemap membership, site name, or AdFit placement/unit configuration.
