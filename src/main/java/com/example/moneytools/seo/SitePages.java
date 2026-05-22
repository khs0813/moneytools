package com.example.moneytools.seo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class SitePages {
    private static final LocalDate SEO_REFRESHED_AT = LocalDate.of(2026, 5, 23);

    private SitePages() {}

    public static final List<PageInfo> ALL = List.of(
            new PageInfo("home", "/", "홈", "머니계산기 | 금융 계산기 모음", "대출이자, 대출 갈아타기, 주담대 월납입, 연봉 실수령액, 실수령액, 퇴직금, 해외주식 세금, 배당, 연차수당을 한 곳에서 계산해보세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("dividend", "/dividend-calculator", "배당 계산기", "배당 계산기 | 월배당·연배당 예상금액 계산", "보유 주식 수와 주당 배당금을 입력해 세전·세후 예상 배당금을 계산해보세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("fair-value", "/fair-value-calculator", "적정주가 계산기", "적정주가 계산기 | EPS와 PER로 목표주가 계산", "EPS, 목표 PER, 성장률, 할인율, 안전마진을 입력해 참고용 적정주가와 매수가를 계산합니다.", false, false, SEO_REFRESHED_AT),
            new PageInfo("loan", "/loan-interest-calculator", "대출이자 계산기", "대출이자 계산기 | 원리금균등·원금균등 상환 계산", "대출금액, 금리, 기간, 상환방식을 입력해 월 상환금과 총이자, 월별 상환표를 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("loan-refinance", "/loan-refinance-calculator", "대출 갈아타기 계산기", "대출 갈아타기 계산기 | 이자 절감액과 손익분기점 계산", "현재 대출과 신규 대출 조건, 중도상환수수료와 기타 비용을 입력해 대출 갈아타기 절감액과 추천 여부를 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("mortgage", "/mortgage-monthly-payment-calculator", "주담대 월납입 계산기", "주담대 월납입 계산기 | LTV와 상환 부담률 계산", "주택 가격, 보유 현금, 대출금액, 금리, LTV, 연소득을 입력해 주담대 월 납입금과 상환 부담률을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("annual-salary-net", "/annual-salary-net-calculator", "연봉 실수령 계산기", "연봉 실수령액 계산기 | 간이 세후 월급 계산", "연봉, 비과세 식대, 부양가족, 자녀 수, 퇴직금 포함 여부와 월 보너스를 입력해 간이 월 실수령액을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("salary", "/salary-calculator", "실수령액 계산기", "실수령액 계산기 | 월급·연봉 공제 후 금액 계산", "월급 또는 연봉을 입력해 4대보험, 소득세, 지방소득세를 반영한 예상 실수령액을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("severance", "/severance-pay-calculator", "퇴직금 계산기", "퇴직금 계산기 | 재직기간과 평균임금으로 계산", "입사일, 퇴사일, 평균임금 또는 최근 3개월 급여를 기준으로 예상 퇴직금을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("annual-leave", "/annual-leave-pay-calculator", "연차수당 계산기", "연차수당 계산기 | 미사용 연차수당 계산", "입사일, 사용 연차, 미사용 연차, 1일 통상임금을 입력해 예상 연차수당을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("exchange", "/exchange-calculator", "환율 계산기", "환율 계산기 | 환전 금액과 수수료 계산", "금액, 환율, 수수료율을 입력해 환전 후 예상 금액을 계산합니다.", false, false, SEO_REFRESHED_AT),
            new PageInfo("overseas-tax", "/overseas-stock-tax-calculator", "해외주식 세금 계산기", "해외주식 세금 계산기 | 양도세·배당세 예상 계산", "해외주식 매수·매도 금액과 환율, 수수료, 배당금을 입력해 예상 세금을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide", "/guide", "이용안내", "이용안내 | 계산 기준과 주의사항", "머니계산기 계산기의 입력 기준, 결과 해석 방법, 세금·투자 관련 주의사항을 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("privacy", "/privacy-policy", "개인정보처리방침", "개인정보처리방침 | 머니계산기", "머니계산기의 개인정보 처리 기준과 광고·쿠키 사용 안내를 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("contact", "/contact", "문의하기", "문의하기 | 머니계산기", "머니계산기 오류 제보, 계산 기준 문의, 제휴 문의를 보낼 수 있습니다.", true, true, SEO_REFRESHED_AT)
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
                require("loan-refinance"),
                require("mortgage"),
                require("annual-salary-net"),
                require("salary"),
                require("severance"),
                require("overseas-tax"),
                require("dividend"),
                require("annual-leave"),
                require("guide"),
                require("privacy"),
                require("contact")
        );
    }

    public static List<PageInfo> sitemap() {
        return ALL.stream().filter(PageInfo::inSitemap).toList();
    }
}
