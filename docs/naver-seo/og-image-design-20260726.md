# Naver SEO OG Image Design Notes - 2026-07-26

PR 1 only records the image plan. It does not switch OG images yet.

## Requirements

- Larger than 150x150.
- At least 5 KB.
- Aspect ratio no wider than 3:1.
- Page-specific subject matter, not repeated logo-only artwork.
- No unsupported claims such as "1위", "100% 정확", "공식 인증", "최고의 계산기", or clickbait.
- Width and height must be rendered in metadata when the implementation lands.

## Candidate Assets

| Page | Target file | Message |
|---|---|---|
| 전기요금 계산기 | `/static/og/electricity-bill-calculator.png` | 가정용 전기요금·누진구간 |
| 대출이자 계산기 | `/static/og/loan-interest-calculator.png` | 원리금균등·원금균등 월상환액 |
| 연봉 실수령액 계산기 | `/static/og/annual-salary-net-calculator.png` | 2026 연봉 실수령액·공제액 |
| 에어컨 전기세 계산기 | `/static/og/air-conditioner-electricity-calculator.png` | 하루 8시간 에어컨 예상 전기세 |
| 국내주식 세금 계산기 | `/static/og/domestic-stock-tax-calculator.png` | 국내주식 매도세금·증권거래세 |
| 물타기 계산기 | `/static/og/stock-average-calculator.png` | 추가매수 후 평균단가 |

## Implementation Notes For Later PRs

- Add page-level OG image fields to the SEO page model instead of hard-coding in templates.
- Keep shared default image for pages without a page-specific asset.
- Keep title/H1/canonical unchanged unless a later title experiment is explicitly approved.
