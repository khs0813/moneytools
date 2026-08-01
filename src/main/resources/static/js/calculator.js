document.addEventListener('DOMContentLoaded', () => {
  /**
   * @typedef {number} KrwAmount 원 단위 금액
   * @typedef {number} PercentInput 화면 입력 퍼센트값. 예: 4.5는 4.5%
   * @typedef {number} RateFraction 내부 계산 비율값. 예: 0.045는 4.5%
   * @typedef {number} KwhAmount 전력 사용량(kWh)
   * @typedef {number} KrwPerKwh kWh당 원화 단가
   */
  const MAX_NUMERIC_TEXT_LENGTH = 128;
  const MAX_SCIENTIFIC_EXPONENT_ABS = 128;
  const resultScrollRequestKey = `moneytools:result-scroll:${window.location.pathname}`;
  const mobileResultScrollMedia = window.matchMedia('(max-width: 820px)');
  const PAGE_ANALYTICS_META = {
    '/electricity-bill-calculator': { calculatorType: 'electricity_bill', contentCluster: 'electricity', pageGroup: 'living' },
    '/guide/electricity-tier': { calculatorType: 'electricity_bill', contentCluster: 'electricity', pageGroup: 'living' },
    '/air-conditioner-electricity-calculator': { calculatorType: 'air_conditioner_cost', contentCluster: 'electricity', pageGroup: 'living' },
    '/guide/aircon-8hours-cost': { calculatorType: 'air_conditioner_cost', contentCluster: 'electricity', pageGroup: 'living' },
    '/loan-interest-calculator': { calculatorType: 'loan_interest', contentCluster: 'loan', pageGroup: 'loan' },
    '/guide/loan-100m-interest': { calculatorType: 'loan_interest', contentCluster: 'loan', pageGroup: 'loan' },
    '/guide/repayment-method-difference': { calculatorType: 'loan_interest', contentCluster: 'loan', pageGroup: 'loan' },
    '/loan-refinance-calculator': { calculatorType: 'loan_refinance', contentCluster: 'loan', pageGroup: 'loan' },
    '/mortgage-monthly-payment-calculator': { calculatorType: 'mortgage', contentCluster: 'loan', pageGroup: 'loan' },
    '/annual-salary-net-calculator': { calculatorType: 'annual_salary_net', contentCluster: 'salary', pageGroup: 'income' },
    '/salary-calculator': { calculatorType: 'salary_net', contentCluster: 'salary', pageGroup: 'income' },
    '/severance-pay-calculator': { calculatorType: 'severance', contentCluster: 'salary', pageGroup: 'income' },
    '/monthly-budget-calculator': { calculatorType: 'monthly_budget', contentCluster: 'living', pageGroup: 'living' },
    '/domestic-stock-tax-calculator': { calculatorType: 'domestic_stock_tax', contentCluster: 'stock_tax', pageGroup: 'investment' },
    '/stock-average-calculator': { calculatorType: 'stock_average', contentCluster: 'stock', pageGroup: 'investment' },
    '/stock-tax-calculator': { calculatorType: 'stock_tax', contentCluster: 'stock_tax', pageGroup: 'investment' },
    '/overseas-stock-tax-calculator': { calculatorType: 'overseas_stock_tax', contentCluster: 'stock_tax', pageGroup: 'investment' },
    '/dividend-calculator': { calculatorType: 'dividend', contentCluster: 'stock', pageGroup: 'investment' },
    '/fair-value-calculator': { calculatorType: 'fair_value', contentCluster: 'stock', pageGroup: 'investment' },
    '/exchange-calculator': { calculatorType: 'exchange', contentCluster: 'finance', pageGroup: 'investment' },
    '/car-maintenance-calculator': { calculatorType: 'car_maintenance', contentCluster: 'living', pageGroup: 'living' },
    '/annual-leave-pay-calculator': { calculatorType: 'annual_leave', contentCluster: 'salary', pageGroup: 'income' }
  };

  const parseReferrerHost = () => {
    if (!document.referrer) return '';
    try {
      return new URL(document.referrer).hostname.toLowerCase();
    } catch (error) {
      return '';
    }
  };

  const classifyReferrer = (host) => {
    if (!host) return 'direct';
    if (host === window.location.hostname.toLowerCase()) return 'internal';
    if (host === 'search.naver.com' || host === 'm.search.naver.com' || host.endsWith('.search.naver.com')) {
      return 'naver_organic';
    }
    if (host === 'www.google.com' || host === 'google.com' || host.endsWith('.google.com')) {
      return 'google_organic';
    }
    if (host.includes('search') || host.includes('bing.com') || host.includes('daum.net')) {
      return 'search_organic';
    }
    return 'external';
  };

  const analyticsContext = () => {
    const pageMeta = PAGE_ANALYTICS_META[window.location.pathname] ?? {};
    const referrerHost = parseReferrerHost();
    return {
      site: window.location.hostname || 'moneycalculator.co.kr',
      page_path: window.location.pathname,
      page_group: pageMeta.pageGroup ?? pageMeta.contentCluster ?? 'general',
      calculator_type: pageMeta.calculatorType ?? 'none',
      content_cluster: pageMeta.contentCluster ?? 'general',
      device: mobileResultScrollMedia.matches ? 'mobile' : 'desktop',
      referrer_host: referrerHost,
      referrer_type: classifyReferrer(referrerHost),
      experiment_id: document.documentElement.dataset.experimentId || document.documentElement.dataset.experimentVariant || 'control',
      experiment_variant: document.documentElement.dataset.experimentVariant || 'control'
    };
  };

  const trackEvent = (eventName, details = {}) => {
    const payload = { ...analyticsContext(), ...details };
    if (typeof window.gtag === 'function') {
      window.gtag('event', eventName, payload);
      return;
    }
    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({ event: eventName, ...payload });
  };

  window.MoneytoolsAnalytics = window.MoneytoolsAnalytics || {};
  window.MoneytoolsAnalytics.trackEvent = trackEvent;
  const queuedAnalyticsEvents = Array.isArray(window.MoneytoolsAnalyticsQueue)
    ? window.MoneytoolsAnalyticsQueue.splice(0)
    : [];
  queuedAnalyticsEvents.forEach((item) => {
    if (!item || !item.eventName) return;
    trackEvent(item.eventName, item.details || {});
  });

  const trackVisibleResult = (resultCard) => {
    if (!resultCard || resultCard.dataset.analyticsResultViewed === 'true') return;
    if (resultCard.hidden || resultCard.offsetParent === null) return;
    resultCard.dataset.analyticsResultViewed = 'true';
    trackEvent('calculator_complete');
    trackEvent('result_view');
    if (window.MoneytoolsAdFit && typeof window.MoneytoolsAdFit.initAdFitSlots === 'function') {
      window.MoneytoolsAdFit.initAdFitSlots();
    }
  };

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
        trackEvent('share_click', { method: 'copy_result' });
        setTimeout(() => button.textContent = original, 1400);
      } catch (error) {
        alert('브라우저에서 복사를 허용하지 않았습니다.');
      }
    });
  });

  if (consumeResultScrollRequest()) {
    scrollToResultCard(getVisibleResultCard());
  }

  const initialAnalyticsContext = analyticsContext();
  if (initialAnalyticsContext.referrer_type.endsWith('_organic')) {
    trackEvent('organic_landing_view');
  }
  try {
    const returnVisitKey = 'moneytools:return-visit';
    if (window.localStorage.getItem(returnVisitKey) === 'true') {
      trackEvent('return_visit');
    } else {
      window.localStorage.setItem(returnVisitKey, 'true');
    }
  } catch (error) {
    // Storage can be unavailable in private or restricted browsing modes.
  }

  trackVisibleResult(getVisibleResultCard());

  const setPresetInputValue = (targetId, value) => {
    if (!targetId || value === undefined) return;
    const input = document.getElementById(targetId);
    if (!input) return;
    input.value = value;
    input.dispatchEvent(new Event('input', { bubbles: true }));
    input.dispatchEvent(new Event('change', { bubbles: true }));
  };

  document.querySelectorAll('[data-quick-preset]').forEach((button) => {
    button.addEventListener('click', () => {
      setPresetInputValue(button.dataset.presetTarget, button.dataset.presetValue);
      if (button.dataset.presetFocus) {
        document.getElementById(button.dataset.presetFocus)?.focus({ preventScroll: true });
      }
      trackEvent('quick_preset_click', {
        preset_key: button.dataset.quickPreset || 'unknown'
      });
      if (button.dataset.presetSubmit === 'true' && button.dataset.presetTarget && button.dataset.presetValue !== undefined) {
        button.closest('form')?.requestSubmit();
      }
    });
  });

  if ('IntersectionObserver' in window) {
    const tableObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        const table = entry.target;
        observer.unobserve(table);
        trackEvent('comparison_table_view', {
          table_id: table.id || table.dataset.analyticsTable || 'data_table'
        });
      });
    }, { threshold: 0.35 });

    document.querySelectorAll('.data-table').forEach((table) => tableObserver.observe(table));

    const contentCompleteTarget = document.querySelector('.footer') || document.querySelector('.related-box:last-of-type');
    if (contentCompleteTarget) {
      const contentObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          observer.unobserve(entry.target);
          trackEvent('content_complete');
        });
      }, { threshold: 0.35 });
      contentObserver.observe(contentCompleteTarget);
    }
  }

  document.querySelectorAll('.related-links a').forEach((link) => {
    link.addEventListener('click', () => {
      const isNextAction = !!link.closest('.related-next-actions');
      trackEvent(isNextAction ? 'next_tool_click' : 'related_content_click', {
        target_path: link.pathname || '',
        placement: isNextAction ? 'related_next_actions' : 'related_links'
      });
      trackEvent('related_calculator_click', {
        target_path: link.pathname || ''
      });
    });
  });

  document.querySelectorAll('.official-sources a').forEach((link) => {
    link.addEventListener('click', () => {
      trackEvent('official_source_click', {
        target_host: link.hostname || ''
      });
    });
  });

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
    nationalPensionEmployeeRate: 0.0475,
    nationalPensionLowerBound: 410_000,
    nationalPensionUpperBound: 6_590_000,
    healthInsuranceEmployeeRate: 0.03595,
    longTermCareInsuranceRateOfHealth: 0.009448 / 0.0719,
    employmentInsuranceEmployeeRate: 0.009,
    localIncomeTaxRate: 0.1,
    basicPersonalDeductionPerPerson: 1_500_000,
    childTaxCreditOne: 20_830,
    childTaxCreditTwo: 45_830,
    childTaxCreditAdditional: 33_330
  };

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
      trackEvent('calculator_start');
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

  const formatWon = (value) => `${Math.round(value).toLocaleString('ko-KR')}원`;
  const formatPercent = (value) => `${(value * 100).toLocaleString('ko-KR', { maximumFractionDigits: 4 })}%`;

  const roundWon = (value) => Math.round(value);
  const clamp = (value, min, max) => Math.max(min, Math.min(max, value));
  const calculateNationalPension = (taxableMonthlyIncome) => {
    if (taxableMonthlyIncome <= 0) return 0;
    const pensionBase = clamp(
      taxableMonthlyIncome,
      ANNUAL_SALARY_RATE_CONFIG.nationalPensionLowerBound,
      ANNUAL_SALARY_RATE_CONFIG.nationalPensionUpperBound
    );
    return roundWon(pensionBase * ANNUAL_SALARY_RATE_CONFIG.nationalPensionEmployeeRate);
  };
  const earnedIncomeDeduction = (annualGross) => {
    if (annualGross <= 5_000_000) return annualGross * 0.70;
    if (annualGross <= 15_000_000) return 3_500_000 + (annualGross - 5_000_000) * 0.40;
    if (annualGross <= 45_000_000) return 7_500_000 + (annualGross - 15_000_000) * 0.15;
    if (annualGross <= 100_000_000) return 12_000_000 + (annualGross - 45_000_000) * 0.05;
    return Math.min(20_000_000, 14_750_000 + (annualGross - 100_000_000) * 0.02);
  };
  const proxySpecialDeduction = (annualGross, familyCount) => {
    const familyBucket = Math.min(familyCount, 3);
    if (annualGross <= 30_000_000) {
      if (familyBucket === 1) return 3_100_000 + annualGross * 0.04;
      if (familyBucket === 2) return 3_600_000 + annualGross * 0.04;
      return 5_000_000 + annualGross * 0.07 + Math.max(0, annualGross - 40_000_000) * 0.04;
    }
    if (annualGross <= 45_000_000) {
      if (familyBucket === 1) return 3_100_000 + annualGross * 0.04 - (annualGross - 30_000_000) * 0.05;
      if (familyBucket === 2) return 3_600_000 + annualGross * 0.04 - (annualGross - 30_000_000) * 0.05;
      return 5_000_000 + annualGross * 0.07 - (annualGross - 30_000_000) * 0.05;
    }
    if (annualGross <= 70_000_000) {
      if (familyBucket === 1) return 3_100_000 + annualGross * 0.015;
      if (familyBucket === 2) return 3_600_000 + annualGross * 0.02;
      return 5_000_000 + annualGross * 0.05;
    }
    if (familyBucket === 1) return 3_100_000 + annualGross * 0.005;
    if (familyBucket === 2) return 3_600_000 + annualGross * 0.01;
    return 5_000_000 + annualGross * 0.03;
  };
  const progressiveIncomeTax = (taxBase) => {
    if (taxBase <= 14_000_000) return taxBase * 0.06;
    if (taxBase <= 50_000_000) return 840_000 + (taxBase - 14_000_000) * 0.15;
    if (taxBase <= 88_000_000) return 6_240_000 + (taxBase - 50_000_000) * 0.24;
    if (taxBase <= 150_000_000) return 15_360_000 + (taxBase - 88_000_000) * 0.35;
    if (taxBase <= 300_000_000) return 37_060_000 + (taxBase - 150_000_000) * 0.38;
    if (taxBase <= 500_000_000) return 94_060_000 + (taxBase - 300_000_000) * 0.40;
    if (taxBase <= 1_000_000_000) return 174_060_000 + (taxBase - 500_000_000) * 0.42;
    return 384_060_000 + (taxBase - 1_000_000_000) * 0.45;
  };
  const earnedIncomeTaxCredit = (calculatedTax, annualTaxableGross) => {
    const credit = calculatedTax <= 1_300_000
      ? calculatedTax * 0.55
      : 715_000 + (calculatedTax - 1_300_000) * 0.30;
    let cap;
    if (annualTaxableGross <= 33_000_000) {
      cap = 740_000;
    } else if (annualTaxableGross <= 70_000_000) {
      cap = Math.max(660_000, 740_000 - (annualTaxableGross - 33_000_000) * 0.008);
    } else if (annualTaxableGross <= 120_000_000) {
      cap = Math.max(500_000, 660_000 - (annualTaxableGross - 70_000_000) / 2.0);
    } else {
      cap = Math.max(200_000, 500_000 - (annualTaxableGross - 120_000_000) / 2.0);
    }
    return Math.min(credit, cap);
  };
  const estimateAnnualIncomeTax = (annualTaxableGross, dependentsIncludingSelf, annualEmployeePension) => {
    if (annualTaxableGross <= 0) return 0;
    const familyCount = Math.max(1, dependentsIncludingSelf);
    const taxBase = Math.max(0,
      annualTaxableGross
        - earnedIncomeDeduction(annualTaxableGross)
        - familyCount * ANNUAL_SALARY_RATE_CONFIG.basicPersonalDeductionPerPerson
        - proxySpecialDeduction(annualTaxableGross, familyCount)
        - annualEmployeePension
    );
    const calculatedTax = progressiveIncomeTax(taxBase);
    return Math.max(0, calculatedTax - earnedIncomeTaxCredit(calculatedTax, annualTaxableGross));
  };
  const withholdingChildTaxCredit = (eligibleChildren) => {
    if (eligibleChildren <= 0) return 0;
    if (eligibleChildren === 1) return ANNUAL_SALARY_RATE_CONFIG.childTaxCreditOne;
    return ANNUAL_SALARY_RATE_CONFIG.childTaxCreditTwo
      + Math.max(0, eligibleChildren - 2) * ANNUAL_SALARY_RATE_CONFIG.childTaxCreditAdditional;
  };

  const calculateAnnualSalaryNetPay = (values) => {
    const baseMonthlySalary = values.retirementIncluded ? values.annualSalary / 13 : values.annualSalary / 12;
    const monthlyBonus = values.monthlyBonusEnabled ? values.monthlyBonusAmount : 0;
    const grossMonthly = baseMonthlySalary + monthlyBonus;
    const taxableMonthlyIncome = Math.max(0, grossMonthly - values.monthlyTaxFreeMeal);

    const nationalPension = calculateNationalPension(taxableMonthlyIncome);
    const healthInsurance = roundWon(taxableMonthlyIncome * ANNUAL_SALARY_RATE_CONFIG.healthInsuranceEmployeeRate);
    const longTermCareInsurance = roundWon(healthInsurance * ANNUAL_SALARY_RATE_CONFIG.longTermCareInsuranceRateOfHealth);
    const employmentInsurance = roundWon(taxableMonthlyIncome * ANNUAL_SALARY_RATE_CONFIG.employmentInsuranceEmployeeRate);
    const familyCount = Math.max(1, values.dependents);
    const eligibleChildren = Math.min(Math.max(0, values.childrenUnder20), Math.max(0, familyCount - 1));
    const annualIncomeTax = estimateAnnualIncomeTax(taxableMonthlyIncome * 12, familyCount, nationalPension * 12);
    const incomeTax = Math.max(0, roundWon(annualIncomeTax / 12) - withholdingChildTaxCredit(eligibleChildren));
    const localIncomeTax = roundWon(incomeTax * ANNUAL_SALARY_RATE_CONFIG.localIncomeTaxRate);
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
      trackEvent('calculator_start');
      renderAnnualSalaryResult();
      trackVisibleResult(annualSalaryResultPanel);
      scrollToResultCard(annualSalaryResultPanel);
    });
  }

  const domesticStockTaxForm = document.querySelector('[data-domestic-stock-tax-calculator]');
  if (domesticStockTaxForm) {
    const DOMESTIC_STOCK_TAX_POLICIES = {
      KOSPI: {
        label: '코스피 일반 주권',
        transactionTaxRate: 0.0005,
        agricultureTaxRate: 0.0015,
        effectiveDate: '2026-01-01'
      },
      KOSDAQ: {
        label: '코스닥 일반 주권',
        transactionTaxRate: 0.002,
        agricultureTaxRate: 0,
        effectiveDate: '2026-01-01'
      },
      KONEX: {
        label: '코넥스',
        transactionTaxRate: 0.001,
        agricultureTaxRate: 0,
        effectiveDate: '2026-01-01'
      },
      KOTC: {
        label: 'K-OTC',
        transactionTaxRate: 0.002,
        agricultureTaxRate: 0,
        effectiveDate: '2026-01-01'
      }
    };
    const layout = document.querySelector('[data-domestic-stock-tax-layout]');
    const resultPanel = document.querySelector('[data-domestic-stock-tax-result-panel]');
    const marketInput = document.getElementById('domesticMarket');
    const productTypeInput = document.getElementById('domesticProductType');
    const useCustomRatesInput = document.getElementById('domesticUseCustomRates');
    const applyCapitalGainsTaxInput = document.getElementById('domesticApplyCapitalGainsTax');
    const capitalGainsTaxRateInput = document.getElementById('domesticCapitalGainsTaxRate');
    const customTransactionTaxRateInput = document.getElementById('domesticCustomTransactionTaxRate');
    const customAgricultureTaxRateInput = document.getElementById('domesticCustomAgricultureTaxRate');
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

    const syncCustomRateInputs = () => {
      const useCustomRates = useCustomRatesInput?.checked ?? false;
      [customTransactionTaxRateInput, customAgricultureTaxRateInput].forEach((input) => {
        if (!input) return;
        input.disabled = !useCustomRates;
        if (!useCustomRates) {
          input.value = '';
          input.setCustomValidity('');
        }
      });
      if (productTypeInput && productTypeInput.value === 'CUSTOM_RATE_REQUIRED' && !useCustomRates) {
        productTypeInput.setCustomValidity('일반 주권 외 상품은 사용자 지정 세율 시나리오를 선택하고 세율을 입력해주세요.');
      } else {
        productTypeInput?.setCustomValidity('');
      }
    };

    syncCustomRateInputs();
    useCustomRatesInput?.addEventListener('change', syncCustomRateInputs);
    productTypeInput?.addEventListener('change', syncCustomRateInputs);

    const renderDomesticStockTaxResult = () => {
      syncCustomRateInputs();
      if (productTypeInput && !productTypeInput.checkValidity()) {
        productTypeInput.reportValidity();
        return;
      }

      const policy = DOMESTIC_STOCK_TAX_POLICIES[marketInput?.value] ?? DOMESTIC_STOCK_TAX_POLICIES.KOSPI;
      const useCustomRates = useCustomRatesInput?.checked ?? false;
      const buyAmount = parseNumberInput(document.getElementById('domesticBuyAmount'));
      const sellAmount = parseNumberInput(document.getElementById('domesticSellAmount'));
      const buyFeeAmount = parseNumberInput(document.getElementById('domesticBuyFeeAmount'));
      const sellFeeAmount = parseNumberInput(document.getElementById('domesticSellFeeAmount'));
      const brokerageFees = buyFeeAmount + sellFeeAmount;
      const transactionTaxRate = useCustomRates
        ? parseNumberInput(customTransactionTaxRateInput) / 100
        : policy.transactionTaxRate;
      const agricultureTaxRate = useCustomRates
        ? parseNumberInput(customAgricultureTaxRateInput) / 100
        : policy.agricultureTaxRate;
      const capitalGainsTaxRate = parseNumberInput(document.getElementById('domesticCapitalGainsTaxRate')) / 100;
      const applyCapitalGainsTax = applyCapitalGainsTaxInput?.checked ?? false;

      const capitalGain = sellAmount - buyAmount - brokerageFees;
      const taxableCapitalGain = Math.max(0, capitalGain);
      const transactionTax = sellAmount * transactionTaxRate;
      const agricultureTax = sellAmount * agricultureTaxRate;
      const capitalGainsTax = applyCapitalGainsTax ? taxableCapitalGain * capitalGainsTaxRate : 0;
      const totalTax = transactionTax + agricultureTax + capitalGainsTax;
      const totalCost = brokerageFees + totalTax;
      const afterTaxProceeds = sellAmount - sellFeeAmount - transactionTax - agricultureTax - capitalGainsTax;
      const afterTaxProfit = capitalGain - totalTax;

      const result = {
        appliedPolicy: `${useCustomRates ? '사용자 지정' : policy.label} · ${policy.effectiveDate} 기준`,
        appliedRates: `증권거래세 ${formatPercent(transactionTaxRate)} / 농어촌특별세 ${formatPercent(agricultureTaxRate)}`,
        capitalGain,
        transactionTax,
        agricultureTax,
        brokerageFees,
        capitalGainsTax,
        totalCost,
        afterTaxProceeds,
        afterTaxProfit
      };

      Object.entries(result).forEach(([key, value]) => {
        if (resultTargets[key]) {
          resultTargets[key].textContent = typeof value === 'number' ? formatWon(value) : value;
        }
      });
      resultPanel?.removeAttribute('hidden');
      layout?.classList.add('has-result');
    };

    domesticStockTaxForm.addEventListener('submit', (event) => {
      event.preventDefault();
      if (!validateNumberInputsInForm(domesticStockTaxForm)) return;
      trackEvent('calculator_start');
      renderDomesticStockTaxResult();
      trackVisibleResult(resultPanel);
      scrollToResultCard(resultPanel);
    });
  }

});
