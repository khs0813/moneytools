# 머니계산기

머니계산기는 배당, 적정주가, 대출이자, 실수령액, 퇴직금, 연차수당, 환율, 해외주식 세금을 한 곳에서 계산하는 Spring Boot 기반 금융 계산기 웹사이트입니다. Thymeleaf 서버사이드 렌더링으로 화면을 구성하고, 검색 노출과 광고 운영을 고려한 SEO/AdSense 설정 구조를 포함합니다.

## 기술 스택

- Java 17
- Spring Boot 3.5.11
- Spring Web MVC
- Thymeleaf
- Bean Validation
- Maven
- HTML, CSS, JavaScript

## 실행 방법

개발 서버 실행:

```bash
mvn spring-boot:run
```

브라우저 접속:

```text
http://localhost:8080
```

패키징 후 실행:

```bash
mvn clean package
java -jar target/moneytools-0.0.1-SNAPSHOT.jar
```

## DB 설정 방법

현재 프로젝트는 계산 로직 중심의 웹 애플리케이션이며 기본 실행에 데이터베이스가 필요하지 않습니다. DB를 추가로 연동하는 경우 실제 접속 정보는 Git에 커밋하지 말고 환경변수 또는 커밋 제외된 로컬 설정 파일에 보관하세요.

권장 방식:

- 공용 예시값: `src/main/resources/application-example.yml`
- 로컬 실제값: `src/main/resources/application-local.yml`
- 민감 운영값: 환경변수 또는 배포 플랫폼의 Secret 관리 기능

예시:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/moneytools}
    username: ${DB_USERNAME:moneytools_user}
    password: ${DB_PASSWORD:change-me}
```

`application-local.yml`과 `application-secret.yml`은 `.gitignore`에 포함되어 있으므로 실제 비밀번호, 토큰, API Key를 저장할 때도 커밋 대상에서 제외됩니다.

## 환경변수 예시

```bash
export APP_NAME="머니계산기"
export APP_BASE_URL="https://www.moneycalculator.co.kr"
export APP_CONTACT_EMAIL="contact@moneycalculator.co.kr"
export GOOGLE_SITE_VERIFICATION="google-search-console-value"
export NAVER_SITE_VERIFICATION="naver-search-advisor-value"
export ADSENSE_ENABLED="false"
export ADSENSE_CLIENT_ID="ca-pub-0000000000000000"
export ADSENSE_SLOT_TOP="0000000000"
export ADSENSE_SLOT_CONTENT="1111111111"
export ADSENSE_SLOT_RESULT="2222222222"
export ADSENSE_SLOT_SIDEBAR="3333333333"
```

## 주요 기능

- 배당 계산기
- 적정주가 계산기
- 대출이자 계산기 및 상환표 CSV 다운로드
- 실수령액 계산기
- 퇴직금 계산기
- 연차수당 계산기
- 환율 계산기
- 해외주식 세금 계산기
- `robots.txt`, `sitemap.xml`, `rss.xml`, `ads.txt`
- 페이지별 title, description, canonical, OG, Twitter Card
- JSON-LD 구조화 데이터
- Google Search Console / Naver Search Advisor 인증 메타 태그 설정
- 404/500 에러 페이지

## 화면 및 API 설명

주요 화면:

- `/` : 계산기 목록과 서비스 홈
- `/dividend-calculator` : 배당 계산기
- `/fair-value-calculator` : 적정주가 계산기
- `/loan-interest-calculator` : 대출이자 계산기
- `/salary-calculator` : 실수령액 계산기
- `/severance-pay-calculator` : 퇴직금 계산기
- `/annual-leave-pay-calculator` : 연차수당 계산기
- `/exchange-calculator` : 환율 계산기
- `/overseas-stock-tax-calculator` : 해외주식 세금 계산기
- `/guide` : 계산 기준 안내
- `/privacy-policy` : 개인정보처리방침
- `/contact` : 문의 안내

계산 요청은 각 화면의 폼 제출을 통해 서버에서 처리됩니다. SEO 관련 공개 엔드포인트는 다음과 같습니다.

- `/robots.txt`
- `/sitemap.xml`
- `/rss.xml`
- `/ads.txt`

## 운영 전 점검

- `APP_BASE_URL`을 실제 운영 도메인으로 설정
- `moneycalculator.co.kr`과 `www.moneycalculator.co.kr` DNS를 배포 서버로 연결
- 검색 대표 주소는 `https://www.moneycalculator.co.kr`로 사용
- Search Console 및 Naver 인증값 설정
- AdSense 승인 후 client ID와 slot ID 교체
- `src/main/resources/static/ads.txt`의 publisher ID 교체
- 4대보험 요율, 세율, 공제 기준 등 정책성 계산값 최신 여부 확인
