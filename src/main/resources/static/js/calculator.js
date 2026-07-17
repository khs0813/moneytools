document.addEventListener('DOMContentLoaded', () => {
  const MAX_NUMERIC_TEXT_LENGTH = 128;
  const MAX_SCIENTIFIC_EXPONENT_ABS = 128;
  const resultScrollRequestKey = `moneytools:result-scroll:${window.location.pathname}`;
  const mobileResultScrollMedia = window.matchMedia('(max-width: 820px)');

  const shouldScrollToResult = () => mobileResultScrollMedia.matches;

  const rememberResultScrollRequest = () => {
    if (!shouldScrollToResult()) return;
    try {
      window.sessionStorage.setItem(resultScrollRequestKey, 'true');
    } catch (error) {
      // Storage can be unavailable in private or restricted browsing modes.
    }
  };

  const consumeResultScrollRequest = () => {
    try {
      const requested = window.sessionStorage.getItem(resultScrollRequestKey) === 'true';
      if (requested) {
        window.sessionStorage.removeItem(resultScrollRequestKey);
      }
      return requested;
    } catch (error) {
      return false;
    }
  };

  const getVisibleResultCard = (container = document) =>
    Array.from(container.querySelectorAll('[data-result-card]'))
      .find((card) => !card.hidden && card.offsetParent !== null) ?? null;

  const getScrollMarginTop = (element) => {
    const scrollMarginTop = Number.parseFloat(window.getComputedStyle(element).scrollMarginTop);
    return Number.isFinite(scrollMarginTop) ? scrollMarginTop : 0;
  };

  const scrollToResultCard = (resultCard) => {
    if (!shouldScrollToResult()) return;
    if (!resultCard) return;
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const scroll = (behavior) => {
      const targetTop = Math.max(0, resultCard.getBoundingClientRect().top + window.scrollY - getScrollMarginTop(resultCard));
      window.scrollTo({
        top: targetTop,
        behavior
      });
    };

    window.requestAnimationFrame(() => {
      scroll(prefersReducedMotion ? 'auto' : 'smooth');
      window.setTimeout(() => scroll('auto'), 120);
    });
  };

  const sidebar = document.getElementById('sidebar');
  const toggle = document.querySelector('[data-menu-toggle]');
  if (toggle && sidebar) {
    const setMenuOpen = (open) => {
      sidebar.classList.toggle('open', open);
      document.body.classList.toggle('menu-open', open);
      toggle.setAttribute('aria-expanded', String(open));
      toggle.setAttribute('aria-label', open ? '메뉴 닫기' : '메뉴 열기');
    };

    toggle.setAttribute('aria-controls', sidebar.id);
    toggle.setAttribute('aria-expanded', 'false');
    toggle.addEventListener('click', () => setMenuOpen(!sidebar.classList.contains('open')));

    sidebar.querySelectorAll('a').forEach((link) => {
      link.addEventListener('click', () => setMenuOpen(false));
    });

    document.addEventListener('click', (event) => {
      if (!sidebar.classList.contains('open')) return;
      if (sidebar.contains(event.target) || toggle.contains(event.target)) return;
      setMenuOpen(false);
    });

    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape') {
        setMenuOpen(false);
      }
    });

    const desktopMedia = window.matchMedia('(min-width: 821px)');
    const closeOnDesktop = (event) => {
      if (event.matches) {
        setMenuOpen(false);
      }
    };
    if (typeof desktopMedia.addEventListener === 'function') {
      desktopMedia.addEventListener('change', closeOnDesktop);
    } else if (typeof desktopMedia.addListener === 'function') {
      desktopMedia.addListener(closeOnDesktop);
    }
  }

  document.querySelectorAll('[data-copy-result]').forEach((button) => {
    button.addEventListener('click', async () => {
      const card = button.closest('[data-result-card]');
      if (!card) return;
      const text = card.innerText.replace('결과 복사', '').trim();
      try {
        await navigator.clipboard.writeText(text);
        const original = button.textContent;
        button.textContent = '복사 완료';
        setTimeout(() => button.textContent = original, 1400);
      } catch (error) {
        alert('브라우저에서 복사를 허용하지 않았습니다.');
      }
    });
  });

  if (consumeResultScrollRequest()) {
    scrollToResultCard(getVisibleResultCard());
  }

  document.querySelectorAll('[data-download-table]').forEach((button) => {
    button.addEventListener('click', () => {
      const tableId = button.getAttribute('data-download-table');
      const table = document.getElementById(tableId);
      if (!table) return;

      const sanitizeCsvCell = (value) => {
        const text = String(value ?? '').trim();
        const escapeFormula = (cellText) => /^[=+\-@]/.test(cellText) ? `'${cellText}` : cellText;
        const numericText = text.replaceAll(',', '');
        if (/^-?\d+(\.\d+)?$/.test(numericText)) {
          const number = Number(numericText);
          return Number.isFinite(number) ? escapeFormula(String(Math.round(number))) : '';
        }
        return escapeFormula(text);
      };

      const rows = Array.from(table.querySelectorAll('tr')).map((row) =>
        Array.from(row.querySelectorAll('th,td'))
          .map((cell) => `"${sanitizeCsvCell(cell.innerText).replaceAll('"', '""')}"`)
          .join(',')
      );
      const blob = new Blob(['\ufeff' + rows.join('\n')], { type: 'text/csv; charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${tableId}.csv`;
      link.click();
      URL.revokeObjectURL(url);
    });
  });

  if (typeof window.flatpickr === 'function') {
    document.querySelectorAll('input[type="date"]').forEach((input) => {
      if (input.dataset.flatpickrReady === 'true') return;
      input.dataset.flatpickrReady = 'true';

      const wrapper = document.createElement('span');
      wrapper.className = 'date-picker-field';
      input.parentNode.insertBefore(wrapper, input);
      wrapper.appendChild(input);

      const button = document.createElement('button');
      button.className = 'date-picker-button';
      button.type = 'button';
      button.setAttribute('aria-label', '달력 열기');
      button.textContent = '📅';
      wrapper.appendChild(button);

      const picker = window.flatpickr(input, {
        locale: window.flatpickr.l10ns?.ko ?? 'default',
        dateFormat: 'Y-m-d',
        allowInput: true,
        disableMobile: true,
        monthSelectorType: 'static',
        nextArrow: '›',
        prevArrow: '‹',
        clickOpens: false
      });

      button.addEventListener('click', () => picker.open());
    });
  }

  const toPlainFromScientific = (value) => {
    const source = String(value ?? '').trim();
    if (!/[eE]/.test(source)) return source;

    let sign = '';
    let working = source;
    if (working.startsWith('+') || working.startsWith('-')) {
      sign = working.startsWith('-') ? '-' : '';
      working = working.slice(1);
    }

    const [mantissa, exponentText] = working.toLowerCase().split('e');
    const exponent = Number.parseInt(exponentText, 10);
    if (!Number.isFinite(exponent)) return source;
    if (Math.abs(exponent) > MAX_SCIENTIFIC_EXPONENT_ABS) return '';

    const [rawInteger = '0', rawFraction = ''] = mantissa.split('.');
    const digits = `${rawInteger}${rawFraction}`.replace(/[^\d]/g, '') || '0';
    const decimalIndex = rawInteger.length + exponent;

    let result;
    if (decimalIndex <= 0) {
      result = `0.${'0'.repeat(Math.abs(decimalIndex))}${digits}`;
    } else if (decimalIndex >= digits.length) {
      result = `${digits}${'0'.repeat(decimalIndex - digits.length)}`;
    } else {
      result = `${digits.slice(0, decimalIndex)}.${digits.slice(decimalIndex)}`;
    }

    const plain = sign + result;
    return plain.length <= MAX_NUMERIC_TEXT_LENGTH ? plain : '';
  };

  const normalizePlainNumber = (value) => {
    let working = String(value ?? '').trim();
    if (!working) return '';

    working = working.replace(/[\s\u00A0\u2007\u202F\uFF0C,]/g, '');
    if (/[eE]/.test(working)) {
      working = toPlainFromScientific(working);
    }

    if (working.length > MAX_NUMERIC_TEXT_LENGTH) return '';

    let sign = '';
    if (working.startsWith('+') || working.startsWith('-')) {
      sign = working.startsWith('-') ? '-' : '';
      working = working.slice(1);
    }

    if (!working) return '';
    const [rawInteger = '', rawFraction = ''] = working.split('.');
    let integerPart = rawInteger.replace(/^0+(?=\d)/, '');
    integerPart = integerPart || '0';
    const fractionPart = rawFraction.replace(/0+$/, '');

    if (integerPart === '0' && !fractionPart) {
      sign = '';
    }

    return sign + integerPart + (fractionPart ? `.${fractionPart}` : '');
  };

  const keepLeadingSign = (value, allowSign) => {
    if (!allowSign) {
      return { sign: '', body: value.replace(/[+-]/g, '') };
    }

    const sign = value.trim().startsWith('-') ? '-' : '';
    return { sign, body: value.replace(/[+-]/g, '') };
  };

  const sanitizeIntegerTyping = (value, allowSign) => {
    const source = String(value ?? '');
    const compact = source.replace(/[\s\u00A0\u2007\u202F\uFF0C,]/g, '');
    const base = /[eE]/.test(compact) ? toPlainFromScientific(compact) : compact;
    const { sign, body } = keepLeadingSign(base, allowSign);
    const integerOnly = body.split('.')[0].replace(/\D/g, '');
    return sign + integerOnly;
  };

  const sanitizeDecimalTyping = (value, allowSign) => {
    const source = String(value ?? '');
    const compact = source.replace(/[\s\u00A0\u2007\u202F\uFF0C,]/g, '');
    const base = /[eE]/.test(compact) ? toPlainFromScientific(compact) : compact;
    const { sign, body } = keepLeadingSign(base, allowSign);

    const filtered = body.replace(/[^\d.]/g, '');
    const firstDotIndex = filtered.indexOf('.');
    if (firstDotIndex === -1) {
      return sign + filtered;
    }

    const integerPart = filtered.slice(0, firstDotIndex).replace(/\./g, '');
    const fractionPart = filtered.slice(firstDotIndex + 1).replace(/\./g, '');
    return sign + integerPart + '.' + fractionPart;
  };

  const formatIntegerDisplay = (value, allowSign) => {
    const plain = normalizePlainNumber(sanitizeIntegerTyping(value, allowSign));
    if (!plain) return '';
    const sign = plain.startsWith('-') ? '-' : '';
    const digits = sign ? plain.slice(1) : plain;
    return sign + digits.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  };

  const formatDecimalDisplay = (value, allowSign) => {
    const typingValue = sanitizeDecimalTyping(value, allowSign);
    if (!/\d/.test(typingValue)) return '';

    const plain = normalizePlainNumber(typingValue);
    if (!plain) return '';

    const sign = plain.startsWith('-') ? '-' : '';
    const unsigned = sign ? plain.slice(1) : plain;
    const [integerPart, fractionPart = ''] = unsigned.split('.');
    const groupedInteger = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    return sign + groupedInteger + (fractionPart ? `.${fractionPart}` : '');
  };

  const getNumberOptions = (input) => ({
    mode: input.dataset.numberInput || (input.hasAttribute('data-money-input') ? 'integer' : 'decimal'),
    allowSign: input.dataset.signed === 'true'
  });

  const applyDefaultNumericConstraints = (input, options) => {
    const defaultMin = options.allowSign ? '-999999999999999' : '0';
    const defaultMax = '999999999999999';
    const defaultDecimals = options.mode === 'integer' ? '0' : '4';
    if (!input.hasAttribute('min')) input.setAttribute('min', defaultMin);
    if (!input.hasAttribute('max')) input.setAttribute('max', defaultMax);
    if (!input.hasAttribute('step')) input.setAttribute('step', options.mode === 'integer' ? '1' : '0.0001');
    if (input.dataset.min === undefined) input.dataset.min = defaultMin;
    if (input.dataset.max === undefined) input.dataset.max = defaultMax;
    if (input.dataset.decimals === undefined) input.dataset.decimals = defaultDecimals;
  };

  const getDisplayValue = (value, options) => {
    if (options.mode === 'integer') {
      return formatIntegerDisplay(value, options.allowSign);
    }
    return formatDecimalDisplay(value, options.allowSign);
  };

  const getCaretPositionFromDigits = (formattedValue, digitCount) => {
    if (digitCount <= 0) return 0;

    let seenDigits = 0;
    for (let index = 0; index < formattedValue.length; index += 1) {
      if (/\d/.test(formattedValue[index])) {
        seenDigits += 1;
        if (seenDigits >= digitCount) {
          return index + 1;
        }
      }
    }

    return formattedValue.length;
  };

  const numericInputs = document.querySelectorAll('[data-number-input], [data-money-input]');
  const applyFormattedValue = (input, options) => {
    const currentValue = input.value;
    const currentCaret = input.selectionStart ?? currentValue.length;
    const digitsBeforeCaret = currentValue.slice(0, currentCaret).replace(/\D/g, '').length;
    const nextValue = getDisplayValue(currentValue, options);

    if (nextValue !== currentValue) {
      input.value = nextValue;
      if (document.activeElement === input && typeof input.setSelectionRange === 'function') {
        const nextCaret = getCaretPositionFromDigits(nextValue, digitsBeforeCaret);
        input.setSelectionRange(nextCaret, nextCaret);
      }
    }
  };

  numericInputs.forEach((input) => {
    const options = getNumberOptions(input);
    applyDefaultNumericConstraints(input, options);
    input.addEventListener('focus', () => {
      const formatted = getDisplayValue(input.value, options);
      if (formatted !== input.value) {
        input.value = formatted;
      }
    });

    input.addEventListener('input', () => {
      applyFormattedValue(input, options);
    });

    input.addEventListener('change', () => {
      applyFormattedValue(input, options);
    });

    input.addEventListener('paste', () => {
      window.requestAnimationFrame(() => applyFormattedValue(input, options));
    });

    input.addEventListener('keyup', () => {
      applyFormattedValue(input, options);
    });

    input.addEventListener('blur', () => {
      const formatted = getDisplayValue(input.value, options);
      if (formatted !== input.value) {
        input.value = formatted;
      }
    });

    const initialValue = getDisplayValue(input.value, options);
    if (initialValue && initialValue !== input.value) {
      input.value = initialValue;
    }
  });

  const ANNUAL_SALARY_RATE_CONFIG = {
    nationalPensionEmployeeRate: 0.045,
    healthInsuranceEmployeeRate: 0.03545,
    longTermCareInsuranceRate: 0.1295,
    employmentInsuranceEmployeeRate: 0.009,
    localIncomeTaxRate: 0.1
  };

  const SIMPLE_INCOME_TAX_BRACKETS = [
    { limit: 14_000_000, rate: 0.06, quickDeduction: 0 },
    { limit: 50_000_000, rate: 0.15, quickDeduction: 1_260_000 },
    { limit: 88_000_000, rate: 0.24, quickDeduction: 5_760_000 },
    { limit: 150_000_000, rate: 0.35, quickDeduction: 15_440_000 },
    { limit: 300_000_000, rate: 0.38, quickDeduction: 19_940_000 },
    { limit: 500_000_000, rate: 0.40, quickDeduction: 25_940_000 },
    { limit: 1_000_000_000, rate: 0.42, quickDeduction: 35_940_000 },
    { limit: Number.POSITIVE_INFINITY, rate: 0.45, quickDeduction: 65_940_000 }
  ];

  const decimalPlaces = (value) => {
    const plain = normalizePlainNumber(value);
    const fraction = plain.split('.')[1] ?? '';
    return fraction.length;
  };

  const validateNumberInput = (input) => {
    if (!input) return true;
    if (input.disabled) {
      input.setCustomValidity('');
      return true;
    }
    const plain = normalizePlainNumber(input.value);
    if (!plain && input.dataset.optional === 'true') {
      input.setCustomValidity('');
      return true;
    }
    if (!plain) {
      input.setCustomValidity('숫자를 입력해주세요.');
      return false;
    }

    const number = Number(plain);
    const min = input.dataset.min === undefined ? Number.NEGATIVE_INFINITY : Number(input.dataset.min);
    const max = input.dataset.max === undefined ? Number.POSITIVE_INFINITY : Number(input.dataset.max);
    const maxDecimals = input.dataset.decimals === undefined ? null : Number(input.dataset.decimals);

    if (!Number.isFinite(number)) {
      input.setCustomValidity('유효한 숫자를 입력해주세요.');
      return false;
    }
    if (number < min || number > max) {
      input.setCustomValidity(`${min.toLocaleString('ko-KR')} 이상 ${max.toLocaleString('ko-KR')} 이하로 입력해주세요.`);
      return false;
    }
    if (maxDecimals !== null && decimalPlaces(plain) > maxDecimals) {
      input.setCustomValidity(`소수점은 ${maxDecimals}자리까지 입력해주세요.`);
      return false;
    }

    input.setCustomValidity('');
    return true;
  };

  const validateNumberInputsInForm = (form) => {
    const inputs = Array.from(form.querySelectorAll('[data-number-input], [data-money-input]'));
    const invalid = inputs.find((input) => !validateNumberInput(input));
    if (invalid) {
      invalid.reportValidity();
      return false;
    }
    return true;
  };

  document.querySelectorAll('form.calculator-form[method]').forEach((form) => {
    if ((form.getAttribute('method') ?? '').toLowerCase() !== 'post') return;
    form.addEventListener('submit', () => {
      rememberResultScrollRequest();
    });
  });

  numericInputs.forEach((input) => {
    input.addEventListener('input', () => validateNumberInput(input));
    input.addEventListener('change', () => validateNumberInput(input));
    validateNumberInput(input);
  });

  const parseNumberInput = (input) => {
    validateNumberInput(input);
    const plain = normalizePlainNumber(input?.value ?? '');
    const number = Number(plain);
    return Number.isFinite(number) ? number : 0;
  };

  const formatWon = (value) => `${Math.round(Math.max(0, value)).toLocaleString('ko-KR')}원`;

  const estimateMonthlyIncomeTax = (monthlyTaxableIncome, dependents, childrenUnder20) => {
    const annualTaxableIncome = monthlyTaxableIncome * 12;
    const simplifiedDeduction = Math.max(1, dependents) * 1_500_000 + Math.max(0, childrenUnder20) * 1_000_000;
    const taxableBase = Math.max(0, annualTaxableIncome - simplifiedDeduction);
    const bracket = SIMPLE_INCOME_TAX_BRACKETS.find((item) => taxableBase <= item.limit)
      ?? SIMPLE_INCOME_TAX_BRACKETS[SIMPLE_INCOME_TAX_BRACKETS.length - 1];
    const annualIncomeTax = Math.max(0, taxableBase * bracket.rate - bracket.quickDeduction);
    return annualIncomeTax / 12;
  };

  const calculateAnnualSalaryNetPay = (values) => {
    const baseMonthlySalary = values.retirementIncluded ? values.annualSalary / 13 : values.annualSalary / 12;
    const monthlyBonus = values.monthlyBonusEnabled ? values.monthlyBonusAmount : 0;
    const grossMonthly = baseMonthlySalary + monthlyBonus;
    const taxableMonthlyIncome = Math.max(0, grossMonthly - values.monthlyTaxFreeMeal);

    const nationalPension = taxableMonthlyIncome * ANNUAL_SALARY_RATE_CONFIG.nationalPensionEmployeeRate;
    const healthInsurance = taxableMonthlyIncome * ANNUAL_SALARY_RATE_CONFIG.healthInsuranceEmployeeRate;
    const longTermCareInsurance = healthInsurance * ANNUAL_SALARY_RATE_CONFIG.longTermCareInsuranceRate;
    const employmentInsurance = taxableMonthlyIncome * ANNUAL_SALARY_RATE_CONFIG.employmentInsuranceEmployeeRate;
    const incomeTax = estimateMonthlyIncomeTax(taxableMonthlyIncome, values.dependents, values.childrenUnder20);
    const localIncomeTax = incomeTax * ANNUAL_SALARY_RATE_CONFIG.localIncomeTaxRate;
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
      netAnnual: netMonthly * 12
    };
  };

  const annualSalaryForm = document.querySelector('[data-annual-salary-calculator]');
  if (annualSalaryForm) {
    const annualSalaryLayout = document.querySelector('[data-annual-salary-layout]');
    const annualSalaryResultPanel = document.querySelector('[data-annual-salary-result-panel]');
    const resultTargets = Object.fromEntries(
      Array.from(document.querySelectorAll('[data-salary-result]')).map((element) => [element.dataset.salaryResult, element])
    );
    const renderAnnualSalaryResult = () => {
      const result = calculateAnnualSalaryNetPay({
        annualSalary: parseNumberInput(document.getElementById('annualSalary')),
        monthlyTaxFreeMeal: parseNumberInput(document.getElementById('monthlyTaxFreeMeal')),
        dependents: parseNumberInput(document.getElementById('dependents')),
        childrenUnder20: parseNumberInput(document.getElementById('childrenUnder20')),
        retirementIncluded: document.getElementById('retirementIncluded')?.checked ?? false,
        monthlyBonusEnabled: document.getElementById('monthlyBonusEnabled')?.checked ?? false,
        monthlyBonusAmount: parseNumberInput(document.getElementById('monthlyBonusAmount'))
      });

      Object.entries(result).forEach(([key, value]) => {
        if (resultTargets[key]) {
          resultTargets[key].textContent = formatWon(value);
        }
      });
      annualSalaryResultPanel?.removeAttribute('hidden');
      annualSalaryLayout?.classList.add('has-result');
    };

    annualSalaryForm.addEventListener('submit', (event) => {
      event.preventDefault();
      if (!validateNumberInputsInForm(annualSalaryForm)) return;
      renderAnnualSalaryResult();
      scrollToResultCard(annualSalaryResultPanel);
    });
  }

  const domesticStockTaxForm = document.querySelector('[data-domestic-stock-tax-calculator]');
  if (domesticStockTaxForm) {
    const layout = document.querySelector('[data-domestic-stock-tax-layout]');
    const resultPanel = document.querySelector('[data-domestic-stock-tax-result-panel]');
    const applyCapitalGainsTaxInput = document.getElementById('domesticApplyCapitalGainsTax');
    const capitalGainsTaxRateInput = document.getElementById('domesticCapitalGainsTaxRate');
    const resultTargets = Object.fromEntries(
      Array.from(document.querySelectorAll('[data-domestic-stock-tax-result]'))
        .map((element) => [element.dataset.domesticStockTaxResult, element])
    );

    const syncCapitalGainsTaxRate = () => {
      if (!capitalGainsTaxRateInput) return;
      if (applyCapitalGainsTaxInput?.checked) {
        if (!capitalGainsTaxRateInput.value.trim()) {
          capitalGainsTaxRateInput.value = '22';
        }
        return;
      }
      capitalGainsTaxRateInput.value = '';
    };

    syncCapitalGainsTaxRate();
    applyCapitalGainsTaxInput?.addEventListener('change', syncCapitalGainsTaxRate);

    const renderDomesticStockTaxResult = () => {
      const buyAmount = parseNumberInput(document.getElementById('domesticBuyAmount'));
      const sellAmount = parseNumberInput(document.getElementById('domesticSellAmount'));
      const feeAmount = parseNumberInput(document.getElementById('domesticFeeAmount'));
      const transactionTaxRate = parseNumberInput(document.getElementById('domesticTransactionTaxRate')) / 100;
      const capitalGainsTaxRate = parseNumberInput(document.getElementById('domesticCapitalGainsTaxRate')) / 100;
      const applyCapitalGainsTax = applyCapitalGainsTaxInput?.checked ?? false;

      const capitalGain = Math.max(0, sellAmount - buyAmount - feeAmount);
      const transactionTax = sellAmount * transactionTaxRate;
      const capitalGainsTax = applyCapitalGainsTax ? capitalGain * capitalGainsTaxRate : 0;
      const totalTax = transactionTax + capitalGainsTax;
      const afterTaxProfit = Math.max(0, sellAmount - buyAmount - feeAmount - totalTax);

      const result = {
        capitalGain,
        transactionTax,
        capitalGainsTax,
        totalTax,
        afterTaxProfit
      };

      Object.entries(result).forEach(([key, value]) => {
        if (resultTargets[key]) {
          resultTargets[key].textContent = formatWon(value);
        }
      });
      resultPanel?.removeAttribute('hidden');
      layout?.classList.add('has-result');
    };

    domesticStockTaxForm.addEventListener('submit', (event) => {
      event.preventDefault();
      if (!validateNumberInputsInForm(domesticStockTaxForm)) return;
      renderDomesticStockTaxResult();
      scrollToResultCard(resultPanel);
    });
  }

});
