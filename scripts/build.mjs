import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const ROOT_DIR = path.resolve(__dirname, '..');

const BASE_URL = (process.env.APP_BASE_URL || 'https://www.moneycalculator.co.kr').replace(/\/$/, '');
const APP_NAME = process.env.APP_NAME || '머니계산기';
const APP_DESC = process.env.APP_DESCRIPTION || '실수령액, 대출이자, 퇴직금, 배당금, 환율, 전기요금, 자동차 유지비, 생활비 계산기와 금융·세금 가이드를 한 곳에서 확인하세요.';
const STATIC_VERSION = '20260801';

const pagesDataRaw = fs.readFileSync(path.join(__dirname, 'page-data.json'), 'utf8');
const ALL_PAGES = JSON.parse(pagesDataRaw);

const NAVIGATION_KEYS = [
  'loan',
  'stock-average',
  'loan-refinance',
  'stock-tax',
  'domestic-stock-tax',
  'overseas-tax',
  'mortgage',
  'dividend',
  'annual-salary-net',
  'salary',
  'severance',
  'annual-leave',
  'fair-value',
  'exchange',
  'electricity-bill',
  'air-conditioner-cost',
  'car-maintenance',
  'monthly-budget',
  'guide',
  'privacy',
  'contact'
];

const NAVIGATION_PAGES = NAVIGATION_KEYS.map((k) => ALL_PAGES.find((p) => p.key === k)).filter(Boolean);
const GUIDE_PAGES = ALL_PAGES.filter((p) => p.key.startsWith('guide-'));
const CALCULATOR_PAGES = NAVIGATION_PAGES.filter((p) => !['home', 'guide', 'privacy', 'contact'].includes(p.key));

// 템플릿 매핑
const TEMPLATE_MAP = {
  'home': 'index.html',
  'guide': 'guide.html',
  'dividend': 'dividend-calculator.html',
  'fair-value': 'fair-value-calculator.html',
  'loan': 'loan-interest-calculator.html',
  'stock-average': 'stock-average-calculator.html',
  'loan-refinance': 'loan-refinance-calculator.html',
  'mortgage': 'mortgage-monthly-payment-calculator.html',
  'annual-salary-net': 'annual-salary-net-calculator.html',
  'salary': 'salary-calculator.html',
  'severance': 'severance-pay-calculator.html',
  'annual-leave': 'annual-leave-pay-calculator.html',
  'exchange': 'exchange-calculator.html',
  'electricity-bill': 'electricity-bill-calculator.html',
  'air-conditioner-cost': 'air-conditioner-electricity-calculator.html',
  'car-maintenance': 'car-maintenance-calculator.html',
  'monthly-budget': 'monthly-budget-calculator.html',
  'stock-tax': 'stock-tax-calculator.html',
  'domestic-stock-tax': 'domestic-stock-tax-calculator.html',
  'overseas-tax': 'overseas-stock-tax-calculator.html',
  'privacy': 'privacy-policy.html',
  'about': 'about.html',
  'terms': 'terms.html',
  'disclaimer': 'disclaimer.html',
  'contact': 'contact.html',

  'guide-stock-tax': 'guide-stock-tax.html',
  'guide-overseas-stock-tax': 'guide-overseas-stock-tax.html',
  'guide-dividend-tax': 'guide-dividend-tax.html',
  'guide-salary-3000': 'guide-salary-3000-net.html',
  'guide-salary-5000': 'guide-salary-5000-net.html',
  'guide-salary-100m': 'guide-salary-100m-net.html',
  'guide-monthly-salary-300': 'guide-monthly-salary-300-net.html',
  'guide-loan-100m-interest': 'guide-loan-100m-interest.html',
  'guide-repayment-difference': 'guide-repayment-method-difference.html',
  'guide-ltv-dsr': 'guide-ltv-dsr-difference.html',
  'guide-severance-average-wage': 'guide-severance-average-wage.html',
  'guide-overseas-deduction': 'guide-overseas-stock-tax-deduction.html',
  'guide-electricity-tier': 'guide-electricity-tier.html',
  'guide-aircon-8hours': 'guide-aircon-8hours-cost.html',
  'guide-car-cost': 'guide-car-monthly-cost.html',
  'guide-dividend-100m': 'guide-dividend-100man.html',
  'guide-budget-items': 'guide-monthly-budget-items.html',
  'guide-per-limit': 'guide-per-fair-value-limit.html'
};

// eyebrow 텍스트 결정
function getEyebrow(key) {
  if (['salary', 'annual-salary-net'].includes(key)) return '급여·실수령액 계산';
  if (['severance', 'annual-leave'].includes(key)) return '노무·퇴직·연차 계산';
  if (['loan', 'loan-refinance', 'mortgage'].includes(key)) return '대출·주거 계산';
  if (['stock-tax', 'domestic-stock-tax', 'overseas-tax'].includes(key)) return '세금·신고 참고 계산';
  if (['dividend', 'fair-value', 'stock-average'].includes(key)) return '투자 판단 보조 계산';
  if (['exchange'].includes(key)) return '환율·해외거래 계산';
  if (['electricity-bill', 'air-conditioner-cost'].includes(key)) return '전기요금·공과금 계산';
  if (['car-maintenance', 'monthly-budget'].includes(key)) return '생활비·가계 예산 계산';
  if (key === 'guide' || key.startsWith('guide-')) return '설명형 금융 가이드';
  if (key === 'about') return '사이트 운영 안내';
  if (key === 'contact') return '문의·오류 제보 안내';
  if (key === 'privacy') return '개인정보 처리 안내';
  if (key === 'terms') return '서비스 이용 정책';
  if (key === 'disclaimer') return '면책·책임 범위 안내';
  return '이용 안내';
}

// 롱폼 콘텐츠 파싱
const longformFile = fs.readFileSync(path.join(ROOT_DIR, 'src/main/resources/templates/fragments/longform.html'), 'utf8');
function extractLongform(key) {
  // th:if="${currentKey == 'annual-salary-net' or currentKey == 'salary'}" 등 매칭
  const regex = new RegExp(`<th:block th:if="\\$\\{([^}]+)\\}\\">([\\s\\S]*?)<\\/th:block>`, 'g');
  let match;
  while ((match = regex.exec(longformFile)) !== null) {
    const cond = match[1];
    const content = match[2];
    if (cond.includes(`'${key}'`)) {
      return content.trim();
    }
  }
  return '';
}

// JSON-LD 생성
function generateStructuredData(page) {
  const canonicalUrl = `${BASE_URL}${page.path === '/' ? '' : page.path}`;
  const graph = [];

  // WebSite schema for home
  if (page.path === '/') {
    graph.push({
      '@context': 'https://schema.org',
      '@type': 'WebSite',
      'name': APP_NAME,
      'url': BASE_URL,
      'description': APP_DESC
    });
  }

  // FAQ schema if page has FAQs
  if (page.faqs && page.faqs.length > 0) {
    graph.push({
      '@context': 'https://schema.org',
      '@type': 'FAQPage',
      'mainEntity': page.faqs.map((faq) => ({
        '@type': 'Question',
        'name': faq.question,
        'acceptedAnswer': {
          '@type': 'Answer',
          'text': faq.answer
        }
      }))
    });
  }

  if (graph.length === 0) return '';
  return JSON.stringify({ '@context': 'https://schema.org', '@graph': graph });
}

// HTML Head 컴파일
function renderHead(page) {
  const canonicalUrl = `${BASE_URL}${page.path === '/' ? '' : page.path}`;
  const defaultImageUrl = `${BASE_URL}/og-image.png`;
  const structuredData = generateStructuredData(page);

  return `<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1">
    <meta name="googlebot" content="index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1">
    <meta name="theme-color" content="#6c8cff">
    <meta name="format-detection" content="telephone=no, address=no, email=no">
    <meta name="application-name" content="${APP_NAME}">
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-title" content="${APP_NAME}">
    <meta name="apple-mobile-web-app-status-bar-style" content="default">
    <title>${page.title}</title>
    <meta name="description" lang="ko" content="${page.description}">
    <link rel="canonical" href="${canonicalUrl}">
    <link rel="alternate" hreflang="ko-KR" href="${canonicalUrl}">
    <link rel="alternate" hreflang="ko" href="${canonicalUrl}">
    <link rel="alternate" hreflang="x-default" href="${canonicalUrl}">
    <link rel="alternate" type="application/rss+xml" title="${APP_NAME} RSS" href="${BASE_URL}/rss.xml">

    <meta property="og:locale" content="ko_KR">
    <meta property="og:type" content="website">
    <meta property="og:site_name" content="${APP_NAME}">
    <meta property="og:title" content="${page.title}">
    <meta property="og:description" content="${page.description}">
    <meta property="og:url" content="${canonicalUrl}">
    <meta property="og:image" content="${defaultImageUrl}">
    <meta property="og:image:type" content="image/png">
    <meta property="og:image:alt" content="${APP_NAME} 금융 계산기 모음">
    <meta property="og:image:width" content="1200">
    <meta property="og:image:height" content="630">
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="${page.title}">
    <meta name="twitter:description" content="${page.description}">
    <meta name="twitter:image" content="${defaultImageUrl}">
    <meta name="twitter:image:alt" content="${APP_NAME} 금융 계산기 모음">

    <meta name="naver-site-verification" content="1ee183886e284f895a1709cd041bda149e20e662">

    <link rel="icon" href="/favicon.svg" type="image/svg+xml">
    <link rel="icon" href="/icons/icon-192.png" sizes="192x192" type="image/png">
    <link rel="apple-touch-icon" href="/icons/apple-touch-icon.png">
    <link rel="manifest" href="/site.webmanifest">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.css">
    <link rel="stylesheet" href="/css/styles.css?v=${STATIC_VERSION}">

    <script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13" defer></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/l10n/ko.js" defer></script>
    ${structuredData ? `<script type="application/ld+json">${structuredData}</script>` : ''}
</head>`;
}

// Nav 컴파일
function renderNav(activeKey) {
  const links = NAVIGATION_PAGES.map((menu) => {
    const isActive = activeKey === menu.key;
    const isUtility = menu.key === 'guide';
    const classes = [
      isActive ? 'active' : '',
      isUtility ? 'utility-start' : ''
    ].filter(Boolean).join(' ');
    const classAttr = classes ? ` class="${classes}"` : '';
    return `<a href="${menu.path}"${classAttr}>${menu.label}</a>`;
  }).join('\n        ');

  return `<aside class="sidebar" id="sidebar">
    <a class="brand" href="/" aria-label="${APP_NAME} 홈으로 이동">
        <span class="brand-mark">₩</span>
        <span>
            <strong>${APP_NAME}</strong>
            <small>생활비·금융·세금 계산 가이드</small>
        </span>
    </a>

    <nav class="side-nav" aria-label="계산기 메뉴">
        ${links}
    </nav>

    <div class="sidebar-note">
        <strong>계산 결과는 참고용</strong>
        <p>세금·급여·투자 판단 전에는 최신 기준과 전문가 확인이 필요합니다.</p>
    </div>
</aside>`;
}

// Footer 컴파일
function renderFooter() {
  return `<footer class="footer">
    <div>
        <strong>${APP_NAME}</strong>
        <p>본 사이트의 계산 결과는 참고용이며 법률, 세무, 투자, 노무 자문을 대체하지 않습니다.</p>
    </div>
    <div class="footer-links">
        <a href="/about">사이트 소개</a>
        <a href="/guide">이용안내</a>
        <a href="/privacy-policy">개인정보처리방침</a>
        <a href="/terms">이용약관</a>
        <a href="/disclaimer">면책고지</a>
        <a href="/contact">문의하기</a>
        <a href="/sitemap.xml">사이트맵</a>
    </div>
    <p class="copyright">© 2026 ${APP_NAME}. All rights reserved.</p>
    <script src="/js/calculator.js?v=${STATIC_VERSION}" defer></script>
</footer>`;
}

// Breadcrumbs 컴파일
function renderBreadcrumbs(page) {
  return `<div class="breadcrumbs" aria-label="breadcrumb">
    <a href="/">홈</a>
    <span aria-hidden="true">/</span>
    <strong>${page.label}</strong>
</div>`;
}

// Page Hero 컴파일
function renderPageHero(page, customTitle) {
  const eyebrow = getEyebrow(page.key);
  const title = customTitle || page.label;
  return `<section class="page-hero">
    <p class="eyebrow">${eyebrow}</p>
    <h1>${title}</h1>
    <p>${page.description}</p>
</section>`;
}

// Calculation Basis 박스
function renderCalculationBasis(key) {
  let liContent = '';
  if (['salary', 'annual-salary-net', 'severance', 'annual-leave'].includes(key)) {
    liContent = '<li>반영 기준: 2026년 6월 현재 공개된 급여 공제 구조, 4대보험 근로자 부담률, 근로기준법상 계산 원칙을 기준으로 추정합니다.</li>';
  } else if (['stock-tax', 'domestic-stock-tax', 'overseas-tax', 'dividend'].includes(key)) {
    liContent = '<li>반영 기준: 2026년 6월 현재 공개된 세율 구조, 기본공제, 원천징수 개념과 사용자가 입력한 거래 조건을 기준으로 참고값을 계산합니다.</li>';
  } else if (['loan', 'loan-refinance', 'mortgage'].includes(key)) {
    liContent = '<li>반영 기준: 사용자가 입력한 금리, 기간, 상환방식, 비용과 2026년 6월 기준 일반적인 금융 계산 공식을 반영합니다.</li>';
  } else if (['electricity-bill', 'air-conditioner-cost'].includes(key)) {
    liContent = '<li>반영 기준: 2026년 6월 기준 가정용 전기요금 구조와 사용자가 입력한 사용량, 단가, 계절 조건을 기준으로 계산합니다.</li>';
  } else {
    liContent = '<li>반영 기준: 2026년 6월 기준 일반 계산 공식과 사용자가 입력한 환율, 단가, 비용, 가정값을 기준으로 참고값을 계산합니다.</li>';
  }

  return `<section class="notice-box calculation-basis-box">
    <strong>계산 기준일: 2026년 6월 기준</strong>
    <ul>
        ${liContent}
        <li>주의: 실제 급여·세금·요금·금융상품 결과와 다를 수 있으며, 회사 정책, 금융사 조건, 계약 종류, 신고 기준, 고시 변경에 따라 최종 금액은 달라질 수 있습니다.</li>
    </ul>
</section>`;
}

// Official Sources 박스
function renderOfficialSources(key) {
  let listItems = '';
  if (['salary', 'annual-salary-net', 'guide-salary-3000', 'guide-salary-5000', 'guide-salary-100m', 'guide-monthly-salary-300'].includes(key)) {
    listItems = `
      <li><a href="https://www.nts.go.kr" rel="noopener noreferrer" target="_blank">국세청 근로소득·원천세 안내</a></li>
      <li><a href="https://www.nps.or.kr" rel="noopener noreferrer" target="_blank">국민연금공단 공식 홈페이지</a></li>
      <li><a href="https://www.nhis.or.kr" rel="noopener noreferrer" target="_blank">국민건강보험공단 공식 홈페이지</a></li>
      <li><a href="https://www.moel.go.kr" rel="noopener noreferrer" target="_blank">고용노동부 임금·근로기준 안내</a></li>`;
  } else if (['severance', 'annual-leave', 'guide-severance-average-wage'].includes(key)) {
    listItems = `
      <li><a href="https://www.moel.go.kr" rel="noopener noreferrer" target="_blank">고용노동부 퇴직금·연차휴가 안내</a></li>
      <li><a href="https://www.nts.go.kr" rel="noopener noreferrer" target="_blank">국세청 퇴직소득 관련 안내</a></li>`;
  } else if (['loan', 'loan-refinance', 'mortgage', 'guide-loan-100m-interest', 'guide-repayment-difference', 'guide-ltv-dsr'].includes(key)) {
    listItems = `
      <li><a href="https://www.fss.or.kr" rel="noopener noreferrer" target="_blank">금융감독원 금융소비자 정보포털</a></li>
      <li><a href="https://www.hf.go.kr" rel="noopener noreferrer" target="_blank">한국주택금융공사 공식 홈페이지</a></li>
      <li><a href="https://www.bok.or.kr" rel="noopener noreferrer" target="_blank">한국은행 기준금리 및 통계</a></li>`;
  } else if (['stock-tax', 'domestic-stock-tax', 'overseas-tax', 'dividend', 'guide-stock-tax', 'guide-overseas-stock-tax', 'guide-dividend-tax', 'guide-overseas-deduction'].includes(key)) {
    listItems = `
      <li><a href="https://www.nts.go.kr" rel="noopener noreferrer" target="_blank">국세청 양도소득세·금융소득 안내</a></li>
      <li><a href="https://www.hometax.go.kr" rel="noopener noreferrer" target="_blank">국세청 홈택스 세금신고</a></li>
      <li><a href="https://www.krx.co.kr" rel="noopener noreferrer" target="_blank">한국거래소 공시 및 시장안내</a></li>`;
  } else if (['electricity-bill', 'air-conditioner-cost', 'guide-electricity-tier', 'guide-aircon-8hours'].includes(key)) {
    listItems = `
      <li><a href="https://cyber.kepco.co.kr" rel="noopener noreferrer" target="_blank">한국전력 사이버지점 전기요금표</a></li>
      <li><a href="https://home.kepco.co.kr" rel="noopener noreferrer" target="_blank">한국전력공사 공식 홈페이지</a></li>`;
  } else {
    listItems = `
      <li><a href="https://www.fss.or.kr" rel="noopener noreferrer" target="_blank">금융감독원 금융소비자 정보포털</a></li>
      <li><a href="https://www.nts.go.kr" rel="noopener noreferrer" target="_blank">국세청 공식 홈페이지</a></li>
      <li><a href="https://www.moel.go.kr" rel="noopener noreferrer" target="_blank">고용노동부 공식 홈페이지</a></li>`;
  }

  return `<section class="content-card official-sources">
    <h2>공식 참고자료</h2>
    <p>아래 링크는 계산 기준을 다시 확인할 때 참고할 수 있는 공식 기관 페이지입니다. 실제 신고, 납부, 심사, 급여 정산 전에는 반드시 최신 공지와 원문을 확인해야 합니다.</p>
    <ul class="source-list">
        ${listItems}
    </ul>
</section>`;
}

// FAQ 아코디언 컴파일
function renderFaqList(faqs) {
  if (!faqs || faqs.length === 0) return '';
  const items = faqs.map((f) => `
        <details>
            <summary>${f.question}</summary>
            <p>${f.answer}</p>
        </details>`).join('');

  return `<section class="faq-section">
    <h2>자주 묻는 질문</h2>
    <div class="faq-list">
        ${items}
    </div>
</section>`;
}

// 관련 계산기 링크 컴파일
function renderRelatedLinks(currentKey) {
  const related = NAVIGATION_PAGES
    .filter((p) => p.key !== currentKey && !['home', 'guide', 'privacy', 'contact'].includes(p.key))
    .map((p) => `<a href="${p.path}">${p.label} 바로가기</a>`)
    .join('\n            ');

  return `<section class="related-box">
    <h2>함께 사용하면 좋은 계산기</h2>
    <div class="related-links">
        ${related}
    </div>
</section>`;
}

// Side Note 컴파일
function renderCalculatorSideNote() {
  return `<section class="content-card calculator-side-note">
    <h2>계산기 이용 시 꼭 확인하세요</h2>
    <div class="note-grid">
        <div>
            <h3>참고용 결과</h3>
            <p>본 계산 결과는 사용자가 입력한 수치와 2026년 기준 기본 공식을 조합한 추정값입니다. 실제 금융기관 심사, 회사 급여명세서, 국세청 최종 세액과 차이가 발생할 수 있습니다.</p>
        </div>
        <div>
            <h3>입력값 확인</h3>
            <p>이자율 단위(연이율 %), 기간(년/개월), 비과세 금액, 부양가족 수 등 세부 조건이 정확하지 않으면 계산 오차가 커질 수 있습니다.</p>
        </div>
        <div>
            <h3>공식 자료 검토</h3>
            <p>계약 체결, 대출 실행, 세금 신고 전에는 반드시 하단의 공식 출처 링크와 약관, 공고문을 다시 확인하시기 바랍니다.</p>
        </div>
    </div>
</section>`;
}

// Empty Result State
function renderResultEmptyState(msg) {
  return `<section class="content-card empty-result-state">
    <h2>결과 안내</h2>
    <p>${msg || '값을 입력하면 결과가 표시됩니다.'}</p>
</section>`;
}

// Disclaimer Box
function renderDisclaimer(text) {
  return `<div class="notice-box">
    <strong>안내</strong>
    <p>${text || '계산 결과는 참고용입니다.'}</p>
</div>`;
}

// 메인 빌드 함수
export async function buildSite() {
  const distDir = path.join(ROOT_DIR, 'dist');
  if (fs.existsSync(distDir)) {
    fs.rmSync(distDir, { recursive: true, force: true });
  }
  fs.mkdirSync(distDir, { recursive: true });

  console.log(`[Build] Building Moneytools Static Site to ${distDir}...`);

  // 1. 정적 에셋 복사
  const staticSrc = path.join(ROOT_DIR, 'src/main/resources/static');
  if (fs.existsSync(staticSrc)) {
    fs.cpSync(staticSrc, distDir, { recursive: true });
    console.log('[Build] Copied static assets.');
  }

  // 2. 템플릿 컴파일
  const templatesDir = path.join(ROOT_DIR, 'src/main/resources/templates');

  for (const page of ALL_PAGES) {
    const templateFileName = TEMPLATE_MAP[page.key];
    if (!templateFileName) {
      console.warn(`[Build] No template mapped for page key: ${page.key}`);
      continue;
    }

    const templatePath = path.join(templatesDir, templateFileName);
    if (!fs.existsSync(templatePath)) {
      console.warn(`[Build] Template file not found: ${templatePath}`);
      continue;
    }

    let html = fs.readFileSync(templatePath, 'utf8');

    // Thymeleaf xmlns 제거
    html = html.replace(/<html\s+lang="ko"\s+xmlns:th="http:\/\/www\.thymeleaf\.org">/g, '<html lang="ko">');

    // Head 치환
    html = html.replace(/<head\s+th:replace="~\{fragments\/head\s+::\s+head\([^)]*\)\}">[\s\S]*?<\/head>/g, renderHead(page));

    // Nav 치환
    html = html.replace(/<aside\s+th:replace="~\{fragments\/nav\s+::\s+sidebar\([^)]*\)\}">[\s\S]*?<\/aside>/g, renderNav(page.key));

    // Footer 치환
    html = html.replace(/<footer\s+th:replace="~\{fragments\/footer\s+::\s+footer\}">[\s\S]*?<\/footer>/g, renderFooter());

    // Breadcrumbs 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+breadcrumbs\([^)]*\)\}">[\s\S]*?<\/div>/g, renderBreadcrumbs(page));

    // PageHero 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+pageHero\('([^']+)'[^)]*\)\}">[\s\S]*?<\/div>/g, (m, title) => renderPageHero(page, title));
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+pageHero\([^)]*\)\}">[\s\S]*?<\/div>/g, renderPageHero(page));

    // CalculationBasis 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+calculationBasis\([^)]*\)\}">[\s\S]*?<\/div>/g, renderCalculationBasis(page.key));

    // OfficialSources 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+officialSources\([^)]*\)\}">[\s\S]*?<\/div>/g, renderOfficialSources(page.key));

    // FaqList 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+faqList\([^)]*\)\}">[\s\S]*?<\/div>/g, renderFaqList(page.faqs));

    // RelatedLinks 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+relatedLinks\([^)]*\)\}">[\s\S]*?<\/div>/g, renderRelatedLinks(page.key));

    // CalculatorSideNote 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+calculatorSideNote\}">[\s\S]*?<\/div>/g, renderCalculatorSideNote());

    // ResultEmptyState 치환
    html = html.replace(/<div[^>]*th:replace="~\{fragments\/components\s+::\s+resultEmptyState\('([^']+)'\)\}">[\s\S]*?<\/div>/g, (m, msg) => renderResultEmptyState(msg));
    html = html.replace(/<div[^>]*th:replace="~\{fragments\/components\s+::\s+resultEmptyState\([^)]*\)\}">[\s\S]*?<\/div>/g, renderResultEmptyState());

    // Disclaimer 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+disclaimer\('([^']+)'\)\}">[\s\S]*?<\/div>/g, (m, text) => renderDisclaimer(text));
    html = html.replace(/<div\s+th:replace="~\{fragments\/components\s+::\s+disclaimer\([^)]*\)\}">[\s\S]*?<\/div>/g, renderDisclaimer());

    // Longform content 치환
    html = html.replace(/<div\s+th:replace="~\{fragments\/longform\s+::\s+calculatorContent\([^)]*\)\}">[\s\S]*?<\/div>/g, extractLongform(page.key));

    // index.html 및 guide.html 내 카드 목록 렌더링
    if (page.key === 'home') {
      const calcCards = CALCULATOR_PAGES.map((c) => `
        <article class="tool-card">
            <p class="tool-badge">${getEyebrow(c.key)}</p>
            <h3><a href="${c.path}">${c.label}</a></h3>
            <p>${c.description}</p>
            <a class="card-link" href="${c.path}">계산기 바로가기 →</a>
        </article>
      `).join('\n');
      html = html.replace(/<article class="tool-card"\s+th:each="item : \$\{calculatorPages\}">[\s\S]*?<\/article>/g, calcCards);

      const guideCards = GUIDE_PAGES.map((g) => `
        <article class="guide-card">
            <p class="guide-badge">설명형 가이드</p>
            <h3><a href="${g.path}">${g.label}</a></h3>
            <p>${g.description}</p>
            <a class="card-link" href="${g.path}">가이드 읽기 →</a>
        </article>
      `).join('\n');
      html = html.replace(/<article class="guide-card"\s+th:each="guide : \$\{guidePages\}">[\s\S]*?<\/article>/g, guideCards);
    }

    if (page.key === 'guide') {
      const allGuideCards = GUIDE_PAGES.map((g) => `
        <article class="guide-card">
            <p class="guide-badge">금융 가이드</p>
            <h3><a href="${g.path}">${g.label}</a></h3>
            <p>${g.description}</p>
            <a class="card-link" href="${g.path}">가이드 읽기 →</a>
        </article>
      `).join('\n');
      html = html.replace(/<article class="guide-card"\s+th:each="guide : \$\{guidePages\}">[\s\S]*?<\/article>/g, allGuideCards);
    }

    // 대출 상환표 섹션에 hidden 추가 및 tbody 비우기
    if (html.includes('id="loan-schedule"')) {
      html = html.replace(/<section class="content-card"[^>]*th:if="[^"]*result[^"]*"[^>]*>/g, '<section class="content-card" data-loan-schedule-section hidden>');
      html = html.replace(/<tbody>[\s\S]*?<\/tbody>/g, '<tbody></tbody>');
    }

    // Spring/Thymeleaf 폼 속성 정제
    html = html.replace(/th:action="@[^"]*"/g, '');
    html = html.replace(/th:object="\$\{[^"]*\}"/g, '');
    html = html.replace(/th:field="\*\{([^}]+)\}"/g, 'id="$1" name="$1"');
    html = html.replace(/th:value="\$\{[^"]*\}"/g, '');
    html = html.replace(/th:classappend="[^"]*"/g, '');
    html = html.replace(/<span\s+class="field-error"[^>]*>[\s\S]*?<\/span>/g, '');

    // 결과 패널에 hidden 속성 및 data 속성 보장
    if (html.includes('class="result-panel"') && !html.includes('data-annual-salary-result-panel') && !html.includes('data-domestic-stock-tax-result-panel')) {
      html = html.replace(/th:if="[^"]*result\s*!=\s*null[^"]*"/g, '');
      html = html.replace(/<section class="result-panel"([^>]*)>/g, (m, attrs) => {
        if (attrs.includes('hidden')) return m;
        return `<section class="result-panel"${attrs} hidden>`;
      });
    }

    // 남은 th: 문법 정리
    html = html.replace(/th:[a-zA-Z0-9_-]+="[^"]*"/g, '');

    // 파일 저장
    let outPath;
    let cleanOutPath;

    if (page.path === '/') {
      outPath = path.join(distDir, 'index.html');
      fs.writeFileSync(outPath, html, 'utf8');
    } else {
      const slug = page.path.replace(/^\//, '');
      // 1) dist/foo.html
      outPath = path.join(distDir, `${slug}.html`);
      fs.mkdirSync(path.dirname(outPath), { recursive: true });
      fs.writeFileSync(outPath, html, 'utf8');

      // 2) dist/foo/index.html (Clean URL 완벽 호환)
      cleanOutPath = path.join(distDir, slug, 'index.html');
      fs.mkdirSync(path.dirname(cleanOutPath), { recursive: true });
      fs.writeFileSync(cleanOutPath, html, 'utf8');
    }
  }

  // 3. 에러 페이지 복사 (404.html, 500.html)
  const error404Src = path.join(templatesDir, 'error/404.html');
  if (fs.existsSync(error404Src)) {
    let e404 = fs.readFileSync(error404Src, 'utf8');
    const dummyPage = { title: '페이지를 찾을 수 없습니다 | 머니계산기', description: '요청하신 페이지가 존재하지 않거나 주소가 변경되었습니다.', path: '/404', key: '404' };
    e404 = e404.replace(/<head\s+th:replace="[^"]*">[\s\S]*?<\/head>/g, renderHead(dummyPage));
    e404 = e404.replace(/<aside\s+th:replace="[^"]*">[\s\S]*?<\/aside>/g, renderNav(''));
    e404 = e404.replace(/<footer\s+th:replace="[^"]*">[\s\S]*?<\/footer>/g, renderFooter());
    fs.writeFileSync(path.join(distDir, '404.html'), e404, 'utf8');
  }

  // 4. sitemap.xml 생성
  const sitemapPages = ALL_PAGES.filter((p) => p.inSitemap);
  const sitemapXml = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
${sitemapPages.map((p) => `  <url>
    <loc>${BASE_URL}${p.path === '/' ? '' : p.path}</loc>
    <lastmod>${p.lastModified}</lastmod>
    <changefreq>${p.path === '/' ? 'daily' : 'weekly'}</changefreq>
    <priority>${p.path === '/' ? '1.0' : '0.8'}</priority>
  </url>`).join('\n')}
</urlset>`;
  fs.writeFileSync(path.join(distDir, 'sitemap.xml'), sitemapXml, 'utf8');
  console.log('[Build] Generated sitemap.xml.');

  // 5. robots.txt 생성
  const robotsTxt = `User-agent: *
Allow: /

Sitemap: ${BASE_URL}/sitemap.xml
`;
  fs.writeFileSync(path.join(distDir, 'robots.txt'), robotsTxt, 'utf8');
  console.log('[Build] Generated robots.txt.');

  // 6. rss.xml 생성
  const rssXml = `<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:atom="http://www.w3.org/2005/Atom">
<channel>
  <title>${APP_NAME}</title>
  <link>${BASE_URL}/</link>
  <description>${APP_DESC}</description>
  <atom:link href="${BASE_URL}/rss.xml" rel="self" type="application/rss+xml" />
  <language>ko-KR</language>
  <lastBuildDate>${new Date().toUTCString()}</lastBuildDate>
${sitemapPages.map((p) => `  <item>
    <title><![CDATA[${p.title}]]></title>
    <link>${BASE_URL}${p.path === '/' ? '' : p.path}</link>
    <guid isPermaLink="true">${BASE_URL}${p.path === '/' ? '' : p.path}</guid>
    <description><![CDATA[${p.description}]]></description>
    <pubDate>${new Date(p.lastModified).toUTCString()}</pubDate>
  </item>`).join('\n')}
</channel>
</rss>`;
  fs.writeFileSync(path.join(distDir, 'rss.xml'), rssXml, 'utf8');
  console.log('[Build] Generated rss.xml.');

  // 7. .well-known/assetlinks.json 생성
  const assetlinksDir = path.join(distDir, '.well-known');
  fs.mkdirSync(assetlinksDir, { recursive: true });
  const assetlinksJson = [
    {
      relation: ['delegate_permission/common.handle_all_urls'],
      target: {
        namespace: 'android_app',
        package_name: 'com.moneycalculator.app',
        sha256_cert_fingerprints: []
      }
    }
  ];
  fs.writeFileSync(path.join(assetlinksDir, 'assetlinks.json'), JSON.stringify(assetlinksJson, null, 2), 'utf8');
  console.log('[Build] Generated .well-known/assetlinks.json.');

  console.log(`[Build] Successfully compiled ${ALL_PAGES.length} pages!`);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  buildSite().catch((err) => {
    console.error('[Build Error]', err);
    process.exit(1);
  });
}
