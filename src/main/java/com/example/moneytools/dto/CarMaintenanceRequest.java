package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CarMaintenanceRequest {
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100000.0")
    private Double monthlyDistanceKm = 1000.0;

    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("100.0")
    private Double fuelEfficiencyKmPerLiter = 12.0;

    @NotNull
    @DecimalMin("500.0")
    @DecimalMax("10000.0")
    private Double fuelPricePerLiter = 1700.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Double parkingFeeMonthly = 100000.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Double insuranceAnnual = 900000.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Double taxAnnual = 300000.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Double maintenanceAnnual = 600000.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Double tollMonthly = 50000.0;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10000000.0")
    private Double installmentMonthly = 0.0;

    public Double getMonthlyDistanceKm() {
        return monthlyDistanceKm;
    }

    public void setMonthlyDistanceKm(Double monthlyDistanceKm) {
        this.monthlyDistanceKm = monthlyDistanceKm;
    }

    public Double getFuelEfficiencyKmPerLiter() {
        return fuelEfficiencyKmPerLiter;
    }

    public void setFuelEfficiencyKmPerLiter(Double fuelEfficiencyKmPerLiter) {
        this.fuelEfficiencyKmPerLiter = fuelEfficiencyKmPerLiter;
    }

    public Double getFuelPricePerLiter() {
        return fuelPricePerLiter;
    }

    public void setFuelPricePerLiter(Double fuelPricePerLiter) {
        this.fuelPricePerLiter = fuelPricePerLiter;
    }

    public Double getParkingFeeMonthly() {
        return parkingFeeMonthly;
    }

    public void setParkingFeeMonthly(Double parkingFeeMonthly) {
        this.parkingFeeMonthly = parkingFeeMonthly;
    }

    public Double getInsuranceAnnual() {
        return insuranceAnnual;
    }

    public void setInsuranceAnnual(Double insuranceAnnual) {
        this.insuranceAnnual = insuranceAnnual;
    }

    public Double getTaxAnnual() {
        return taxAnnual;
    }

    public void setTaxAnnual(Double taxAnnual) {
        this.taxAnnual = taxAnnual;
    }

    public Double getMaintenanceAnnual() {
        return maintenanceAnnual;
    }

    public void setMaintenanceAnnual(Double maintenanceAnnual) {
        this.maintenanceAnnual = maintenanceAnnual;
    }

    public Double getTollMonthly() {
        return tollMonthly;
    }

    public void setTollMonthly(Double tollMonthly) {
        this.tollMonthly = tollMonthly;
    }

    public Double getInstallmentMonthly() {
        return installmentMonthly;
    }

    public void setInstallmentMonthly(Double installmentMonthly) {
        this.installmentMonthly = installmentMonthly;
    }
}
