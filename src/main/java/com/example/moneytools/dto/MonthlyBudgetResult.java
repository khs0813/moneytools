package com.example.moneytools.dto;

public record MonthlyBudgetResult(
        double fixedExpenses,
        double variableExpenses,
        double totalExpenses,
        double remainingAfterExpenses,
        double remainingAfterSavingsGoal,
        double expenseRatio,
        double savingsGoalRatio
) {
}
