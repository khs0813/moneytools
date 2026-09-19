/**
 * 머니계산기 클라이언트 스크립트
 * 모든 계산기 폼을 서버 없이 브라우저에서 순수 JavaScript로 즉시 계산 및 렌더링합니다.
 */

document.addEventListener('DOMContentLoaded', () => {
  const MAX_NUMERIC_TEXT_LENGTH = 128;
  const MAX_SCIENTIFIC_EXPONENT_ABS = 128;

  // 사이드바 토글
  const sidebar = document.getElementById('sidebar');
  const toggle = document.querySelector('[data-menu-toggle]');
  if (toggle && sidebar) {
    toggle.addEventListener('click', () => sidebar.classList.toggle('open'));
  }

  // 결과 복사 버튼
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

  // CSV 다운로드 버튼
  document.querySelectorAll('[data-download-table]').forEach((button) => {
    button.addEventListener('click', () => {
      const tableId = button.getAttribute('data-download-table');
      const table = document.getElementById(tableId);
      if (!table) return;
      const rows = Array.from(table.querySelectorAll('tr')).map((row) =>
        Array.from(row.querySelectorAll('th,td'))
          .map((cell) => `"${cell.innerText.replaceAll('"', '""')}"`)
          .join(',')
      );
      const blob = new Blob(['\ufeff' + rows.join('\n')], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${tableId}.csv`;
      link.click();
      URL.revokeObjectURL(url);
    });
  });

  // Flatpickr 달력 초기화
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

  // 숫자 입력 포매팅 유틸리티
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
    if (!Number.isFinite(exponent) || Math.abs(exponent) > MAX_SCIENTIFIC_EXPONENT_ABS) return '';
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
    if (/[eE]/.test(working)) working = toPlainFromScientific(working);
    if (working.length > MAX_NUMERIC_TEXT_LENGTH) return '';
    let sign = '';
    if (working.startsWith('+') || working.startsWith('-')) {
      sign = working.startsWith('-') ? '-' : '';
      working = working.slice(1);
    }
    if (!working) return '';
    const [rawInteger = '', rawFraction = ''] = working.split('.');
    let integerPart = rawInteger.replace(/^0+(?=\d)/, '') || '0';
    const fractionPart = rawFraction.replace(/0+$/, '');
    if (integerPart === '0' && !fractionPart) sign = '';
    return sign + integerPart + (fractionPart ? `.${fractionPart}` : '');
  };

  const keepLeadingSign = (value, allowSign) => {
    if (!allowSign) return { sign: '', body: value.replace(/[+-]/g, '') };
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
    if (firstDotIndex === -1) return sign + filtered;
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

  const getDisplayValue = (value, options) => {
    if (options.mode === 'integer') return formatIntegerDisplay(value, options.allowSign);
    return formatDecimalDisplay(value, options.allowSign);
  };

  const getCaretPositionFromDigits = (formattedValue, digitCount) => {
    if (digitCount <= 0) return 0;
    let seenDigits = 0;
    for (let index = 0; index < formattedValue.length; index += 1) {
      if (/\d/.test(formattedValue[index])) {
        seenDigits += 1;
        if (seenDigits >= digitCount) return index + 1;
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
    input.addEventListener('focus', () => {
      const formatted = getDisplayValue(input.value, options);
      if (formatted !== input.value) input.value = formatted;
    });
    input.addEventListener('input', () => applyFormattedValue(input, options));
    input.addEventListener('change', () => applyFormattedValue(input, options));
    input.addEventListener('paste', () => window.requestAnimationFrame(() => applyFormattedValue(input, options)));
    input.addEventListener('keyup', () => applyFormattedValue(input, options));
    input.addEventListener('blur', () => {
      const formatted = getDisplayValue(input.value, options);
      if (formatted !== input.value) input.value = formatted;
    });
    const initialValue = getDisplayValue(input.value, options);
    if (initialValue && initialValue !== input.value) input.value = initialValue;
  });

  // 숫자 파싱 헬퍼
  const parseNum = (idOrElem) => {
    const elem = typeof idOrElem === 'string' ? document.getElementById(idOrElem) : idOrElem;
    if (!elem) return 0;
    const plain = normalizePlainNumber(elem.value ?? '');
    const num = Number(plain);
    return Number.isFinite(num) ? num : 0;
  };

  const formatWon = (value) => `${Math.round(Math.max(0, Number(value) || 0)).toLocaleString('ko-KR')}원`;
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
  };

  /* ====================================================
     1. 대출이자 계산기 (loan-interest-calculator)
  ==================================================== */
  const loanForm = document.querySelector('form[action*="loan-interest-calculator"], [data-loan-calculator]');
  if (loanForm) {
    const layout = loanForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');
    const scheduleSection = document.querySelector('[data-loan-schedule-section], section.content-card:has(#loan-schedule)');
    const tableBody = document.querySelector('#loan-schedule tbody');

    loanForm.addEventListener('submit', (e) => {
      e.preventDefault();
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
        let remaining = principal;
        const monthlyPayment = monthlyRate === 0
          ? principal / months
          : principal * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1);
        for (let m = 1; m <= months; m++) {
          const interest = remaining * monthlyRate;
          const principalPayment = Math.min(remaining, monthlyPayment - interest);
          remaining = Math.max(0, remaining - principalPayment);
          schedule.push({ month: m, payment: principalPayment + interest, principalPayment, interestPayment: interest, remainingPrincipal: remaining });
        }
      }

      const totalPayment = schedule.reduce((sum, r) => sum + r.payment, 0);
      const totalInterest = schedule.reduce((sum, r) => sum + r.interestPayment, 0);
      const firstPayment = schedule[0]?.payment || 0;
      const averagePayment = months === 0 ? 0 : totalPayment / months;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 4) {
        targets[0].textContent = formatWon(firstPayment);
        targets[1].textContent = formatWon(averagePayment);
        targets[2].textContent = formatWon(totalInterest);
        targets[3].textContent = formatWon(totalPayment);
      }

      if (tableBody) {
        tableBody.innerHTML = schedule.map((row) => `
          <tr>
            <td>${row.month}</td>
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

  /* ====================================================
     2. 물타기 계산기 (stock-average-calculator)
  ==================================================== */
  const stockAvgForm = document.querySelector('form[action*="stock-average-calculator"], [data-stock-average-calculator]');
  if (stockAvgForm) {
    const layout = stockAvgForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    stockAvgForm.addEventListener('submit', (e) => {
      e.preventDefault();
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
      const averagePriceChangeRate = currentAveragePrice === 0 ? 0 : (averagePriceChange / currentAveragePrice) * 100;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 7) {
        targets[0].textContent = formatWon(currentInvestment);
        targets[1].textContent = formatWon(additionalInvestment);
        targets[2].textContent = `${formatComma(totalShares)}주`;
        targets[3].textContent = formatWon(totalInvestment);
        targets[4].textContent = formatWon(newAveragePrice);
        targets[5].textContent = `${averagePriceChange > 0 ? '+' : ''}${formatWon(averagePriceChange)}`;
        targets[6].textContent = `${averagePriceChangeRate > 0 ? '+' : ''}${formatComma(averagePriceChangeRate, 2)}%`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     3. 대출 갈아타기 계산기 (loan-refinance-calculator)
  ==================================================== */
  const loanRefinanceForm = document.querySelector('form[action*="loan-refinance-calculator"], [data-loan-refinance-calculator]');
  if (loanRefinanceForm) {
    const layout = loanRefinanceForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    loanRefinanceForm.addEventListener('submit', (e) => {
      e.preventDefault();
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

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 10) {
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
      }
      const riskSummary = resultPanel?.querySelector('.risk-summary');
      if (riskSummary) {
        const strong = riskSummary.querySelector('strong');
        if (strong) strong.textContent = rec;
        riskSummary.className = `risk-summary risk-${recClass}`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     4. 주담대 월납입 계산기 (mortgage-monthly-payment-calculator)
  ==================================================== */
  const mortgageForm = document.querySelector('form[action*="mortgage-monthly-payment-calculator"], [data-mortgage-calculator]');
  if (mortgageForm) {
    const layout = mortgageForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    mortgageForm.addEventListener('submit', (e) => {
      e.preventDefault();
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
        const factor = Math.pow(1 + monthlyRate, months);
        monthlyPayment = monthlyRate === 0 ? expectedLoan / months : expectedLoan * monthlyRate * factor / (factor - 1);
        totalInterest = monthlyPayment * months - expectedLoan;
      }

      const totalPayment = expectedLoan + totalInterest;
      const requiredEquity = Math.max(0, housePrice - expectedLoan);
      const cashShortfall = Math.max(0, requiredEquity - cashOnHand);
      const maxLoanByLtv = housePrice * ltvRatio / 100;
      const monthlyIncome = annualIncome / 12;
      const burdenRate = monthlyIncome === 0 ? 0 : (monthlyPayment / monthlyIncome) * 100;
      const burdenRateWithDebt = monthlyIncome === 0 ? 0 : ((monthlyPayment + debtPayment) / monthlyIncome) * 100;

      let riskLabel = '안전';
      let riskClass = 'safe';
      if (burdenRateWithDebt > 40) {
        riskLabel = '위험';
        riskClass = 'danger';
      } else if (burdenRateWithDebt > 25) {
        riskLabel = '주의';
        riskClass = 'caution';
      }

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 8) {
        targets[0].textContent = formatWon(monthlyPayment);
        targets[1].textContent = formatWon(totalInterest);
        targets[2].textContent = formatWon(totalPayment);
        targets[3].textContent = formatWon(requiredEquity);
        targets[4].textContent = formatWon(maxLoanByLtv);
        targets[5].textContent = `${formatComma(burdenRate, 1)}%`;
        targets[6].textContent = `${formatComma(burdenRateWithDebt, 1)}%`;
        targets[7].textContent = formatWon(cashShortfall);
      }
      const riskSummary = resultPanel?.querySelector('.risk-summary');
      if (riskSummary) {
        const strong = riskSummary.querySelector('strong');
        if (strong) strong.textContent = riskLabel;
        riskSummary.className = `risk-summary risk-${riskClass}`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     5. 배당금 계산기 (dividend-calculator)
  ==================================================== */
  const dividendForm = document.querySelector('form[action*="dividend-calculator"], [data-dividend-calculator]');
  if (dividendForm) {
    const layout = dividendForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    dividendForm.addEventListener('submit', (e) => {
      e.preventDefault();
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
      if (targets && targets.length >= 6) {
        targets[0].textContent = formatWon(gross);
        targets[1].textContent = formatWon(net);
        targets[2].textContent = formatWon(annualGross / 12);
        targets[3].textContent = formatWon(annualNet / 12);
        targets[4].textContent = formatWon(annualGross);
        targets[5].textContent = formatWon(annualNet);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     6. 연봉 실수령액 계산기 (annual-salary-net-calculator)
  ==================================================== */
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

  const annualSalaryForm = document.querySelector('[data-annual-salary-calculator], form[action*="annual-salary-net-calculator"]');
  if (annualSalaryForm) {
    const layout = annualSalaryForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');
    const resultTargets = Object.fromEntries(
      Array.from(document.querySelectorAll('[data-salary-result]')).map((el) => [el.dataset.salaryResult, el])
    );

    annualSalaryForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const annualSalary = parseNum('annualSalary');
      const monthlyTaxFreeMeal = parseNum('monthlyTaxFreeMeal');
      const dependents = Math.max(1, parseNum('dependents'));
      const childrenUnder20 = Math.max(0, parseNum('childrenUnder20'));
      const retirementIncluded = document.getElementById('retirementIncluded')?.checked ?? false;
      const monthlyBonusEnabled = document.getElementById('monthlyBonusEnabled')?.checked ?? false;
      const monthlyBonusAmount = parseNum('monthlyBonusAmount');

      const baseMonthly = retirementIncluded ? annualSalary / 13 : annualSalary / 12;
      const bonus = monthlyBonusEnabled ? monthlyBonusAmount : 0;
      const grossMonthly = baseMonthly + bonus;
      const taxableMonthly = Math.max(0, grossMonthly - monthlyTaxFreeMeal);

      const nationalPension = taxableMonthly * ANNUAL_SALARY_RATE_CONFIG.nationalPensionEmployeeRate;
      const healthInsurance = taxableMonthly * ANNUAL_SALARY_RATE_CONFIG.healthInsuranceEmployeeRate;
      const longTermCareInsurance = healthInsurance * ANNUAL_SALARY_RATE_CONFIG.longTermCareInsuranceRate;
      const employmentInsurance = taxableMonthly * ANNUAL_SALARY_RATE_CONFIG.employmentInsuranceEmployeeRate;

      const annualTaxable = taxableMonthly * 12;
      const deduction = dependents * 1_500_000 + childrenUnder20 * 1_000_000;
      const taxableBase = Math.max(0, annualTaxable - deduction);
      const bracket = SIMPLE_INCOME_TAX_BRACKETS.find((b) => taxableBase <= b.limit) ?? SIMPLE_INCOME_TAX_BRACKETS.at(-1);
      const incomeTax = Math.max(0, taxableBase * bracket.rate - bracket.quickDeduction) / 12;
      const localIncomeTax = incomeTax * ANNUAL_SALARY_RATE_CONFIG.localIncomeTaxRate;
      const totalDeduction = nationalPension + healthInsurance + longTermCareInsurance + employmentInsurance + incomeTax + localIncomeTax;
      const netMonthly = Math.max(0, grossMonthly - totalDeduction);

      const res = {
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

      Object.entries(res).forEach(([k, v]) => {
        if (resultTargets[k]) resultTargets[k].textContent = formatWon(v);
      });
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     7. 월급 실수령액 계산기 (salary-calculator) - 2026 기준
  ==================================================== */
  const salaryForm = document.querySelector('form[action*="salary-calculator"]:not([action*="annual-salary"]), [data-salary-calculator]');
  if (salaryForm) {
    const layout = salaryForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    salaryForm.addEventListener('submit', (e) => {
      e.preventDefault();
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
        const pensionBase = Math.min(Math.max(taxableMonthly, 400_000), 6_370_000);
        nationalPension = Math.round(pensionBase * 0.0475);
        healthInsurance = Math.round(taxableMonthly * 0.03595);
        longTermCareInsurance = Math.round(healthInsurance * (0.009448 / 0.0719));
        employmentInsurance = Math.round(taxableMonthly * 0.009);
      }

      const annualTaxableGross = taxableMonthly * 12.0;
      let annualIncomeTax = 0;
      if (annualTaxableGross > 0) {
        let eDed = 0;
        if (annualTaxableGross <= 5_000_000) eDed = annualTaxableGross * 0.7;
        else if (annualTaxableGross <= 15_000_000) eDed = 3_500_000 + (annualTaxableGross - 5_000_000) * 0.4;
        else if (annualTaxableGross <= 45_000_000) eDed = 7_500_000 + (annualTaxableGross - 15_000_000) * 0.15;
        else if (annualTaxableGross <= 100_000_000) eDed = 12_000_000 + (annualTaxableGross - 45_000_000) * 0.05;
        else eDed = 14_750_000 + (annualTaxableGross - 100_000_000) * 0.02;

        let sDed = 3_700_000;
        if (dependents === 1) {
          if (annualTaxableGross <= 30_000_000) sDed = 3_100_000 + annualTaxableGross * 0.04;
          else if (annualTaxableGross <= 45_000_000) sDed = 3_100_000 + annualTaxableGross * 0.04 - (annualTaxableGross - 30_000_000) * 0.05;
          else if (annualTaxableGross <= 70_000_000) sDed = 3_100_000 + annualTaxableGross * 0.015;
          else if (annualTaxableGross <= 120_000_000) sDed = 3_100_000 + annualTaxableGross * 0.005;
        } else if (dependents === 2) {
          if (annualTaxableGross <= 30_000_000) sDed = 3_600_000 + annualTaxableGross * 0.04;
          else if (annualTaxableGross <= 45_000_000) sDed = 3_600_000 + annualTaxableGross * 0.04 - (annualTaxableGross - 30_000_000) * 0.05;
          else if (annualTaxableGross <= 70_000_000) sDed = 3_600_000 + annualTaxableGross * 0.02;
          else if (annualTaxableGross <= 120_000_000) sDed = 3_600_000 + annualTaxableGross * 0.01;
        } else {
          if (annualTaxableGross <= 30_000_000) sDed = 5_000_000 + annualTaxableGross * 0.07;
          else if (annualTaxableGross <= 45_000_000) sDed = 5_000_000 + annualTaxableGross * 0.07 - (annualTaxableGross - 30_000_000) * 0.05;
          else if (annualTaxableGross <= 70_000_000) sDed = 5_000_000 + annualTaxableGross * 0.05;
          else if (annualTaxableGross <= 120_000_000) sDed = 5_000_000 + annualTaxableGross * 0.03;
        }

        const taxBase = Math.max(0, annualTaxableGross - eDed - dependents * 1_500_000 - sDed - nationalPension * 12);
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
      if (eligibleChildren === 1) childCredit = 12_500;
      else if (eligibleChildren >= 2) childCredit = 29_160 + (eligibleChildren - 2) * 25_000;

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

  /* ====================================================
     8. 퇴직금 계산기 (severance-pay-calculator)
  ==================================================== */
  const severanceForm = document.querySelector('form[action*="severance-pay-calculator"], [data-severance-calculator]');
  if (severanceForm) {
    const layout = severanceForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    severanceForm.addEventListener('submit', (e) => {
      e.preventDefault();
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

  /* ====================================================
     9. 연차수당 계산기 (annual-leave-pay-calculator)
  ==================================================== */
  const annualLeaveForm = document.querySelector('form[action*="annual-leave-pay-calculator"], [data-annual-leave-calculator]');
  if (annualLeaveForm) {
    const layout = annualLeaveForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    annualLeaveForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const startDate = document.getElementById('startDate')?.value;
      const calculationDate = document.getElementById('calculationDate')?.value;
      const dailyOrdinaryWage = parseNum('dailyOrdinaryWage');
      const usedLeaveDays = parseNum('usedLeaveDays');

      if (!startDate || !calculationDate) {
        alert('입사일과 산정 기준일을 입력해주세요.');
        return;
      }

      const start = new Date(startDate);
      const calc = new Date(calculationDate);
      let months = (calc.getFullYear() - start.getFullYear()) * 12 + (calc.getMonth() - start.getMonth());
      if (calc.getDate() < start.getDate()) months -= 1;
      months = Math.max(0, months);

      let generated = 0;
      if (months < 12) {
        generated = Math.min(11, months);
      } else {
        const years = Math.floor(months / 12);
        const additional = Math.max(0, Math.floor((years - 1) / 2));
        generated = Math.min(25, 15 + additional);
      }

      const remaining = Math.max(0, generated - usedLeaveDays);
      const allowance = remaining * dailyOrdinaryWage;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 5) {
        targets[0].textContent = `${months}개월`;
        targets[1].textContent = `${generated}일`;
        targets[2].textContent = `${usedLeaveDays}일`;
        targets[3].textContent = `${remaining}일`;
        targets[4].textContent = formatWon(allowance);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     10. 적정주가 계산기 (fair-value-calculator)
  ==================================================== */
  const fairValueForm = document.querySelector('form[action*="fair-value-calculator"], [data-fair-value-calculator]');
  if (fairValueForm) {
    const layout = fairValueForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    fairValueForm.addEventListener('submit', (e) => {
      e.preventDefault();
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

  /* ====================================================
     11. 환율 계산기 (exchange-calculator)
  ==================================================== */
  const exchangeForm = document.querySelector('form[action*="exchange-calculator"], [data-exchange-calculator]');
  if (exchangeForm) {
    const layout = exchangeForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    exchangeForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const amount = parseNum('amount');
      const exchangeRate = parseNum('exchangeRate');
      const feeRate = parseNum('feeRate');

      const beforeFee = amount * exchangeRate;
      const fee = beforeFee * feeRate / 100;
      const afterFee = Math.max(0, beforeFee - fee);

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 3) {
        targets[0].textContent = formatWon(beforeFee);
        targets[1].textContent = formatWon(fee);
        targets[2].textContent = formatWon(afterFee);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     12. 전기요금 계산기 (electricity-bill-calculator)
  ==================================================== */
  const electricityForm = document.querySelector('form[action*="electricity-bill-calculator"], [data-electricity-calculator]');
  if (electricityForm) {
    const layout = electricityForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    electricityForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const usage = parseNum('usageKwh');
      const season = document.getElementById('season')?.value || 'OTHER';

      let tier1 = 200, tier2 = 400;
      if (season === 'SUMMER') {
        tier1 = 300;
        tier2 = 450;
      }

      const u1 = Math.min(usage, tier1);
      const u2 = Math.min(Math.max(usage - tier1, 0), tier2 - tier1);
      const u3 = Math.max(usage - tier2, 0);

      const energyCharge = u1 * 120.0 + u2 * 214.6 + u3 * 307.3;
      const baseFee = usage <= tier1 ? 910 : usage <= tier2 ? 1600 : 7300;
      const climateCharge = usage * 9.0;
      const fuelAdjustment = usage * 5.0;
      const subtotal = baseFee + energyCharge + climateCharge + fuelAdjustment;
      const vat = Math.round(subtotal * 0.1);
      const fund = Math.floor(subtotal * 0.037 / 10) * 10;
      const total = subtotal + vat + fund;
      const avgPrice = usage > 0 ? total / usage : 0;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 10) {
        targets[0].textContent = `${formatComma(usage)} kWh`;
        targets[1].textContent = formatWon(baseFee);
        targets[2].textContent = formatWon(energyCharge);
        targets[3].textContent = formatWon(climateCharge);
        targets[4].textContent = formatWon(fuelAdjustment);
        targets[5].textContent = formatWon(subtotal);
        targets[6].textContent = formatWon(vat);
        targets[7].textContent = formatWon(fund);
        targets[8].textContent = formatWon(total);
        targets[9].textContent = `${formatComma(avgPrice, 1)}원/kWh`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     13. 에어컨 전기세 계산기 (air-conditioner-electricity-calculator)
  ==================================================== */
  const airconForm = document.querySelector('form[action*="air-conditioner-electricity-calculator"], [data-aircon-calculator]');
  if (airconForm) {
    const layout = airconForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    airconForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const pWatts = parseNum('powerWatts');
      const sWatts = parseNum('standbyWatts');
      const hPerDay = parseNum('hoursPerDay');
      const dPerMonth = parseNum('daysPerMonth');
      const rate = parseNum('electricityRatePerKwh');

      const activeKwh = (pWatts / 1000) * hPerDay * dPerMonth;
      const standbyKwh = (sWatts / 1000) * 24 * dPerMonth;
      const totalKwh = activeKwh + standbyKwh;
      const estimatedCost = totalKwh * rate;
      const dailyCost = dPerMonth > 0 ? estimatedCost / dPerMonth : 0;
      const totalHours = hPerDay * dPerMonth;
      const hourlyCost = totalHours > 0 ? estimatedCost / totalHours : 0;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 6) {
        targets[0].textContent = `${formatComma(activeKwh, 1)} kWh`;
        targets[1].textContent = `${formatComma(standbyKwh, 1)} kWh`;
        targets[2].textContent = `${formatComma(totalKwh, 1)} kWh`;
        targets[3].textContent = formatWon(estimatedCost);
        targets[4].textContent = formatWon(dailyCost);
        targets[5].textContent = formatWon(hourlyCost);
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     14. 자동차 유지비 계산기 (car-maintenance-calculator)
  ==================================================== */
  const carForm = document.querySelector('form[action*="car-maintenance-calculator"], [data-car-calculator]');
  if (carForm) {
    const layout = carForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    carForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const dist = parseNum('monthlyDistanceKm');
      const eff = parseNum('fuelEfficiencyKmPerLiter') || 1;
      const fuelP = parseNum('fuelPricePerLiter');
      const parking = parseNum('parkingFeeMonthly');
      const ins = parseNum('insuranceAnnual');
      const tax = parseNum('taxAnnual');
      const installment = parseNum('installmentMonthly');
      const maint = parseNum('maintenanceAnnual');
      const toll = parseNum('tollMonthly');

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

  /* ====================================================
     15. 월 생활비 계산기 (monthly-budget-calculator)
  ==================================================== */
  const budgetForm = document.querySelector('form[action*="monthly-budget-calculator"], [data-budget-calculator]');
  if (budgetForm) {
    const layout = budgetForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    budgetForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const income = parseNum('monthlyIncome');
      const fixedExp = parseNum('housing') + parseNum('communication') + parseNum('insurance') + parseNum('education') + parseNum('subscriptions');
      const varExp = parseNum('food') + parseNum('transport') + parseNum('leisure') + parseNum('other');
      const totalExp = fixedExp + varExp;
      const remaining = income - totalExp;
      const savings = parseNum('savingsGoal');
      const remAfterSavings = remaining - savings;
      const expRatio = income > 0 ? (totalExp / income) * 100 : 0;
      const savingsRatio = income > 0 ? (savings / income) * 100 : 0;

      const targets = resultPanel?.querySelectorAll('.result-grid strong');
      if (targets && targets.length >= 7) {
        targets[0].textContent = formatWon(fixedExp);
        targets[1].textContent = formatWon(varExp);
        targets[2].textContent = formatWon(totalExp);
        targets[3].textContent = formatWon(remaining);
        targets[4].textContent = formatWon(remAfterSavings);
        targets[5].textContent = `${formatComma(expRatio, 1)}%`;
        targets[6].textContent = `${formatComma(savingsRatio, 1)}%`;
      }
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     16. 국내 주식 세금 계산기 (domestic-stock-tax-calculator)
  ==================================================== */
  const domesticStockTaxForm = document.querySelector('[data-domestic-stock-tax-calculator], form[action*="domestic-stock-tax-calculator"]');
  if (domesticStockTaxForm) {
    const layout = domesticStockTaxForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');
    const applyCapitalGainsTaxInput = document.getElementById('domesticApplyCapitalGainsTax');
    const capitalGainsTaxRateInput = document.getElementById('domesticCapitalGainsTaxRate');
    const resultTargets = Object.fromEntries(
      Array.from(document.querySelectorAll('[data-domestic-stock-tax-result]')).map((el) => [el.dataset.domesticStockTaxResult, el])
    );

    const syncTaxRate = () => {
      if (!capitalGainsTaxRateInput) return;
      if (applyCapitalGainsTaxInput?.checked) {
        if (!capitalGainsTaxRateInput.value.trim()) capitalGainsTaxRateInput.value = '22';
      } else {
        capitalGainsTaxRateInput.value = '';
      }
    };
    syncTaxRate();
    applyCapitalGainsTaxInput?.addEventListener('change', syncTaxRate);

    domesticStockTaxForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const buyAmount = parseNum('domesticBuyAmount');
      const sellAmount = parseNum('domesticSellAmount');
      const feeAmount = parseNum('domesticFeeAmount');
      const transTaxRate = parseNum('domesticTransactionTaxRate') / 100;
      const capTaxRate = parseNum('domesticCapitalGainsTaxRate') / 100;
      const applyCapTax = applyCapitalGainsTaxInput?.checked ?? false;

      const capitalGain = Math.max(0, sellAmount - buyAmount - feeAmount);
      const transactionTax = sellAmount * transTaxRate;
      const capitalGainsTax = applyCapTax ? capitalGain * capTaxRate : 0;
      const totalTax = transactionTax + capitalGainsTax;
      const afterTaxProfit = Math.max(0, sellAmount - buyAmount - feeAmount - totalTax);

      const res = { capitalGain, transactionTax, capitalGainsTax, totalTax, afterTaxProfit };
      Object.entries(res).forEach(([k, v]) => {
        if (resultTargets[k]) resultTargets[k].textContent = formatWon(v);
      });
      showResult(layout, resultPanel, emptyState);
    });
  }

  /* ====================================================
     17. 해외주식 세금 계산기 (overseas-stock-tax-calculator)
  ==================================================== */
  const overseasForm = document.querySelector('form[action*="overseas-stock-tax-calculator"], [data-overseas-stock-tax-calculator]');
  if (overseasForm) {
    const layout = overseasForm.closest('.calculator-layout');
    const resultPanel = layout?.querySelector('.result-panel');
    const emptyState = layout?.querySelector('.empty-result-state');

    overseasForm.addEventListener('submit', (e) => {
      e.preventDefault();
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

// PWA Service Worker 등록
if ('serviceWorker' in navigator && window.isSecureContext) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/service-worker.js').catch(() => {
      // Core 계산 기능은 서비스 워커 없이도 정상 작동
    });
  });
}
