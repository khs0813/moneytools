package com.example.moneytools.seo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class SitePages {
    private static final LocalDate BASE_REFRESHED_AT = LocalDate.of(2026, 6, 4);
    private static final LocalDate SEO_REFRESHED_AT = LocalDate.of(2026, 6, 4);

    private SitePages() {}

    public static final List<PageInfo> ALL = List.of(
            new PageInfo("home", "/", "홈", "머니계산기 | 실수령액·대출·세금·생활비 계산기와 가이드", "실수령액, 대출이자, 퇴직금, 배당금, 환율, 전기요금, 자동차 유지비, 생활비 계산기와 금융·세금 가이드를 한 곳에서 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("dividend", "/dividend-calculator", "배당 계산기", "배당 계산기 | 세전·세후 배당금 계산기", "보유 주식 수와 주당 배당금, 배당 주기, 세율을 입력해 세전 배당금과 세후 배당금, 월배당·연배당 금액을 계산해보세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("fair-value", "/fair-value-calculator", "적정주가 계산기", "적정주가 계산기 | EPS와 PER로 목표주가 계산", "EPS, 목표 PER, 성장률, 할인율, 안전마진을 입력해 참고용 적정주가와 매수가를 계산합니다.", true, true, BASE_REFRESHED_AT),
            new PageInfo("loan", "/loan-interest-calculator", "대출이자 계산기", "대출이자 계산기 | 원리금균등·원금균등 계산", "대출금액, 금리, 기간, 상환방식을 입력해 월 상환금, 총이자, 월별 상환표를 간편하게 계산하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("stock-average", "/stock-average-calculator", "물타기 계산기", "물타기 계산기 | 주식 평균단가 계산", "현재 보유 수량과 평균단가, 추가 매수 수량과 단가를 입력해 물타기 후 평균단가와 총 투자금을 계산하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("loan-refinance", "/loan-refinance-calculator", "대출 갈아타기 계산기", "대출 갈아타기 계산기 | 이자 절감액 비교", "기존 대출과 신규 대출 조건을 비교해 월 상환액 차이, 총 이자 절감액, 갈아타기 손익분기점을 계산하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("mortgage", "/mortgage-monthly-payment-calculator", "주담대 월납입 계산기", "주담대 월납입 계산기 | LTV·상환부담률 계산", "주택 가격, 보유 현금, 대출금액, 금리, 연소득을 입력해 주담대 월 납입금과 소득 대비 상환 부담률을 계산하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("annual-salary-net", "/annual-salary-net-calculator", "연봉 실수령액 계산기", "2026 연봉 실수령액 계산기 | 세후 월급 계산", "2026년 기준 연봉, 비과세 식대, 부양가족 수, 자녀 수를 입력해 예상 세후 월급과 연 실수령액을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("salary", "/salary-calculator", "월급 실수령액 계산기", "2026 월급 실수령액 계산기 | 4대보험 공제 후 세후 월급", "월급 또는 연봉을 입력해 국민연금, 건강보험, 고용보험, 소득세, 지방소득세를 반영한 예상 실수령액을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("severance", "/severance-pay-calculator", "퇴직금 계산기", "퇴직금 계산기 | 평균임금·상여금 포함 계산", "입사일, 퇴사일, 최근 3개월 임금, 상여금, 연차수당을 입력해 예상 퇴직금을 계산하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("annual-leave", "/annual-leave-pay-calculator", "연차수당 계산기", "연차수당 계산기 | 미사용 연차수당 계산", "통상임금, 1일 근무시간, 미사용 연차일수를 입력해 예상 연차수당을 간편하게 계산하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("exchange", "/exchange-calculator", "환율 계산기", "환율 계산기 | 환전 금액과 수수료 계산", "금액, 환율, 수수료율을 입력해 환전 후 예상 금액을 계산합니다.", true, true, BASE_REFRESHED_AT),
            new PageInfo("electricity-bill", "/electricity-bill-calculator", "전기요금 계산기", "전기요금 계산기 | 가정용 누진요금 참고 계산", "월 사용 전력량과 계절 구분을 입력해 기본요금, 전력량요금, 부가세, 전력산업기반기금을 포함한 예상 전기요금을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("air-conditioner-cost", "/air-conditioner-electricity-calculator", "에어컨 전기세 계산기", "에어컨 전기세 계산기 | 하루 8시간 사용 전기요금 계산", "에어컨 소비전력, 하루 사용 시간, 사용 일수, kWh당 단가를 입력해 월 예상 전력 사용량과 전기요금을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("car-maintenance", "/car-maintenance-calculator", "자동차 유지비 계산기", "자동차 유지비 계산기 | 유류비·보험료·주차비 월평균 계산", "주행거리, 연비, 유가, 보험료, 자동차세, 주차비를 입력해 월 자동차 유지비와 km당 비용을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("monthly-budget", "/monthly-budget-calculator", "월 생활비 계산기", "월 생활비 계산기 | 고정비·변동비 예산 계산", "월 소득과 주거비, 식비, 교통비, 통신비, 보험료, 여가비를 입력해 총 생활비와 남는 돈, 저축 목표 달성 가능성을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("stock-tax", "/stock-tax-calculator", "주식 세금 계산기", "주식 세금 계산기 | 국내·해외주식 매도세금 계산", "국내주식과 해외주식 매도 시 발생할 수 있는 세금 차이를 비교하고, 증권거래세·양도소득세·배당세 계산기로 예상 세금을 확인해보세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("domestic-stock-tax", "/domestic-stock-tax-calculator", "국내 주식 세금 계산기", "국내 주식 세금 계산기 | 매도세금·증권거래세 계산", "국내 주식 매도금액을 입력해 증권거래세 등 예상 매도세금을 계산하고, 국내주식과 해외주식 세금 차이를 확인해보세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("overseas-tax", "/overseas-stock-tax-calculator", "해외주식 세금 계산기", "해외주식 세금 계산기 | 양도세 250만원 공제 계산", "해외주식 매수금액, 매도금액, 환율, 수수료를 입력하면 양도소득세와 배당세를 예상 계산합니다. 기본공제 250만원도 반영할 수 있습니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide", "/guide", "이용안내", "이용안내 | 계산 기준·주의사항·가이드 모음", "머니계산기 계산기의 입력 기준, 결과 해석 방법, 데이터 저장 여부와 실수령액·대출·세금·생활비 가이드를 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide-stock-tax", "/guide/stock-tax", "주식 세금 계산 방법 총정리", "주식 세금 계산 방법 총정리 | 국내·해외주식 세금 비교", "국내주식 세금, 해외주식 양도소득세, 배당소득세, 증권거래세 차이를 정리하고 관련 계산기로 예상 세금을 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide-overseas-stock-tax", "/guide/overseas-stock-tax", "해외주식 양도소득세 계산 방법", "해외주식 양도소득세 계산 방법 | 250만원 기본공제와 과세표준", "해외주식 양도차익, 기본공제 250만원, 과세표준, 예상세금 계산 흐름을 설명하고 해외주식 세금 계산기로 연결합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide-dividend-tax", "/guide/dividend-tax", "배당소득세 계산 방법", "배당소득세 계산 방법 | 세전·세후 배당금 계산", "세전 배당금, 세후 배당금, 배당소득세 15.4% 계산 예시를 정리하고 배당 계산기로 연결합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide-salary-3000", "/guide/salary-3000-net", "연봉 3000만원 실수령액은 얼마일까?", "연봉 3000만원 실수령액은 얼마일까? | 초봉 구간 세후 월급 가이드", "연봉 3,000만 원의 예상 세후 월급과 1인 가구 기준 생활비 해석 포인트를 정리합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-salary-5000", "/guide/salary-5000-net", "연봉 5,000만 원 실수령액은 얼마일까?", "연봉 5,000만 원 실수령액은 얼마일까? | 세후 월급 계산 가이드", "연봉 5,000만 원 기준 월 세전 급여와 예상 실수령액, 공제 항목, 자주 하는 실수를 예시와 함께 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-salary-100m", "/guide/salary-100m-net", "연봉 1억원 실수령액은 얼마일까?", "연봉 1억원 실수령액은 얼마일까? | 고소득 구간 세후 월급 가이드", "연봉 1억 원의 월 세전 급여와 실수령액 차이, 세금 체감 포인트를 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-monthly-salary-300", "/guide/monthly-salary-300-net", "월급 300만원 실수령액은 얼마일까?", "월급 300만원 실수령액은 얼마일까? | 세후 월급과 생활비 해석", "월급 300만 원의 예상 실수령액과 고정비 계획 포인트를 실제 예시와 함께 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-loan-100m-interest", "/guide/loan-100m-interest", "대출 1억을 금리 5%로 빌리면 한 달 이자는 얼마일까?", "대출 1억 금리 5% 한 달 이자 | 계산 예시와 상환 방식 비교", "대출 1억 원을 연 5%로 빌릴 때 월 이자와 상환 방식별 월 납입액 차이를 실제 숫자로 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-repayment-difference", "/guide/repayment-method-difference", "원리금균등상환과 원금균등상환 차이", "원리금균등상환과 원금균등상환 차이 | 월 납입액과 총이자 비교", "원리금균등, 원금균등, 만기일시상환 구조 차이와 어떤 상황에서 무엇이 유리한지 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-ltv-dsr", "/guide/ltv-dsr-difference", "주담대 LTV와 DSR 차이", "주담대 LTV와 DSR 차이 | 한도와 상환능력 보는 법", "LTV와 DSR이 무엇이 다른지, 주담대 한도와 상환 부담을 어떻게 해석해야 하는지 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-severance-average-wage", "/guide/severance-average-wage", "퇴직금 계산할 때 평균임금이 중요한 이유", "퇴직금 계산할 때 평균임금이 중요한 이유 | 평균임금·통상임금 설명", "퇴직금 계산에서 평균임금과 통상임금이 어떤 역할을 하는지, 왜 마지막 3개월 임금이 중요한지 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-overseas-deduction", "/guide/overseas-stock-tax-deduction", "해외주식 양도세 250만 원 공제 쉽게 설명", "해외주식 양도세 250만 원 공제 쉽게 설명 | 손익통산과 과세표준", "해외주식 양도차익 기본공제 250만 원이 어떤 방식으로 적용되는지 쉽게 설명하고 계산 예시를 제공합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-electricity-tier", "/guide/electricity-tier", "전기요금 누진세 구간 쉽게 정리", "전기요금 누진세 구간 쉽게 정리 | 가정용 전기요금 이해하기", "가정용 전기요금의 누진 구간과 기본요금, 전력량요금, 부가세를 이해하기 쉽게 정리합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-aircon-8hours", "/guide/aircon-8hours-cost", "에어컨 하루 8시간 사용 시 전기요금 계산 방법", "에어컨 하루 8시간 사용 시 전기요금 계산 방법 | 소비전력으로 계산하기", "에어컨 소비전력과 사용 시간을 이용해 하루 8시간 기준 월 전기요금을 계산하는 방법을 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-car-cost", "/guide/car-monthly-cost", "자동차 유지비 한 달 평균 비용 계산 방법", "자동차 유지비 한 달 평균 비용 계산 방법 | 유류비·보험료·주차비 정리", "자동차 유지비를 월 단위로 계산할 때 어떤 항목을 넣어야 하는지와 평균 비용 보는 법을 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-dividend-100m", "/guide/dividend-100man", "배당금으로 월 100만 원 받으려면 얼마가 필요할까?", "배당금으로 월 100만 원 받으려면 얼마가 필요할까? | 목표 배당금 역산", "목표 월 배당금 100만 원을 만들기 위해 필요한 투자 원금을 배당수익률별로 계산하는 방법을 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-budget-items", "/guide/monthly-budget-items", "생활비 예산을 계산할 때 꼭 봐야 할 항목", "생활비 예산을 계산할 때 꼭 봐야 할 항목 | 고정비·변동비 체크리스트", "생활비 예산표를 만들 때 빠뜨리기 쉬운 고정비와 변동비 항목, 저축 목표 반영 방법을 정리합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("guide-per-limit", "/guide/per-fair-value-limit", "PER 적정주가 계산의 한계", "PER 적정주가 계산의 한계 | 적정주가 계산기 해석법", "PER 방식 적정주가 계산이 왜 단순 참고용인지, 어떤 한계와 왜곡이 있는지 설명합니다.", false, true, SEO_REFRESHED_AT),
            new PageInfo("privacy", "/privacy-policy", "개인정보처리방침", "개인정보처리방침 | 머니계산기", "머니계산기의 개인정보 처리 기준과 광고·쿠키 사용 안내를 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("about", "/about", "사이트 소개", "사이트 소개 | 머니계산기 운영 목적과 편집 원칙", "머니계산기가 어떤 기준으로 계산기와 가이드를 운영하는지, 업데이트 원칙과 편집 기준을 확인하세요.", false, false, SEO_REFRESHED_AT),
            new PageInfo("terms", "/terms", "이용약관", "이용약관 | 머니계산기 서비스 이용 조건", "머니계산기 계산기와 정보성 콘텐츠의 이용 조건, 책임 범위, 금지 행위를 확인하세요.", false, true, SEO_REFRESHED_AT),
            new PageInfo("disclaimer", "/disclaimer", "면책고지", "면책고지 | 머니계산기", "머니계산기에서 제공하는 계산 결과와 정보의 이용 범위, 한계, 책임 제한 기준을 확인하세요.", true, true, BASE_REFRESHED_AT),
            new PageInfo("contact", "/contact", "문의하기", "문의하기 | 머니계산기", "머니계산기 오류 제보, 계산 기준 문의, 제휴 문의를 보낼 수 있습니다.", true, false, SEO_REFRESHED_AT)
    );

    public static Optional<PageInfo> findByKey(String key) {
        return ALL.stream().filter(page -> page.key().equals(key)).findFirst();
    }

    public static PageInfo require(String key) {
        return findByKey(key).orElseThrow(() -> new IllegalArgumentException("Unknown page key: " + key));
    }

    public static List<PageInfo> navigation() {
        return List.of(
                require("loan"),
                require("stock-average"),
                require("loan-refinance"),
                require("stock-tax"),
                require("domestic-stock-tax"),
                require("overseas-tax"),
                require("mortgage"),
                require("dividend"),
                require("annual-salary-net"),
                require("salary"),
                require("severance"),
                require("annual-leave"),
                require("fair-value"),
                require("exchange"),
                require("electricity-bill"),
                require("air-conditioner-cost"),
                require("car-maintenance"),
                require("monthly-budget"),
                require("guide"),
                require("privacy"),
                require("contact")
        );
    }

    public static List<PageInfo> guides() {
        return ALL.stream().filter(page -> page.key().startsWith("guide-")).toList();
    }

    public static List<PageInfo> sitemap() {
        return ALL.stream().filter(PageInfo::inSitemap).toList();
    }
}
