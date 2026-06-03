package com.example.moneytools.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class MonthlyBudgetRequest {
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double monthlyIncome = 3500000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double housing = 900000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double food = 600000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double transport = 200000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double communication = 100000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double insurance = 150000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double education = 0.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double subscriptions = 50000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double leisure = 300000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double savingsGoal = 700000.0;
    @NotNull @DecimalMin("0.0") @DecimalMax("999999999999999.0")
    private Double other = 200000.0;

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public Double getHousing() { return housing; }
    public void setHousing(Double housing) { this.housing = housing; }
    public Double getFood() { return food; }
    public void setFood(Double food) { this.food = food; }
    public Double getTransport() { return transport; }
    public void setTransport(Double transport) { this.transport = transport; }
    public Double getCommunication() { return communication; }
    public void setCommunication(Double communication) { this.communication = communication; }
    public Double getInsurance() { return insurance; }
    public void setInsurance(Double insurance) { this.insurance = insurance; }
    public Double getEducation() { return education; }
    public void setEducation(Double education) { this.education = education; }
    public Double getSubscriptions() { return subscriptions; }
    public void setSubscriptions(Double subscriptions) { this.subscriptions = subscriptions; }
    public Double getLeisure() { return leisure; }
    public void setLeisure(Double leisure) { this.leisure = leisure; }
    public Double getSavingsGoal() { return savingsGoal; }
    public void setSavingsGoal(Double savingsGoal) { this.savingsGoal = savingsGoal; }
    public Double getOther() { return other; }
    public void setOther(Double other) { this.other = other; }
}
