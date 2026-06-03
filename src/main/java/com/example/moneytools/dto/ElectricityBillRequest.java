package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ElectricityBillRequest {
    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("100000.0")
    private Double usageKwh = 350.0;

    @NotNull
    @Pattern(regexp = "NORMAL|SUMMER|WINTER", message = "계절 구분을 선택해주세요.")
    private String season = "NORMAL";

    public Double getUsageKwh() {
        return usageKwh;
    }

    public void setUsageKwh(Double usageKwh) {
        this.usageKwh = usageKwh;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
