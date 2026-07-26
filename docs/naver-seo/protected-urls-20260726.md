# Naver Top URL Protection List - 2026-07-26

The following rows are protected while the Naver CTR improvement work is in progress.

Do not change URL path, title, H1, canonical, robots/index state, 301 behavior, or sitemap inclusion without explicit operator approval.

| Page | Route | Title | H1 | Canonical | Robots | Sitemap | Naver clicks 90d | Naver impressions 90d | Naver CTR 90d |
|---|---|---|---|---|---|---|---:|---:|---:|
| 전기요금 계산기 | `/electricity-bill-calculator` | 전기요금 계산기 \| 가정용 누진요금 참고 계산 | 전기요금 계산기 | `https://www.moneycalculator.co.kr/electricity-bill-calculator` | index, follow | yes | 811 | 116544 | 0.7% |
| 국내주식 세금 계산기 | `/domestic-stock-tax-calculator` | 국내 주식 세금 계산기 \| 매도세금·증권거래세 계산 | 국내 주식 세금 계산기 | `https://www.moneycalculator.co.kr/domestic-stock-tax-calculator` | index, follow | yes | 453 | 16318 | 2.8% |
| 에어컨 전기세 계산기 | `/air-conditioner-electricity-calculator` | 에어컨 전기세 계산기 \| 하루 8시간 사용 전기요금 계산 | 에어컨 전기세 계산기 | `https://www.moneycalculator.co.kr/air-conditioner-electricity-calculator` | index, follow | yes | 391 | 24574 | 1.6% |
| 대출이자 계산기 | `/loan-interest-calculator` | 대출이자 계산기 \| 원리금균등·원금균등 계산 | 대출이자 계산기 | `https://www.moneycalculator.co.kr/loan-interest-calculator` | index, follow | yes | 365 | 72661 | 0.5% |
| 연봉 실수령액 계산기 | `/annual-salary-net-calculator` | 2026 연봉 실수령액 계산기 \| 세후 월급 계산 | 연봉 실수령액 계산기 | `https://www.moneycalculator.co.kr/annual-salary-net-calculator` | index, follow | yes | 231 | 74752 | 0.3% |
| 퇴직금 계산기 | `/severance-pay-calculator` | 퇴직금 계산기 \| 평균임금·상여금 포함 계산 | 퇴직금 계산기 | `https://www.moneycalculator.co.kr/severance-pay-calculator` | index, follow | yes | 186 | 9451 | 2.0% |
| 물타기 계산기 | `/stock-average-calculator` | 물타기 계산기 \| 주식 평균단가 계산 | 물타기 계산기 | `https://www.moneycalculator.co.kr/stock-average-calculator` | index, follow | yes | 132 | 3546 | 3.7% |

## Cluster URLs To Watch

These routes are not all top performers in the supplied Naver table, but they are required by the work plan and should be watched during cluster changes.

| Page | Route | Page key | Current protection |
|---|---|---|---|
| 전기요금 누진구간 가이드 | `/guide/electricity-tier` | `guide-electricity-tier` | watch before changing title/H1/canonical/index |
| 원리금균등·원금균등 가이드 | `/guide/repayment-method-difference` | `guide-repayment-difference` | watch before changing title/H1/canonical/index |
| 월급 실수령액 계산기 | `/salary-calculator` | `salary` | protect route/canonical/index; avoid title/H1 changes without approval |
| 에어컨 하루 8시간 가이드 | `/guide/aircon-8hours-cost` | `guide-aircon-8hours` | watch before changing title/H1/canonical/index |

## Title Experiment Hold

Title experiments are explicitly deferred until after content, description, OG, internal-link improvements have shipped and 14 to 28 days of Naver data have been reviewed.
