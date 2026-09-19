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
        appliedPolicy: `${useCustomRates ? '사용자 지정' : policy.label} · 2026년 9월 기준`,
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

  // ====================================================
  // 정적 사이트(Render Static Site)용 클라이언트 계산기 엔진
  // ====================================================
  const parseNum = (idOrElem) => {
    const elem = typeof idOrElem === 'string' ? document.getElementById(idOrElem) : idOrElem;
    return parseNumberInput(elem);
  };

  const formatComma = (value, decimals = 0) => {
    const num = Number(value) || 0;
    return decimals > 0
      ? num.toLocaleString('ko-KR', { minimumFractionDigits: decimals, maximumFractionDigits: decimals })
      : Math.round(num).toLocaleString('ko-KR');
  };

  const showResult = (layout, resultPanel, emptyState) => {
    resultPanel?.removeAttribute('hidden');
    emptyState?.setAttribute('hidden', 'true');
    layout?.classList.add('has-result');
    trackVisibleResult(resultPanel);
    scrollToResultCard(resultPanel);
  };

  /* 1. 대출이자 계산기 (loan-interest-calculator) */
  const loanForm = document.getElementById('principal')?.closest('form');
  if (loanForm) {
    const layout = loanForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');
    const scheduleSection = document.querySelector('[data-loan-schedule-section], section.content-card:has(#loan-schedule)');
    const tableBody = document.querySelector('#loan-schedule tbody');

    loanForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(loanForm)) return;
      trackEvent('calculator_start');

      const principal = parseNum('principal');
      const annualRate = parseNum('annualRate');
      const years = parseNum('years');
      const repaymentType = document.getElementById('repaymentType')?.value || 'EQUAL_PAYMENT';

      const monthlyRate = annualRate / 100.0 / 12.0;
      const months = Math.max(1, years * 12);
      const schedule = [];

      if (repaymentType === 'EQUAL_PRINCIPAL') {
        let remaining = principal;
        const fixedPrincipal = principal / months;
        for (let m = 1; m <= months; m++) {
          const interest = remaining * monthlyRate;
          const principalPayment = Math.min(remaining, fixedPrincipal);
          remaining = Math.max(0, remaining - principalPayment);
          schedule.push({ month: m, payment: principalPayment + interest, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
        }
      } else if (repaymentType === 'BULLET') {
        for (let m = 1; m <= months; m++) {
          const interest = principal * monthlyRate;
          const principalPayment = m === months ? principal : 0;
          const remaining = m === months ? 0 : principal;
          schedule.push({ month: m, payment: interest + principalPayment, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
        }
      } else {
        if (monthlyRate === 0) {
          const monthlyPayment = principal / months;
          let remaining = principal;
          for (let m = 1; m <= months; m++) {
            remaining = Math.max(0, remaining - monthlyPayment);
            schedule.push({ month: m, payment: monthlyPayment, principalPayment: monthlyPayment, interestPayment: 0, remainingPrincipal: remaining });
          }
        } else {
          const factor = Math.pow(1 + monthlyRate, months);
          const monthlyPayment = principal * monthlyRate * factor / (factor - 1);
          let remaining = principal;
          for (let m = 1; m <= months; m++) {
            const interest = remaining * monthlyRate;
            const principalPayment = Math.min(remaining, monthlyPayment - interest);
            remaining = Math.max(0, remaining - principalPayment);
            schedule.push({ month: m, payment: monthlyPayment, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
          }
        }
      }

      const totalPayment = schedule.reduce((sum, r) => sum + r.payment, 0);
      const totalInterest = Math.max(0, totalPayment - principal);
      const averageMonthly = totalPayment / months;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 5) {
        targets[0].textContent = formatWon(schedule[0]?.payment ?? 0);
        targets[1].textContent = formatWon(averageMonthly);
        targets[2].textContent = formatWon(schedule[months - 1]?.payment ?? 0);
        targets[3].textContent = formatWon(totalInterest);
        targets[4].textContent = formatWon(totalPayment);
      }

      if (tableBody) {
        tableBody.innerHTML = schedule.map((row) => `
          <tr>
            <td>${row.month}회차</td>
            <td>${formatComma(row.payment)}</td>
            <td>${formatComma(row.principalPayment)}</td>
            <td>${formatComma(row.interestPayment)}</td>
            <td>${formatComma(row.remainingPrincipal)}</td>
          </tr>
        `).join('');
      }

      scheduleSection?.removeAttribute('hidden');
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 2. 물타기 계산기 (stock-average-calculator) */
  const stockAvgForm = document.getElementById('currentAveragePrice')?.closest('form');
  if (stockAvgForm) {
    const layout = stockAvgForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    stockAvgForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(stockAvgForm)) return;
      trackEvent('calculator_start');

      const currentShares = parseNum('currentShares');
      const currentAveragePrice = parseNum('currentAveragePrice');
      const additionalShares = parseNum('additionalShares');
      const additionalPrice = parseNum('additionalPrice');

      const currentInvestment = currentShares * currentAveragePrice;
      const additionalInvestment = additionalShares * additionalPrice;
      const totalShares = currentShares + additionalShares;
      const totalInvestment = currentInvestment + additionalInvestment;
      const newAveragePrice = totalShares === 0 ? 0 : totalInvestment / totalShares;
      const averagePriceChange = newAveragePrice - currentAveragePrice;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 7) {
        targets[0].textContent = formatWon(currentInvestment);
        targets[1].textContent = formatWon(additionalInvestment);
        targets[2].textContent = `${formatComma(totalShares)}주`;
        targets[3].textContent = formatWon(totalInvestment);
        targets[4].textContent = formatWon(newAveragePrice);
        targets[5].textContent = `${averagePriceChange > 0 ? '+' : ''}${formatWon(averagePriceChange)}`;
        targets[6].textContent = formatWon(newAveragePrice);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 3. 대출 갈아타기 계산기 (loan-refinance-calculator) */
  const loanRefinanceForm = document.getElementById('currentRemainingYears')?.closest('form');
  if (loanRefinanceForm) {
    const layout = loanRefinanceForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    loanRefinanceForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(loanRefinanceForm)) return;
      trackEvent('calculator_start');

      const balance = parseNum('currentBalance');
      const curRate = parseNum('currentAnnualRate') / 100 / 12;
      const curMonths = Math.max(1, parseNum('currentRemainingYears') * 12);
      const newRate = parseNum('newAnnualRate') / 100 / 12;
      const newMonths = Math.max(1, parseNum('newYears') * 12);
      const repaymentType = document.getElementById('repaymentType')?.value || 'EQUAL_PAYMENT';
      const penaltyRate = parseNum('prepaymentPenaltyRate') / 100;
      const addCost = parseNum('additionalCost');

      const getMonthlyPayment = (p, rate, m) => {
        if (repaymentType === 'EQUAL_PRINCIPAL') return p / m + p * rate;
        if (rate === 0) return p / m;
        const factor = Math.pow(1 + rate, m);
        return p * rate * factor / (factor - 1);
      };

      const getTotalInterest = (p, rate, m) => {
        if (repaymentType === 'EQUAL_PRINCIPAL') {
          let interest = 0;
          const fixedP = p / m;
          let rem = p;
          for (let i = 1; i <= m; i++) {
            interest += rem * rate;
            rem = Math.max(0, rem - fixedP);
          }
          return interest;
        }
        return getMonthlyPayment(p, rate, m) * m - p;
      };

      const curMonthly = getMonthlyPayment(balance, curRate, curMonths);
      const newMonthly = getMonthlyPayment(balance, newRate, newMonths);
      const monthlySavings = curMonthly - newMonthly;
      const curTotalInt = getTotalInterest(balance, curRate, curMonths);
      const newTotalInt = getTotalInterest(balance, newRate, newMonths);
      const totalIntSavings = curTotalInt - newTotalInt;
      const penalty = balance * penaltyRate;
      const switchingCost = penalty + addCost;
      const netSavings = totalIntSavings - switchingCost;
      const breakEven = monthlySavings > 0 ? Math.ceil(switchingCost / monthlySavings) : -1;

      let rec = '갈아타기 불리';
      let recClass = 'danger';
      if (netSavings > 0) {
        if (breakEven > 0 && breakEven <= 24) {
          rec = '갈아타기 유리';
          recClass = 'safe';
        } else {
          rec = '장기 보유 시 유리';
          recClass = 'caution';
        }
      }

      const riskSummary = resultPanel?.querySelector('.risk-summary');
      if (riskSummary) {
        const strong = riskSummary.querySelector('strong');
        if (strong) strong.textContent = rec;
        riskSummary.className = `risk-summary risk-${recClass}`;
      }

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 11) {
        targets[0].textContent = formatWon(curMonthly);
        targets[1].textContent = formatWon(newMonthly);
        targets[2].textContent = `${monthlySavings > 0 ? '+' : ''}${formatWon(monthlySavings)}`;
        targets[3].textContent = formatWon(curTotalInt);
        targets[4].textContent = formatWon(newTotalInt);
        targets[5].textContent = `${totalIntSavings > 0 ? '+' : ''}${formatWon(totalIntSavings)}`;
        targets[6].textContent = formatWon(penalty);
        targets[7].textContent = formatWon(switchingCost);
        targets[8].textContent = `${netSavings > 0 ? '+' : ''}${formatWon(netSavings)}`;
        targets[9].textContent = breakEven > 0 ? `${breakEven}개월` : '회수 어려움';
        if (breakEven > 0) {
          const beDate = new Date();
          beDate.setMonth(beDate.getMonth() + breakEven);
          targets[10].textContent = `${beDate.getFullYear()}-${String(beDate.getMonth() + 1).padStart(2, '0')}-01`;
        } else {
          targets[10].textContent = '-';
        }
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 4. 주담대 월납입 계산기 (mortgage-monthly-payment-calculator) */
  const mortgageForm = document.getElementById('housePrice')?.closest('form');
  if (mortgageForm) {
    const layout = mortgageForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    mortgageForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(mortgageForm)) return;
      trackEvent('calculator_start');

      const housePrice = parseNum('housePrice');
      const cashOnHand = parseNum('cashOnHand');
      const expectedLoan = parseNum('expectedLoanAmount');
      const annualRate = parseNum('annualRate');
      const years = parseNum('years');
      const repaymentType = document.getElementById('repaymentType')?.value || 'EQUAL_PAYMENT';
      const ltvRatio = parseNum('ltvRatio');
      const annualIncome = parseNum('annualIncome');
      const debtPayment = parseNum('existingMonthlyDebtPayment');

      const monthlyRate = annualRate / 100 / 12;
      const months = Math.max(1, years * 12);

      let monthlyPayment = 0;
      let totalInterest = 0;

      if (repaymentType === 'EQUAL_PRINCIPAL') {
        monthlyPayment = expectedLoan / months + expectedLoan * monthlyRate;
        let rem = expectedLoan;
        const fixedP = expectedLoan / months;
        for (let i = 1; i <= months; i++) {
          totalInterest += rem * monthlyRate;
          rem = Math.max(0, rem - fixedP);
        }
      } else if (repaymentType === 'BULLET') {
        monthlyPayment = expectedLoan * monthlyRate;
        totalInterest = expectedLoan * monthlyRate * months;
      } else {
        if (monthlyRate === 0) {
          monthlyPayment = expectedLoan / months;
          totalInterest = 0;
        } else {
          const factor = Math.pow(1 + monthlyRate, months);
          monthlyPayment = expectedLoan * monthlyRate * factor / (factor - 1);
          totalInterest = monthlyPayment * months - expectedLoan;
        }
      }

      const totalRepayment = expectedLoan + totalInterest;
      const requiredEquity = Math.max(0, housePrice - expectedLoan);
      const maxLoanByLtv = housePrice * (ltvRatio / 100);
      const monthlyIncome = annualIncome / 12;
      const dtiRatio = monthlyIncome > 0 ? (monthlyPayment / monthlyIncome) * 100 : 0;
      const dsrRatio = monthlyIncome > 0 ? ((monthlyPayment + debtPayment) / monthlyIncome) * 100 : 0;
      const cashShortfall = Math.max(0, requiredEquity - cashOnHand);

      let riskLabel = '안전';
      let riskClass = 'safe';
      if (dsrRatio >= 70 || cashShortfall > 0 || expectedLoan > maxLoanByLtv) {
        riskLabel = '위험';
        riskClass = 'danger';
      } else if (dsrRatio >= 40 || dtiRatio >= 40) {
        riskLabel = '주의';
        riskClass = 'caution';
      }

      const riskSummary = resultPanel?.querySelector('.risk-summary');
      if (riskSummary) {
        const strong = riskSummary.querySelector('strong');
        if (strong) strong.textContent = riskLabel;
        riskSummary.className = `risk-summary risk-${riskClass}`;
      }

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 8) {
        targets[0].textContent = formatWon(monthlyPayment);
        targets[1].textContent = formatWon(totalInterest);
        targets[2].textContent = formatWon(totalRepayment);
        targets[3].textContent = formatWon(requiredEquity);
        targets[4].textContent = formatWon(maxLoanByLtv);
        targets[5].textContent = `${formatComma(dtiRatio, 1)}%`;
        targets[6].textContent = `${formatComma(dsrRatio, 1)}%`;
        targets[7].textContent = formatWon(cashShortfall);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 5. 배당금 계산기 (dividend-calculator) */
  const dividendForm = document.getElementById('dividendPerShare')?.closest('form');
  if (dividendForm) {
    const layout = dividendForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    dividendForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(dividendForm)) return;
      trackEvent('calculator_start');

      const shares = parseNum('shares');
      const dividendPerShare = parseNum('dividendPerShare');
      const period = document.getElementById('period')?.value || 'QUARTERLY';
      const taxApplied = document.getElementById('taxApplied')?.checked ?? true;
      const taxRate = parseNum('taxRate');

      const multiplier = { MONTHLY: 12, SEMI_ANNUAL: 2, ANNUAL: 1, QUARTERLY: 4 }[period] || 4;
      const gross = shares * dividendPerShare;
      const taxFactor = taxApplied ? Math.max(0, 1 - taxRate / 100) : 1;
      const net = gross * taxFactor;
      const annualGross = gross * multiplier;
      const annualNet = net * multiplier;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 4) {
        targets[0].textContent = formatWon(gross);
        targets[1].textContent = formatWon(net);
        targets[2].textContent = formatWon(annualNet / 12);
        targets[3].textContent = formatWon(annualNet);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 7. 월급 실수령액 계산기 (salary-calculator) */
  const salaryForm = document.getElementById('amount')?.closest('form');
  if (salaryForm && !salaryForm.hasAttribute('data-annual-salary-calculator')) {
    const layout = salaryForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    salaryForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(salaryForm)) return;
      trackEvent('calculator_start');

      const incomeType = document.getElementById('incomeType')?.value || 'MONTHLY';
      const amount = parseNum('amount');
      const taxFreeAmount = parseNum('taxFreeAmount');
      const dependents = Math.max(1, parseNum('dependents'));
      const children = Math.max(0, parseNum('children'));
      const applyInsurance = document.getElementById('applyInsurance')?.checked ?? true;

      const grossMonthly = incomeType === 'ANNUAL' ? amount / 12.0 : amount;
      const taxableMonthly = Math.max(0, grossMonthly - taxFreeAmount);

      let nationalPension = 0, healthInsurance = 0, longTermCareInsurance = 0, employmentInsurance = 0;
      if (applyInsurance && taxableMonthly > 0) {
        const pensionBase = Math.min(Math.max(taxableMonthly, 410_000), 6_590_000);
        nationalPension = Math.round(pensionBase * 0.0475);
        healthInsurance = Math.round(taxableMonthly * 0.03595);
        longTermCareInsurance = Math.round(healthInsurance * (0.009448 / 0.0719));
        employmentInsurance = Math.round(taxableMonthly * 0.009);
      }

      const annualTaxableGross = taxableMonthly * 12.0;
      let annualIncomeTax = 0;
      if (annualTaxableGross > 0) {
        let eDed = 0;
        if (annualTaxableGross <= 5_000_000) eDed = annualTaxableGross * 0.70;
        else if (annualTaxableGross <= 15_000_000) eDed = 3_500_000 + (annualTaxableGross - 5_000_000) * 0.40;
        else if (annualTaxableGross <= 45_000_000) eDed = 7_500_000 + (annualTaxableGross - 15_000_000) * 0.15;
        else if (annualTaxableGross <= 100_000_000) eDed = 12_000_000 + (annualTaxableGross - 45_000_000) * 0.05;
        else eDed = Math.min(20_000_000, 14_750_000 + (annualTaxableGross - 100_000_000) * 0.02);

        const basicDeduction = dependents * 1_500_000;
        const bucket = Math.min(dependents, 3);
        let proxySpecial = 0;
        if (annualTaxableGross <= 30_000_000) {
          proxySpecial = bucket === 1 ? 3_100_000 + annualTaxableGross * 0.04 : bucket === 2 ? 3_600_000 + annualTaxableGross * 0.04 : 5_000_000 + annualTaxableGross * 0.07 + Math.max(0, annualTaxableGross - 40_000_000) * 0.04;
        } else if (annualTaxableGross <= 45_000_000) {
          proxySpecial = bucket === 1 ? 3_100_000 + annualTaxableGross * 0.04 - (annualTaxableGross - 30_000_000) * 0.05 : bucket === 2 ? 3_600_000 + annualTaxableGross * 0.04 - (annualTaxableGross - 30_000_000) * 0.05 : 5_000_000 + annualTaxableGross * 0.07 - (annualTaxableGross - 30_000_000) * 0.05;
        } else if (annualTaxableGross <= 70_000_000) {
          proxySpecial = bucket === 1 ? 3_100_000 + annualTaxableGross * 0.015 : bucket === 2 ? 3_600_000 + annualTaxableGross * 0.02 : 5_000_000 + annualTaxableGross * 0.05;
        } else {
          proxySpecial = bucket === 1 ? 3_100_000 + annualTaxableGross * 0.005 : bucket === 2 ? 3_600_000 + annualTaxableGross * 0.01 : 5_000_000 + annualTaxableGross * 0.03;
        }

        const taxBase = Math.max(0, annualTaxableGross - eDed - basicDeduction - proxySpecial - (nationalPension * 12.0));
        let calcTax = 0;
        if (taxBase <= 14_000_000) calcTax = taxBase * 0.06;
        else if (taxBase <= 50_000_000) calcTax = 840_000 + (taxBase - 14_000_000) * 0.15;
        else if (taxBase <= 88_000_000) calcTax = 6_240_000 + (taxBase - 50_000_000) * 0.24;
        else if (taxBase <= 150_000_000) calcTax = 15_360_000 + (taxBase - 88_000_000) * 0.35;
        else if (taxBase <= 300_000_000) calcTax = 37_060_000 + (taxBase - 150_000_000) * 0.38;
        else if (taxBase <= 500_000_000) calcTax = 94_060_000 + (taxBase - 300_000_000) * 0.40;
        else if (taxBase <= 1_000_000_000) calcTax = 174_060_000 + (taxBase - 500_000_000) * 0.42;
        else calcTax = 384_060_000 + (taxBase - 1_000_000_000) * 0.45;

        let rawCredit = calcTax <= 1_300_000 ? calcTax * 0.55 : 715_000 + (calcTax - 1_300_000) * 0.30;
        let limit = 740_000;
        if (annualTaxableGross > 33_000_000 && annualTaxableGross <= 70_000_000) limit = Math.max(660_000, 740_000 - (annualTaxableGross - 33_000_000) * 0.008);
        else if (annualTaxableGross > 70_000_000 && annualTaxableGross <= 120_000_000) limit = Math.max(500_000, 660_000 - (annualTaxableGross - 70_000_000) * 0.50);
        else if (annualTaxableGross > 120_000_000) limit = Math.max(200_000, 500_000 - (annualTaxableGross - 120_000_000) * 0.50);

        annualIncomeTax = Math.max(0, calcTax - Math.min(rawCredit, limit));
      }

      const eligibleChildren = Math.min(children, Math.max(0, dependents - 1));
      let childCredit = 0;
      if (eligibleChildren === 1) childCredit = 20_830;
      else if (eligibleChildren >= 2) childCredit = 45_830 + (eligibleChildren - 2) * 33_330;

      const incomeTax = Math.max(0, Math.round(annualIncomeTax / 12.0) - childCredit);
      const localIncomeTax = Math.round(incomeTax * 0.1);
      const totalDeduction = nationalPension + healthInsurance + longTermCareInsurance + employmentInsurance + incomeTax + localIncomeTax;
      const netMonthly = Math.max(0, grossMonthly - totalDeduction);

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 10) {
        targets[0].textContent = formatWon(grossMonthly);
        targets[1].textContent = formatWon(nationalPension);
        targets[2].textContent = formatWon(healthInsurance);
        targets[3].textContent = formatWon(longTermCareInsurance);
        targets[4].textContent = formatWon(employmentInsurance);
        targets[5].textContent = formatWon(incomeTax);
        targets[6].textContent = formatWon(localIncomeTax);
        targets[7].textContent = formatWon(totalDeduction);
        targets[8].textContent = formatWon(netMonthly);
        targets[9].textContent = formatWon(netMonthly * 12);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 8. 퇴직금 계산기 (severance-pay-calculator) */
  const severanceForm = document.getElementById('ordinaryDailyWage')?.closest('form');
  if (severanceForm) {
    const layout = severanceForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    severanceForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(severanceForm)) return;
      trackEvent('calculator_start');

      const startDate = document.getElementById('startDate')?.value;
      const endDate = document.getElementById('endDate')?.value;
      const wage3Months = parseNum('totalWageForLastThreeMonths');
      const annualBonus = parseNum('annualBonus');
      const leaveAllowance = parseNum('annualLeaveAllowance');
      const ordinaryDailyWage = parseNum('ordinaryDailyWage');

      if (!startDate || !endDate) {
        alert('입사일과 퇴사일을 입력해주세요.');
        return;
      }

      const start = new Date(startDate);
      const end = new Date(endDate);
      const msPerDay = 1000 * 60 * 60 * 24;
      const serviceDays = Math.max(0, Math.round((end - start) / msPerDay) + 1);

      const periodStart = new Date(end);
      periodStart.setMonth(periodStart.getMonth() - 3);
      const calculationPeriodDays = Math.max(1, Math.round((end - periodStart) / msPerDay));

      const bonusInc = annualBonus * 3 / 12;
      const leaveInc = leaveAllowance * 3 / 12;
      const threeMonthTotal = wage3Months + bonusInc + leaveInc;

      const averageDailyWage = threeMonthTotal / calculationPeriodDays;
      const appliedDailyWage = Math.max(averageDailyWage, ordinaryDailyWage);
      const severance = serviceDays < 365 ? 0 : appliedDailyWage * 30 * serviceDays / 365;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 6) {
        targets[0].textContent = `${formatComma(serviceDays)}일`;
        targets[1].textContent = `${formatComma(calculationPeriodDays)}일`;
        targets[2].textContent = formatWon(averageDailyWage);
        targets[3].textContent = formatWon(ordinaryDailyWage);
        targets[4].textContent = formatWon(appliedDailyWage);
        targets[5].textContent = formatWon(severance);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 9. 연차수당 계산기 (annual-leave-pay-calculator) */
  const annualLeaveForm = document.getElementById('unusedLeaveDays')?.closest('form');
  if (annualLeaveForm) {
    const layout = annualLeaveForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    annualLeaveForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(annualLeaveForm)) return;
      trackEvent('calculator_start');

      const unusedLeaveDays = parseNum('unusedLeaveDays');
      const dailyOrdinaryWage = parseNum('dailyOrdinaryWage');
      const allowance = unusedLeaveDays * dailyOrdinaryWage;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 3) {
        targets[0].textContent = `${formatComma(unusedLeaveDays, 2)}일`;
        targets[1].textContent = formatWon(dailyOrdinaryWage);
        targets[2].textContent = formatWon(allowance);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 10. 적정주가 계산기 (fair-value-calculator) */
  const fairValueForm = document.getElementById('eps')?.closest('form');
  if (fairValueForm) {
    const layout = fairValueForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    fairValueForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(fairValueForm)) return;
      trackEvent('calculator_start');

      const eps = parseNum('eps');
      const targetPer = parseNum('targetPer');
      const growthRate = parseNum('growthRate');
      const discountRate = parseNum('discountRate');
      const safetyMargin = parseNum('safetyMargin');

      const base = eps * targetPer;
      const growthAdjusted = base * (1 + growthRate / 100) / (1 + discountRate / 100);
      const safeBuy = growthAdjusted * (1 - safetyMargin / 100);

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 6) {
        targets[0].textContent = formatWon(base);
        targets[1].textContent = formatWon(growthAdjusted);
        targets[2].textContent = formatWon(safeBuy);
        targets[3].textContent = formatWon(safeBuy * 0.85);
        targets[4].textContent = formatWon(safeBuy);
        targets[5].textContent = formatWon(safeBuy * 1.15);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 11. 환전 계산기 (exchange-calculator) */
  const exchangeForm = document.getElementById('fromCurrency')?.closest('form');
  if (exchangeForm) {
    const layout = exchangeForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    exchangeForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(exchangeForm)) return;
      trackEvent('calculator_start');

      const from = document.getElementById('fromCurrency')?.value || 'USD';
      const to = document.getElementById('toCurrency')?.value || 'KRW';
      const amount = parseNum('amount');
      const exchangeRate = parseNum('exchangeRate');
      const targetExchangeRate = parseNum('targetExchangeRate');
      const feeRate = parseNum('feeRate') / 100;

      let beforeFeeTarget = 0;
      let beforeFeeKrw = 0;
      let krwPerTarget = 1;

      if (from === to) {
        beforeFeeTarget = amount;
        beforeFeeKrw = from === 'KRW' ? amount : amount * exchangeRate;
        krwPerTarget = from === 'KRW' ? 1 : exchangeRate;
      } else if (to === 'KRW') {
        beforeFeeKrw = amount * exchangeRate;
        beforeFeeTarget = beforeFeeKrw;
        krwPerTarget = 1;
      } else if (from === 'KRW') {
        beforeFeeTarget = exchangeRate > 0 ? amount / exchangeRate : 0;
        beforeFeeKrw = amount;
        krwPerTarget = exchangeRate;
      } else {
        const sourceKrw = amount * exchangeRate;
        beforeFeeTarget = targetExchangeRate > 0 ? sourceKrw / targetExchangeRate : 0;
        beforeFeeKrw = sourceKrw;
        krwPerTarget = targetExchangeRate;
      }

      const feeTarget = beforeFeeTarget * feeRate;
      const afterFeeTarget = Math.max(0, beforeFeeTarget - feeTarget);
      const feeKrw = feeTarget * krwPerTarget;
      const afterFeeKrw = afterFeeTarget * krwPerTarget;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 7) {
        targets[0].textContent = formatComma(beforeFeeTarget, 2);
        targets[1].textContent = formatComma(feeTarget, 2);
        targets[2].textContent = to;
        targets[3].textContent = formatComma(afterFeeTarget, 2);
        targets[4].textContent = formatWon(beforeFeeKrw);
        targets[5].textContent = formatWon(feeKrw);
        targets[6].textContent = formatWon(afterFeeKrw);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 12. 전기요금 계산기 (electricity-bill-calculator) */
  const electricityForm = document.getElementById('usageKwh')?.closest('form');
  if (electricityForm) {
    const layout = electricityForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    electricityForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(electricityForm)) return;
      trackEvent('calculator_start');

      const usage = parseNum('usageKwh');
      const prevUsage = parseNum('previousUsageKwh');
      const season = document.getElementById('season')?.value || 'SPRING_FALL';

      const isSummer = season === 'SUMMER';
      const tier1Limit = isSummer ? 300 : 200;
      const tier2Limit = isSummer ? 450 : 400;

      const baseFee = usage <= tier1Limit ? 910 : usage <= tier2Limit ? 1600 : 7300;
      const t1 = Math.min(usage, tier1Limit);
      const t2 = Math.min(Math.max(0, usage - tier1Limit), tier2Limit - tier1Limit);
      const t3 = Math.max(0, usage - tier2Limit);
      const energyCharge = t1 * 120.0 + t2 * 214.6 + t3 * 307.3;
      const climateCharge = usage * 9.0;
      const fuelAdj = usage * 5.0;

      const subtotal = baseFee + energyCharge + climateCharge + fuelAdj;
      const vat = Math.round(subtotal * 0.1);
      const fund = Math.floor((subtotal * 0.027) / 10) * 10;
      const total = Math.floor((subtotal + vat + fund) / 10) * 10;
      const avgUnit = usage > 0 ? total / usage : 0;
      const usageDelta = usage - prevUsage;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 9) {
        targets[0].textContent = formatWon(baseFee);
        targets[1].textContent = formatWon(energyCharge);
        targets[2].textContent = formatWon(climateCharge);
        targets[3].textContent = formatWon(fuelAdj);
        targets[4].textContent = formatWon(vat);
        targets[5].textContent = formatWon(fund);
        targets[6].textContent = formatWon(total);
        targets[7].textContent = `${formatComma(avgUnit, 1)}원/kWh`;
        targets[8].textContent = `${usageDelta > 0 ? '+' : ''}${formatComma(usageDelta)}kWh`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 13. 에어컨 전기요금 계산기 (air-conditioner-electricity-calculator) */
  const airconForm = document.getElementById('powerWatts')?.closest('form');
  if (airconForm) {
    const layout = airconForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    airconForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(airconForm)) return;
      trackEvent('calculator_start');

      const powerW = parseNum('powerWatts');
      const hours = Math.min(24, Math.max(0, parseNum('hoursPerDay')));
      const standbyHours = 24 - hours;
      const loadFactor = Math.min(1, Math.max(0, parseNum('loadFactor')));
      const days = parseNum('daysPerMonth');
      const ratePerKwh = parseNum('electricityRatePerKwh');
      const standbyW = parseNum('standbyWatts');
      const baseHouseholdKwh = parseNum('householdUsageKwh');
      const season = document.getElementById('season')?.value || 'SUMMER';

      const activeKwh = (powerW / 1000) * hours * days * loadFactor;
      const standbyKwh = (standbyW / 1000) * standbyHours * days;
      const totalKwh = activeKwh + standbyKwh;
      const standaloneCost = totalKwh * ratePerKwh;
      const dailyCost = days > 0 ? standaloneCost / days : 0;
      const totalActiveHours = hours * days;
      const hourlyCost = totalActiveHours > 0 ? standaloneCost / totalActiveHours : 0;
      const totalHouseholdKwh = baseHouseholdKwh + totalKwh;

      const calcElec = (kwh, s) => {
        const isSum = s === 'SUMMER';
        const t1Lim = isSum ? 300 : 200;
        const t2Lim = isSum ? 450 : 400;
        const bFee = kwh <= t1Lim ? 910 : kwh <= t2Lim ? 1600 : 7300;
        const eChg = Math.min(kwh, t1Lim) * 120.0 + Math.min(Math.max(0, kwh - t1Lim), t2Lim - t1Lim) * 214.6 + Math.max(0, kwh - t2Lim) * 307.3;
        const sub = bFee + eChg + kwh * 9.0 + kwh * 5.0;
        const v = Math.round(sub * 0.1);
        const f = Math.floor((sub * 0.027) / 10) * 10;
        return Math.floor((sub + v + f) / 10) * 10;
      };

      const baseBill = calcElec(baseHouseholdKwh, season);
      const totalBill = calcElec(totalHouseholdKwh, season);
      const incCost = Math.max(0, totalBill - baseBill);

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 9) {
        targets[0].textContent = `${formatComma(activeKwh, 1)}kWh`;
        targets[1].textContent = `${formatComma(standbyKwh, 1)}kWh`;
        targets[2].textContent = `${formatComma(totalKwh, 1)}kWh`;
        targets[3].textContent = formatWon(standaloneCost);
        targets[4].textContent = formatWon(dailyCost);
        targets[5].textContent = formatWon(hourlyCost);
        targets[6].textContent = `${formatComma(totalHouseholdKwh, 1)}kWh`;
        targets[7].textContent = formatWon(incCost);
        targets[8].textContent = formatWon(totalBill);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 14. 자동차 유지비 계산기 (car-maintenance-calculator) */
  const carForm = document.getElementById('monthlyDistanceKm')?.closest('form');
  if (carForm) {
    const layout = carForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    carForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(carForm)) return;
      trackEvent('calculator_start');

      const dist = parseNum('monthlyDistanceKm');
      const eff = parseNum('fuelEfficiencyKmPerLiter');
      const fuelP = parseNum('fuelPricePerLiter');
      const parking = parseNum('parkingFeeMonthly');
      const ins = parseNum('insuranceAnnual');
      const tax = parseNum('taxAnnual');
      const maint = parseNum('maintenanceAnnual');
      const toll = parseNum('tollMonthly');
      const installment = parseNum('installmentMonthly');

      const fuelMonthly = eff > 0 ? (dist / eff) * fuelP : 0;
      const fixedMonthly = parking + ins / 12 + tax / 12 + installment;
      const variableMonthly = fuelMonthly + maint / 12 + toll;
      const totalMonthly = fixedMonthly + variableMonthly;
      const totalAnnual = totalMonthly * 12;
      const costPerKm = dist > 0 ? totalMonthly / dist : 0;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 6) {
        targets[0].textContent = formatWon(fuelMonthly);
        targets[1].textContent = formatWon(fixedMonthly);
        targets[2].textContent = formatWon(variableMonthly);
        targets[3].textContent = formatWon(totalMonthly);
        targets[4].textContent = formatWon(totalAnnual);
        targets[5].textContent = `${formatComma(costPerKm, 1)}원/km`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 15. 월 생활비 계산기 (monthly-budget-calculator) */
  const budgetForm = document.getElementById('monthlyIncome')?.closest('form');
  if (budgetForm) {
    const layout = budgetForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    budgetForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(budgetForm)) return;
      trackEvent('calculator_start');

      const income = parseNum('monthlyIncome');
      const fixedExp = parseNum('housing') + parseNum('communication') + parseNum('insurance') + parseNum('education') + parseNum('subscriptions');
      const varExp = parseNum('food') + parseNum('transport') + parseNum('leisure') + parseNum('other');
      const totalExp = fixedExp + varExp;
      const remaining = income - totalExp;
      const savings = parseNum('savingsGoal');
      const remAfterSavings = remaining - savings;
      const expRatio = income > 0 ? (totalExp / income) * 100 : 0;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 6) {
        targets[0].textContent = formatWon(fixedExp);
        targets[1].textContent = formatWon(varExp);
        targets[2].textContent = formatWon(totalExp);
        targets[3].textContent = formatWon(remaining);
        targets[4].textContent = formatWon(remAfterSavings);
        targets[5].textContent = `${formatComma(expRatio, 1)}%`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* 17. 해외주식 세금 계산기 (overseas-stock-tax-calculator) */
  const overseasForm = document.getElementById('buyAmountForeign')?.closest('form');
  if (overseasForm) {
    const layout = overseasForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    overseasForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateNumberInputsInForm(overseasForm)) return;
      trackEvent('calculator_start');

      const buyForeign = parseNum('buyAmountForeign');
      const buyExRate = parseNum('buyExchangeRate');
      const sellForeign = parseNum('sellAmountForeign');
      const sellExRate = parseNum('sellExchangeRate');
      const feeKrw = parseNum('feeKrw');
      const applyDeduction = document.getElementById('applyBasicDeduction')?.checked ?? true;
      const deductionKrw = parseNum('basicDeductionKrw');
      const capTaxRate = parseNum('capitalGainsTaxRate') / 100;
      const divForeign = parseNum('dividendForeign');
      const divExRate = parseNum('dividendExchangeRate');
      const divTaxRate = parseNum('dividendTaxRate') / 100;

      const buyKrw = buyForeign * buyExRate;
      const sellKrw = sellForeign * sellExRate;
      const capitalGain = sellKrw - buyKrw - feeKrw;
      const deduction = applyDeduction ? deductionKrw : 0;
      const taxableCapGain = Math.max(0, capitalGain - deduction);
      const capitalTax = taxableCapGain * capTaxRate;

      const dividendKrw = divForeign * divExRate;
      const dividendTax = dividendKrw * divTaxRate;
      const totalTax = capitalTax + dividendTax;
      const afterTaxProfit = capitalGain + dividendKrw - totalTax;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 9) {
        targets[0].textContent = formatWon(buyKrw);
        targets[1].textContent = formatWon(sellKrw);
        targets[2].textContent = formatWon(capitalGain);
        targets[3].textContent = formatWon(taxableCapGain);
        targets[4].textContent = formatWon(capitalTax);
        targets[5].textContent = formatWon(dividendKrw);
        targets[6].textContent = formatWon(dividendTax);
        targets[7].textContent = formatWon(totalTax);
        targets[8].textContent = formatWon(afterTaxProfit);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

});
