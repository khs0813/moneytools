import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const DIST_DIR = path.resolve(__dirname, '../dist');

console.log('[SEO Check] Starting SEO and build validation...');

if (!fs.existsSync(DIST_DIR)) {
  console.error('[SEO Check] FAILED: dist/ directory does not exist. Run "npm run build" first.');
  process.exit(1);
}

const pagesDataRaw = fs.readFileSync(path.join(__dirname, 'page-data.json'), 'utf8');
const pages = JSON.parse(pagesDataRaw);

let errors = 0;
let warnings = 0;

// 1. 필수 정적 파일 존재 확인
const requiredFiles = [
  'index.html',
  '404.html',
  'sitemap.xml',
  'robots.txt',
  'rss.xml',
  'favicon.svg',
  'og-image.png',
  'site.webmanifest',
  'service-worker.js',
  'css/styles.css',
  'js/calculator.js',
  '.well-known/assetlinks.json'
];

for (const file of requiredFiles) {
  const fullPath = path.join(DIST_DIR, file);
  if (!fs.existsSync(fullPath)) {
    console.error(`[SEO Check] ERROR: Missing required file: ${file}`);
    errors += 1;
  }
}

// 2. 43개 모든 페이지의 HTML 및 SEO 메타태그 검증
for (const page of pages) {
  const filePath = page.path === '/'
    ? path.join(DIST_DIR, 'index.html')
    : path.join(DIST_DIR, `${page.path.replace(/^\//, '')}.html`);

  const cleanDirIndex = page.path === '/'
    ? path.join(DIST_DIR, 'index.html')
    : path.join(DIST_DIR, page.path.replace(/^\//, ''), 'index.html');

  if (!fs.existsSync(filePath)) {
    console.error(`[SEO Check] ERROR: HTML file missing for page ${page.key} at ${filePath}`);
    errors += 1;
    continue;
  }

  if (!fs.existsSync(cleanDirIndex)) {
    console.error(`[SEO Check] ERROR: Clean URL directory index missing for page ${page.key} at ${cleanDirIndex}`);
    errors += 1;
    continue;
  }

  const content = fs.readFileSync(filePath, 'utf8');

  // Title 검증
  if (!content.includes(`<title>${page.title}</title>`)) {
    console.error(`[SEO Check] ERROR: Title mismatch in ${page.key}`);
    errors += 1;
  }

  // Canonical 검증
  if (!content.includes('rel="canonical"')) {
    console.error(`[SEO Check] ERROR: Missing canonical link in ${page.key}`);
    errors += 1;
  }

  // Description 검증
  if (!content.includes('name="description"')) {
    console.error(`[SEO Check] ERROR: Missing meta description in ${page.key}`);
    errors += 1;
  }

  // Open Graph 검증
  if (!content.includes('property="og:title"') || !content.includes('property="og:description"')) {
    console.error(`[SEO Check] ERROR: Missing Open Graph tags in ${page.key}`);
    errors += 1;
  }

  // Naver site verification
  if (!content.includes('name="naver-site-verification"')) {
    console.warn(`[SEO Check] WARNING: Missing naver verification in ${page.key}`);
    warnings += 1;
  }

  // JSON-LD 검증 (FAQ가 있는 페이지는 FAQPage schema 검증)
  if (page.faqs && page.faqs.length > 0) {
    if (!content.includes('"@type":"FAQPage"') && !content.includes('"@type": "FAQPage"')) {
      console.error(`[SEO Check] ERROR: Missing FAQ schema in ${page.key}`);
      errors += 1;
    }
  }
}

// 3. Sitemap 정합성 검증
const sitemapContent = fs.readFileSync(path.join(DIST_DIR, 'sitemap.xml'), 'utf8');
const sitemapPages = pages.filter((p) => p.inSitemap);

for (const p of sitemapPages) {
  const expectedUrl = `https://www.moneycalculator.co.kr${p.path === '/' ? '' : p.path}`;
  if (!sitemapContent.includes(`<loc>${expectedUrl}</loc>`)) {
    console.error(`[SEO Check] ERROR: URL missing from sitemap.xml: ${expectedUrl}`);
    errors += 1;
  }
}

console.log(`[SEO Check] Completed check for ${pages.length} pages.`);
console.log(`[SEO Check] Result: ${errors} errors, ${warnings} warnings.`);

if (errors > 0) {
  console.error('[SEO Check] FAILED with errors.');
  process.exit(1);
} else {
  console.log('[SEO Check] PASSED! All SEO and build requirements satisfied.');
}
