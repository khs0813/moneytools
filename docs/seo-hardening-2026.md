# SEO 보정 적용 내역 (2026-04-25)

## 반영한 항목

1. `example.com` canonical 문제 보정
   - `app.base-url`가 비어 있거나 placeholder/localhost인 경우, 현재 요청 호스트를 기준으로 절대 URL 생성
   - canonical, `og:url`, sitemap, RSS 링크에 동일 로직 적용

2. Google / Naver 사이트 소유 확인 메타 태그 환경변수화
   - `GOOGLE_SITE_VERIFICATION`
   - `NAVER_SITE_VERIFICATION`

3. sitemap `lastmod`를 페이지별 실제 수정일로 변경
   - `SitePages`에서 페이지별 `lastModified` 관리
   - 더 이상 요청 시점의 현재 날짜를 사용하지 않음

4. JSON-LD `dateModified`를 페이지별 실제 수정일로 변경
   - KST 기준 ISO-8601 출력

5. 추가 보정
   - `server.forward-headers-strategy: framework` 적용
   - RSS에 `language`, `lastBuildDate`, `guid`, `pubDate` 추가
   - XML escape 처리 추가

## 운영 전 체크

- `APP_BASE_URL` 실제 운영 도메인 설정
- Search Console / Naver Search Advisor 검증값 입력
- Search Console에서 sitemap 제출
- Naver Search Advisor에서 sitemap / RSS 제출
- Rich Results Test에서 FAQ, Breadcrumb 확인
