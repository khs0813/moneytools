# SEO 점검 메모 (2026-04-25)

## 적용된 항목
- 페이지별 고유 title / meta description
- canonical URL
- robots 메타 / googlebot 메타
- robots.txt
- sitemap.xml
- rss.xml
- JSON-LD 구조화 데이터 (Organization, WebSite, WebPage, BreadcrumbList, FAQPage)
- Open Graph / Twitter 카드 메타
- Google / Naver 사이트 검증 메타 태그 연결
- 내부 링크 / breadcrumb
- 정적 자산 버전 파라미터 적용

## 운영 전 필수 설정
- `APP_BASE_URL=https://실제도메인`
- `GOOGLE_SITE_VERIFICATION=...`
- `NAVER_SITE_VERIFICATION=...`

## 제출 권장 순서
1. Google Search Console 사이트 등록 및 소유 확인
2. sitemap.xml 제출
3. Naver Search Advisor 사이트 등록 및 소유 확인
4. sitemap.xml / rss.xml 제출
5. Google Rich Results Test, URL Inspection, Naver 사이트 간단 체크 실행
