# Android Play 출시 준비

이 프로젝트는 Spring Boot/Thymeleaf 웹앱이므로 Android 앱은 동일한 웹 서비스를 여는 Trusted Web Activity(TWA) 래퍼로 배포하는 것을 권장합니다. 계산 로직을 Android와 서버에 중복 구현하지 않고, 웹 배포만으로 계산기 콘텐츠와 정책 문서를 갱신할 수 있습니다.

## 이번 저장소 반영 사항

- `/site.webmanifest`: Android 설치형 앱에 필요한 앱 이름, 범위, 시작 URL, 카테고리, 192/512/maskable 아이콘 정의
- `/service-worker.js`: 앱 셸 캐시와 오프라인 안내 페이지 제공
- `/offline.html`: 네트워크가 끊긴 상태의 안내 화면
- `/icons/*.png`: Play/TWA/PWA용 앱 아이콘
- `/.well-known/assetlinks.json`: TWA 검증용 Digital Asset Links 응답
- `/privacy-policy`: Android 앱 이용 시 데이터 처리 안내 보강

## 운영 환경변수

```bash
export APP_BASE_URL="https://www.moneycalculator.co.kr"
export APP_ANDROID_PACKAGE_NAME="com.moneycalculator.app"
export APP_ANDROID_SHA256_CERT_FINGERPRINTS="AA:BB:CC:DD:...:99"
```

`APP_ANDROID_SHA256_CERT_FINGERPRINTS`에는 Google Play Console의 `앱 무결성 > 앱 서명 키 인증서 > SHA-256` 값을 넣습니다. 로컬 APK 직접 설치 테스트까지 TWA 검증을 통과시키려면 업로드 키 또는 로컬 서명 키의 SHA-256도 콤마로 함께 넣을 수 있습니다.

설정 후 아래 URL이 빈 배열이 아닌 관계 선언을 반환해야 합니다.

```text
https://www.moneycalculator.co.kr/.well-known/assetlinks.json
```

## TWA 래퍼 생성

```bash
npm i -g @bubblewrap/cli
bubblewrap doctor
bubblewrap init --manifest=https://www.moneycalculator.co.kr/site.webmanifest
bubblewrap build
```

초기화 시 권장값:

- Package ID: `com.moneycalculator.app`
- App name: `머니계산기`
- Start URL: `https://www.moneycalculator.co.kr/`
- Orientation: portrait
- Display mode: standalone
- Output: Android App Bundle(`.aab`)

2026년 8월 31일부터 Google Play 신규 앱과 업데이트는 Android 16(API 36) 이상 타깃이 필요합니다. Bubblewrap이 생성한 Android 프로젝트의 `targetSdkVersion` 또는 `targetSdk`가 36 이상인지 제출 전에 확인하세요.

## Play Console 등록 전 체크리스트

- 운영 도메인은 HTTPS로 접속 가능해야 합니다.
- `/site.webmanifest`, `/service-worker.js`, `/icons/icon-512.png`, `/privacy-policy`, `/.well-known/assetlinks.json`이 운영 도메인에서 200으로 응답해야 합니다.
- 앱 서명 키 SHA-256을 `APP_ANDROID_SHA256_CERT_FINGERPRINTS`에 반영한 뒤 서버를 재배포해야 합니다.
- Play Console 앱 콘텐츠/데이터 보안 답변은 실제 운영 상태와 맞춰야 합니다. AdSense를 켠 경우 광고 식별자, 쿠키, 앱 활동 처리 여부를 보수적으로 검토하세요.
- 금융·세금 계산 결과는 참고용이라는 면책 문구가 앱 설명, 개인정보처리방침, 사이트 화면에서 일관되게 보여야 합니다.

## 스토어 등록 문구 초안

앱 이름:

```text
머니계산기
```

짧은 설명:

```text
실수령액, 대출이자, 퇴직금, 배당, 세금, 생활비를 한 번에 계산
```

전체 설명:

```text
머니계산기는 실수령액, 대출이자, 퇴직금, 연차수당, 배당금, 환율, 해외주식 세금, 전기요금, 자동차 유지비, 월 생활비를 간편하게 확인하는 금융·생활 계산기 앱입니다.

각 계산기는 입력값을 기준으로 예상 결과를 제공하고, 계산 기준과 주의사항, 관련 가이드를 함께 안내합니다. 계산 결과는 참고용이며 실제 세금, 급여, 대출 심사, 요금, 투자 판단은 법령, 기관 고시, 회사 정책, 금융사 조건, 신고 기준에 따라 달라질 수 있습니다.
```
