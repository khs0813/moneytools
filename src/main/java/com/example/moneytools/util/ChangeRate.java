package com.example.moneytools.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ChangeRate {
    private ChangeRate() {}

    public static Result calculate(double previous, double current) {
        if (!Double.isFinite(previous) || !Double.isFinite(current)) {
            return Result.notComparable();
        }
        if (previous == 0.0 && current > 0.0) {
            return Result.newValue();
        }
        if (previous == 0.0) {
            return Result.percent(0.0);
        }
        double rate = ((current - previous) / previous) * 100.0;
        return Double.isFinite(rate) ? Result.percent(round(rate)) : Result.notComparable();
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public record Result(double percent, Status status) {
        public static Result percent(double percent) {
            return new Result(percent, Status.PERCENT);
        }

        public static Result newValue() {
            return new Result(0.0, Status.NEW);
        }

        public static Result notComparable() {
            return new Result(0.0, Status.NOT_COMPARABLE);
        }

        public boolean comparable() {
            return status == Status.PERCENT;
        }
    }

    public enum Status {
        PERCENT,
        NEW,
        NOT_COMPARABLE
    }
}
