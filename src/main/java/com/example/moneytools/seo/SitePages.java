package com.example.moneytools.seo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class SitePages {
    private static final LocalDate SEO_REFRESHED_AT = LocalDate.of(2026, 5, 26);

    private SitePages() {}

    public static final List<PageInfo> ALL = List.of(
            new PageInfo("home", "/", "홈", "머니계산기 | 금융 계산기 모음", "대출이자, 주담대 월납입, 연봉 실수령액, 퇴직금, 해외주식 세금, 배당까지 자주 찾는 금융 계산기를 한 곳에서 이용하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("dividend", "/dividend-calculator", "배당 계산기", "배당 계산기 | 세전·세후 월배당 계산", "보유 주식 수, 주당 배당금, 배당 주기, 원천징수세율을 입력해 세전·세후 배당금과 월·연 배당금을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("fair-value", "/fair-value-calculator", "적정주가 계산기", "적정주가 계산기 | EPS와 PER로 목표주가 계산", "EPS, 목표 PER, 성장률, 할인율, 안전마진을 입력해 참고용 적정주가와 매수가를 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("loan", "/loan-interest-calculator", "대출이자 계산기", "대출이자 계산기 | 원리금균등·원금균등 계산", "대출금액, 금리, 기간, 상환방식을 입력해 월 상환금, 총이자, 월별 상환표를 간편하게 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("stock-average", "/stock-average-calculator", "물타기 계산기", "물타기 계산기 | 주식 평균단가 계산", "현재 보유 수량과 평균단가, 추가 매수 수량과 단가를 입력해 물타기 후 평균단가와 총 투자금을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("loan-refinance", "/loan-refinance-calculator", "대출 갈아타기 계산기", "대출 갈아타기 계산기 | 이자 절감액 비교", "기존 대출과 신규 대출 조건을 비교해 월 상환액 차이, 총 이자 절감액, 갈아타기 손익분기점을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("mortgage", "/mortgage-monthly-payment-calculator", "주담대 월납입 계산기", "주담대 월납입 계산기 | LTV·상환부담률 계산", "주택 가격, 보유 현금, 대출금액, 금리, 연소득을 입력해 주담대 월 납입금과 소득 대비 상환 부담률을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("annual-salary-net", "/annual-salary-net-calculator", "연봉 실수령액 계산기", "2026 연봉 실수령액 계산기 | 세후 월급 계산", "2026년 기준 연봉, 비과세 식대, 부양가족 수, 자녀 수를 입력해 예상 세후 월급과 연 실수령액을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("salary", "/salary-calculator", "월급 실수령액 계산기", "2026 월급 실수령액 계산기 | 4대보험 공제 계산", "월급 또는 연봉을 입력해 국민연금, 건강보험, 고용보험, 소득세, 지방소득세를 반영한 예상 실수령액을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("severance", "/severance-pay-calculator", "퇴직금 계산기", "퇴직금 계산기 | 평균임금·상여금 포함 계산", "입사일, 퇴사일, 최근 3개월 임금, 상여금, 연차수당을 입력해 예상 퇴직금을 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("annual-leave", "/annual-leave-pay-calculator", "연차수당 계산기", "연차수당 계산기 | 미사용 연차수당 계산", "통상임금, 1일 근무시간, 미사용 연차일수를 입력해 예상 연차수당을 간편하게 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("exchange", "/exchange-calculator", "환율 계산기", "환율 계산기 | 환전 금액과 수수료 계산", "금액, 환율, 수수료율을 입력해 환전 후 예상 금액을 계산합니다.", true, true, SEO_REFRESHED_AT),
            new PageInfo("overseas-tax", "/overseas-stock-tax-calculator", "해외주식 세금 계산기", "해외주식 세금 계산기 | 양도세·배당세 계산", "해외주식 매수·매도 금액, 환율, 수수료, 배당금을 입력해 예상 양도소득세와 배당세를 계산하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("guide", "/guide", "이용안내", "이용안내 | 계산 기준·주의사항·개인정보 안내 - 머니계산기", "머니계산기 계산기의 입력 기준, 결과 해석 방법, 데이터 저장 여부, 세금·투자·노무 관련 주의사항을 확인하세요.", true, true, SEO_REFRESHED_AT),
            new PageInfo("privacy", "/privacy", "개인정보처리방침", "개인정보처리방침 | 머니계산기", "머니계산기의 개인정보 처리 기준과 광고·쿠키 사용 안내를 확인하세요.", true, true, SEO_REFRESHED_AT),
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
                require("stock-average"),
                require("loan-refinance"),
                require("mortgage"),
                require("annual-salary-net"),
                require("salary"),
                require("severance"),
                require("overseas-tax"),
                require("dividend"),
                require("annual-leave"),
                require("fair-value"),
                require("exchange"),
                require("guide"),
                require("privacy"),
                require("contact")
        );
    }

    public static List<PageInfo> sitemap() {
        return ALL.stream().filter(PageInfo::inSitemap).toList();
    }
}
