# 머니툴스 - Spring Boot + Thymeleaf 계산기 웹사이트

배당 계산기, 적정주가 계산기, 대출이자 계산기, 실수령액 계산기, 퇴직금 계산기, 연차수당 계산기, 환율 계산기, 해외주식 세금 계산기를 포함한 광고 수익형 웹사이트 템플릿입니다.

## 포함 기능

- Spring Boot 3.5.11 + Java 17 + Thymeleaf
- 좌측 고정 nav / 모바일 햄버거 메뉴
- 밝은 파스텔 톤 카드형 UI
- 서버사이드 계산 처리
- 입력값 검증
- 결과 복사 기능
- 대출 상환표 CSV 다운로드
- Google AdSense 수동 광고 슬롯 구조
- `robots.txt`, `sitemap.xml`, `rss.xml`, `ads.txt`
- 페이지별 title, description, canonical, OG, Twitter Card
- JSON-LD 구조화 데이터: WebSite, WebPage, BreadcrumbList, FAQPage
- Google Search Console / Naver Search Advisor 인증 메타 태그 설정값
- 요청 호스트 기반 canonical/OG URL 자동 보정
- 페이지별 실제 수정일 기준 sitemap `lastmod` / 구조화데이터 `dateModified`
- 404/500 에러 페이지


## 화면/입력 UX 메모
- 숫자 입력칸은 브라우저의 지수표기(예: `1.0E8`)가 보이지 않도록 서버 렌더링과 클라이언트 포맷팅을 함께 적용했습니다.
- 기본은 세로 입력 폼이며, `해외주식 세금 계산기`와 `퇴직금 계산기`처럼 긴 폼만 계산 버튼이 첫 화면에 보이지 않을 때 2열 이상으로 전환됩니다.

## 실행 방법

```bash
mvn spring-boot:run
```

브라우저에서 다음 주소로 접속합니다.

```text
http://localhost:8080
```

패키징:

```bash
mvn clean package
java -jar target/moneytools-0.0.1-SNAPSHOT.jar
```

## 도메인/SEO 설정

운영 전에는 환경변수 또는 `application.yml`로 아래 값을 채우세요.

```yaml
app:
  name: 머니툴스
  base-url: https://moneytools.example
  contact-email: contact@moneytools.example
  google-site-verification: "구글_서치콘솔_검증값"
  naver-site-verification: "네이버_서치어드바이저_검증값"
```

환경변수 예시:

```bash
export APP_BASE_URL="https://your-real-domain.com"
export APP_CONTACT_EMAIL="contact@your-real-domain.com"
export GOOGLE_SITE_VERIFICATION="구글 검증값"
export NAVER_SITE_VERIFICATION="네이버 검증값"
```

`APP_BASE_URL`를 비워두면 개발/스테이징 환경에서는 **현재 요청 호스트**를 기준으로 canonical, OG URL, sitemap URL을 자동 생성합니다. 다만 실서비스 SEO를 위해서는 운영 도메인을 명시적으로 설정하는 것을 권장합니다.

배포 후 확인할 URL:

- `/robots.txt`
- `/sitemap.xml`
- `/rss.xml`
- `/ads.txt`

## Search Console / Naver Search Advisor 체크리스트

1. 실제 운영 도메인으로 `APP_BASE_URL` 설정
2. `GOOGLE_SITE_VERIFICATION`, `NAVER_SITE_VERIFICATION` 값 입력
3. 배포 후 `/robots.txt`, `/sitemap.xml`, `/rss.xml` 응답 확인
4. Google Search Console에 sitemap 제출
5. Naver Search Advisor에 sitemap, RSS 제출
6. Google Rich Results Test로 FAQ / Breadcrumb 구조화데이터 점검

## AdSense 설정

AdSense 승인 후 환경변수 또는 `application.yml`에서 광고를 활성화하고 client-id/slot을 교체하세요.

```yaml
app:
  adsense:
    enabled: true
    client-id: ca-pub-본인값
    slots:
      top: "상단광고슬롯"
      content: "본문광고슬롯"
      result: "결과하단광고슬롯"
      sidebar: "사이드광고슬롯"
```

`src/main/resources/static/ads.txt`도 실제 publisher ID로 교체하세요.

```text
google.com, pub-본인값, DIRECT, f08c47fec0942fa0
```

## 2026년 기준 반영 현황

이 프로젝트에는 2026년 공개 시점 기준으로 아래 항목을 반영했습니다.

- 실수령액 계산기
  - 국민연금 근로자 부담률 4.75%
  - 건강보험 근로자 부담률 3.595%
  - 장기요양보험 근로자 부담률 0.4724%
  - 고용보험(실업급여) 근로자 부담률 0.9%
  - 2026년 3월 1일부터 적용되는 근로소득 간이세액표의 가족 수 개념과 세율·공제 구조를 반영한 추정식
- 해외주식 세금 계산기
  - 양도차익 기본공제 250만원
  - 기본 양도세율 22%
  - 기본 배당세율 15.4%
  - 매수일/매도일/배당일 환율을 각각 입력받도록 개선
- 퇴직금 계산기
  - 직전 3개월의 실제 총일수 반영
  - 연간 상여금 3/12, 연차수당 3/12 가산
  - 평균임금이 통상임금보다 낮으면 통상임금 적용
- 연차수당 계산기
  - 1년 미만 1개월 개근 시 1일, 1년 이상 15일의 기본 구조 반영

## 계산 기준 주의사항

이 프로젝트의 계산 로직은 실서비스용 기초 버전입니다. 2026년 기준 값을 반영했지만, 실제 서비스 전과 매년 초에는 반드시 아래 항목을 다시 검토하세요.

- 실수령액 계산기의 4대보험 요율과 국민연금 상·하한, 근로소득 간이세액표 개정 여부
- 해외주식 양도세 기본공제, 배당세율, 금융소득 종합과세 기준, 외국납부세액공제 처리 방식
- 퇴직금 및 연차수당 산정 기준과 최신 행정해석
- 환율 적용 시점과 수수료 정책

## 확장 아이디어

- 실시간 환율 API 연동
- 관리자 페이지에서 FAQ/가이드/광고 슬롯 관리
- 블로그 콘텐츠 게시판 추가
- 계산 결과 공유 URL
- 다국어 페이지
- 계산기별 통계/인기 순위
# moneytools
