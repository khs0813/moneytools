package com.example.moneytools.seo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NaverProtectedPageTests {
    private static final Map<String, ProtectedPage> PROTECTED_PAGES = Map.of(
            "electricity-bill", new ProtectedPage(
                    "/electricity-bill-calculator",
                    "전기요금 계산기",
                    "전기요금 계산기 | 가정용 누진요금 참고 계산"),
            "domestic-stock-tax", new ProtectedPage(
                    "/domestic-stock-tax-calculator",
                    "국내 주식 세금 계산기",
                    "국내 주식 세금 계산기 | 매도세금·증권거래세 계산"),
            "air-conditioner-cost", new ProtectedPage(
                    "/air-conditioner-electricity-calculator",
                    "에어컨 전기세 계산기",
                    "에어컨 전기세 계산기 | 하루 8시간 사용 전기요금 계산"),
            "loan", new ProtectedPage(
                    "/loan-interest-calculator",
                    "대출이자 계산기",
                    "대출이자 계산기 | 원리금균등·원금균등 계산"),
            "annual-salary-net", new ProtectedPage(
                    "/annual-salary-net-calculator",
                    "연봉 실수령액 계산기",
                    "2026 연봉 실수령액 계산기 | 세후 월급 계산"),
            "severance", new ProtectedPage(
                    "/severance-pay-calculator",
                    "퇴직금 계산기",
                    "퇴직금 계산기 | 평균임금·상여금 포함 계산"),
            "stock-average", new ProtectedPage(
                    "/stock-average-calculator",
                    "물타기 계산기",
                    "물타기 계산기 | 주식 평균단가 계산")
    );

    @Test
    void keepsTopNaverPagesOnTheirProtectedRoutesAndTitles() {
        PROTECTED_PAGES.forEach((key, expected) -> {
            PageInfo page = SitePages.require(key);

            assertThat(page.path()).isEqualTo(expected.path());
            assertThat(page.label()).isEqualTo(expected.h1());
            assertThat(page.title()).isEqualTo(expected.title());
            assertThat(page.inSitemap()).isTrue();
        });
    }

    private record ProtectedPage(String path, String h1, String title) {}
}
