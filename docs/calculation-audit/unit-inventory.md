# Unit Inventory

Audit date: 2026-07-28 Asia/Seoul

## Common Rules

- Internal KRW amounts are stored and calculated in won.
- `만원` and `억원` appear only in display/example labels, not as DTO units.
- Percent inputs are entered as percent values. Example: `4.5` means 4.5% and is converted once to `0.045` or `0.00375` monthly rate where needed.
- Exchange quotes use `1 foreign currency unit = X KRW`.
- Electricity usage is kWh; device power is W; fuel efficiency is km/L.
- Result labels must not append `원` to non-KRW currencies or unitless counts/ratios.

## Route Units

| Route | Input fields and units | Result fields and units |
|---|---|---|
| `/loan-interest-calculator` | `principal` KRW, `annualRate` percent/year, `years` years, `repaymentType` enum | monthly payments KRW, interest KRW, total payment KRW, schedule month/count/KRW |
| `/stock-average-calculator` | shares count, average/additional price KRW/share | investment KRW, total shares count, average price KRW/share, change KRW and percent |
| `/loan-refinance-calculator` | balance KRW, current/new annual rates percent/year, periods years, penalty percent, costs KRW, refinance date ISO date | monthly payments KRW, interest KRW, costs KRW, savings KRW, break-even months, break-even date |
| `/stock-tax-calculator` | none | none |
| `/domestic-stock-tax-calculator` | buy/sell/fee KRW, transaction/capital-gains tax rates percent | signed gain/loss KRW, transaction tax KRW, optional capital gains tax KRW, total tax KRW, signed after-tax gain/loss KRW |
| `/overseas-stock-tax-calculator` | buy/sell/dividend foreign amount, exchange rates KRW per 1 foreign unit, fee/deduction KRW, tax rates percent | buy/sell/dividend KRW equivalents, capital gain KRW, taxable gain KRW, tax KRW, after-tax profit KRW |
| `/mortgage-monthly-payment-calculator` | house price/cash/loan/income/debt KRW, annual rate percent/year, period years, LTV percent | monthly payment KRW, interest KRW, required equity KRW, LTV loan cap KRW, burden rates percent, risk label |
| `/dividend-calculator` | shares count, dividend per share KRW/share, period enum, tax rate percent | one-time/monthly/annual dividend KRW, payments per year count |
| `/annual-salary-net-calculator` | annual salary KRW/year, tax-free meal KRW/month, dependents count, children count, monthly bonus KRW/month, boolean flags | monthly gross KRW, deductions KRW/month, net monthly KRW, net annual KRW |
| `/salary-calculator` | amount KRW/month or KRW/year depending on `incomeType`, tax-free amount KRW/month, dependents/children counts, insurance boolean | monthly gross KRW, deductions KRW/month, net monthly KRW, net annual KRW |
| `/severance-pay-calculator` | dates ISO date, last-three-month wage KRW, annual bonus KRW/year, annual leave allowance KRW/year, ordinary daily wage KRW/day | service days count, calculation period days count, daily wages KRW/day, severance KRW |
| `/annual-leave-pay-calculator` | dates ISO date, used leave days count, daily ordinary wage KRW/day | service months count, generated/used/remaining leave days count, allowance KRW |
| `/fair-value-calculator` | EPS KRW/share, target PER multiplier, growth/discount/safety margin percent | fair-value and scenario prices KRW/share |
| `/exchange-calculator` | amount in source currency, source/target currency code, primary exchange rate KRW per 1 applicable foreign unit, target exchange rate KRW per 1 target foreign unit for foreign-to-foreign, fee rate percent | before/after amount in target currency, fee in target currency, before/fee/after KRW equivalents, fee currency code |
| `/electricity-bill-calculator` | usage and previous usage kWh/month, season enum | base/energy/climate/fuel/VAT/fund/total KRW, average unit price KRW/kWh, usage delta kWh |
| `/air-conditioner-electricity-calculator` | power W, hours/day, days/month, load factor 0-1, rate KRW/kWh, standby W, existing household usage kWh, season enum | active/standby/total usage kWh, standalone cost KRW, daily/hourly averages KRW, household usage kWh, base/total/incremental bills KRW |
| `/car-maintenance-calculator` | monthly distance km, efficiency km/L, fuel price KRW/L, parking/toll/installment KRW/month, insurance/tax/maintenance KRW/year | monthly fuel/fixed/variable/total KRW, annual total KRW, cost per km KRW/km |
| `/monthly-budget-calculator` | monthly income and expense categories KRW/month | fixed/variable/total expenses KRW/month, balances KRW/month, ratios percent |

## Unit-Specific Fix Notes

- `ExchangeResult` now carries `CurrencyCode` fields so foreign amounts are labeled with their actual currency instead of assuming KRW.
- `AirConditionerCostRequest.loadFactor` is a unitless multiplier, not a percent. Input label explicitly says `0~1`.
- `OverseasStockTaxRequest` exchange-rate fields now reject 0 and use labels `1 외화 = 원`.
- `formatWon()` in JS no longer clamps negative values, so signed KRW profit/loss remains visible.
