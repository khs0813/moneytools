# SEO 보정 노트

이번 패치에서 반영한 항목:

- `https://www.example.com` placeholder에 의존하지 않도록 canonical, Open Graph URL, sitemap, RSS 생성 시 현재 요청 도메인을 우선 사용
- `APP_BASE_URL` 환경변수 지원 추가
- 프록시 환경(Nginx)에서 `X-Forwarded-*` 헤더를 신뢰하도록 `server.forward-headers-strategy=framework` 적용
- sitemap `lastmod`를 매 요청 시각이 아닌 페이지별 실제 수정일로 고정
- JSON-LD `dateModified`를 매 요청 시각이 아닌 페이지별 실제 수정일로 고정
- RSS에 `language`, `guid`, `pubDate`, `lastBuildDate` 추가
- XML 이스케이프 적용으로 sitemap/RSS 안정성 보강

배포 후 체크할 것:

1. 운영 도메인으로 접속했을 때 `<link rel="canonical">`이 실제 도메인으로 출력되는지 확인
2. `/sitemap.xml`, `/robots.txt`, `/rss.xml`의 절대 URL이 실제 도메인으로 출력되는지 확인
3. Search Console과 Naver Search Advisor에서 사이트 소유 확인 후 sitemap 제출
4. Rich Results Test로 FAQ 및 Breadcrumb 구조화 데이터 검증
