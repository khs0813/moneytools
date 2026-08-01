# Render AdFit Environment Map

Do not put DAN IDs in source control. Render environment variables are the source of truth for current unit IDs.

## Global Controls

| Env | Default | Purpose |
| --- | --- | --- |
| `PUBLIC_ADFIT_ENABLED` | `false` | Global AdFit enable switch. |
| `PUBLIC_ADFIT_ALLOWED_HOSTS` | `www.moneycalculator.co.kr` | Comma-separated host allowlist. |
| `PUBLIC_ADFIT_EXPERIMENT` | `off` | Current single active experiment id. |
| `PUBLIC_ADFIT_CALC_PRE_FAQ_ROUTES` | empty | Route allowlist for the currently unused pre-FAQ calculator placement. |

## Placement Flags

All flags default to `true` to preserve current behavior when `PUBLIC_ADFIT_ENABLED=true`.

| Env | Placement |
| --- | --- |
| `PUBLIC_ADFIT_ENABLE_CALCULATOR_POST_TOOL` | `calculator_post_tool` |
| `PUBLIC_ADFIT_ENABLE_CALCULATOR_ARTICLE_MID` | `calculator_article_mid` |
| `PUBLIC_ADFIT_ENABLE_CALCULATOR_PRE_FAQ` | `calculator_pre_faq` |
| `PUBLIC_ADFIT_ENABLE_GUIDE_ARTICLE_MID` | `guide_article_mid` |
| `PUBLIC_ADFIT_ENABLE_GUIDE_PRE_FAQ` | `guide_pre_faq` |
| `PUBLIC_ADFIT_ENABLE_GUIDE_INDEX` | `guide_index` |
| `PUBLIC_ADFIT_ENABLE_HOME_MID` | `home_mid` |

## Unit Variables

| Env | Size | Notes |
| --- | --- | --- |
| `PUBLIC_ADFIT_CALC_POST_TOOL_DESKTOP` | `300x250` | Keep. Strongest earnings unit in baseline. |
| `PUBLIC_ADFIT_CALC_POST_TOOL_MOBILE` | `320x100` | Keep. Requests only after result card is visible. |
| `PUBLIC_ADFIT_CALC_POST_TOOL_MOBILE_ALT` | `300x250` | Used only when `PUBLIC_ADFIT_EXPERIMENT=calc_post_tool_mobile_alt`. |
| `PUBLIC_ADFIT_CALC_ARTICLE_DESKTOP` | `728x90` | Low VR. Experiment only after mobile result. |
| `PUBLIC_ADFIT_CALC_ARTICLE_MOBILE` | `320x100` | First placement move candidate, one page group only. |
| `PUBLIC_ADFIT_CALC_PRE_FAQ_DESKTOP` | `728x90` | Code-supported but not mounted by any template. |
| `PUBLIC_ADFIT_CALC_PRE_FAQ_MOBILE` | `320x100` | Code-supported but not mounted by any template. |
| `PUBLIC_ADFIT_GUIDE_DESKTOP` | `728x90` | Shared by guide index, article mid, and pre-FAQ placements. |
| `PUBLIC_ADFIT_GUIDE_MOBILE` | `320x100` | Shared by guide index, article mid, and pre-FAQ placements. |
| `PUBLIC_ADFIT_HOME_DESKTOP` | `728x90` | OFF candidate after baseline observation. |
| `PUBLIC_ADFIT_HOME_MOBILE` | `320x100` | OFF candidate after baseline observation. |

## Operational Notes

- If a unit env is empty, the slot is removed and no ad request is made.
- If a placement has a regression, turn off only that placement flag first.
- Do not add `NEXT_PUBLIC_*`, `VITE_*`, or another framework prefix to this Spring Boot project.
- Do not invent replacement DAN IDs. If a new unit is needed, document the env name and exact required size first.

