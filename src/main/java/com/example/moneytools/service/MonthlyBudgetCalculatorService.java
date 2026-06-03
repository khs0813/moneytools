package com.example.moneytools.service;

import com.example.moneytools.dto.MonthlyBudgetRequest;
import com.example.moneytools.dto.MonthlyBudgetResult;
import org.springframework.stereotype.Service;

@Service
public class MonthlyBudgetCalculatorService {
    public MonthlyBudgetResult calculate(MonthlyBudgetRequest request) {
        double fixedExpenses = request.getHousing() + request.getCommunication() + request.getInsurance()
                + request.getEducation() + request.getSubscriptions();
        double variableExpenses = request.getFood() + request.getTransport() + request.getLeisure() + request.getOther();
        double totalExpenses = fixedExpenses + variableExpenses;
        double remainingAfterExpenses = request.getMonthlyIncome() - totalExpenses;
        double remainingAfterSavingsGoal = remainingAfterExpenses - request.getSavingsGoal();
        double expenseRatio = request.getMonthlyIncome() > 0 ? totalExpenses / request.getMonthlyIncome() * 100.0 : 0.0;
        double savingsGoalRatio = request.getMonthlyIncome() > 0 ? request.getSavingsGoal() / request.getMonthlyIncome() * 100.0 : 0.0;
        return new MonthlyBudgetResult(
                fixedExpenses,
                variableExpenses,
                totalExpenses,
                remainingAfterExpenses,
                remainingAfterSavingsGoal,
                expenseRatio,
                savingsGoalRatio
        );
    }
}
