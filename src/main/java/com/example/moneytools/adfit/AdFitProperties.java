package com.example.moneytools.adfit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.adfit")
public class AdFitProperties {
    private boolean v2Enabled = false;
    private boolean pcSideEnabled = false;
    private boolean secondaryEnabled = false;
    private final Money money = new Money();

    public boolean isV2Enabled() {
        return v2Enabled;
    }

    public void setV2Enabled(boolean v2Enabled) {
        this.v2Enabled = v2Enabled;
    }

    public boolean isPcSideEnabled() {
        return pcSideEnabled;
    }

    public void setPcSideEnabled(boolean pcSideEnabled) {
        this.pcSideEnabled = pcSideEnabled;
    }

    public boolean isSecondaryEnabled() {
        return secondaryEnabled;
    }

    public void setSecondaryEnabled(boolean secondaryEnabled) {
        this.secondaryEnabled = secondaryEnabled;
    }

    public Money getMoney() {
        return money;
    }

    public String unitFor(MoneyCalculatorGroup group, AdPlacement placement) {
        String unit = switch (placement) {
            case RESULT_MOBILE -> switch (group) {
                case FINANCE -> money.financeResultMobile;
                case INCOME -> money.incomeResultMobile;
                case LIVING -> money.livingResultMobile;
            };
            case RESULT_PC -> switch (group) {
                case FINANCE -> money.financeResultPc;
                case INCOME -> money.incomeResultPc;
                case LIVING -> money.livingResultPc;
            };
            case PC_SIDE -> money.pcSide;
            case SECONDARY_MOBILE -> money.secondaryMobile;
            case SECONDARY_PC -> money.secondaryPc;
        };
        return normalize(unit);
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    public static class Money {
        private String financeResultMobile = "";
        private String financeResultPc = "";
        private String incomeResultMobile = "";
        private String incomeResultPc = "";
        private String livingResultMobile = "";
        private String livingResultPc = "";
        private String pcSide = "";
        private String secondaryMobile = "";
        private String secondaryPc = "";

        public String getFinanceResultMobile() { return financeResultMobile; }
        public void setFinanceResultMobile(String financeResultMobile) { this.financeResultMobile = financeResultMobile; }
        public String getFinanceResultPc() { return financeResultPc; }
        public void setFinanceResultPc(String financeResultPc) { this.financeResultPc = financeResultPc; }
        public String getIncomeResultMobile() { return incomeResultMobile; }
        public void setIncomeResultMobile(String incomeResultMobile) { this.incomeResultMobile = incomeResultMobile; }
        public String getIncomeResultPc() { return incomeResultPc; }
        public void setIncomeResultPc(String incomeResultPc) { this.incomeResultPc = incomeResultPc; }
        public String getLivingResultMobile() { return livingResultMobile; }
        public void setLivingResultMobile(String livingResultMobile) { this.livingResultMobile = livingResultMobile; }
        public String getLivingResultPc() { return livingResultPc; }
        public void setLivingResultPc(String livingResultPc) { this.livingResultPc = livingResultPc; }
        public String getPcSide() { return pcSide; }
        public void setPcSide(String pcSide) { this.pcSide = pcSide; }
        public String getSecondaryMobile() { return secondaryMobile; }
        public void setSecondaryMobile(String secondaryMobile) { this.secondaryMobile = secondaryMobile; }
        public String getSecondaryPc() { return secondaryPc; }
        public void setSecondaryPc(String secondaryPc) { this.secondaryPc = secondaryPc; }
    }
}
