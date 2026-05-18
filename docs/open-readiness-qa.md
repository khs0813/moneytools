# Open readiness QA

- Fixed validation/error rendering for select fields: dividend period, exchange currencies, salary income type, loan repayment type.
- Existing numeric input formatting path remains unchanged.
- Static checks completed: routes/templates/fragments present, no `#fields.fieldValue(` usage, no `type="number"` inputs, SEO endpoints defined.
- Remaining manual step: run the app locally and verify mobile layout in a real browser before production launch.
