package com.example.moneytools.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public final class NumberTextUtils {
    private static final Pattern GROUPING_TARGET = Pattern.compile("\\B(?=(\\d{3})+(?!\\d))");

    private NumberTextUtils() {
    }

    public static String normalizeForParsing(String text) {
        if (text == null) {
            return null;
        }

        String normalized = text
                .replace(",", "")
                .replace("，", "")
                .replace(" ", "")
                .trim();

        if (normalized.isBlank()) {
            return normalized;
        }

        if (normalized.contains("e") || normalized.contains("E")) {
            try {
                normalized = new BigDecimal(normalized).stripTrailingZeros().toPlainString();
            } catch (NumberFormatException ignored) {
                // 원본 문자열을 그대로 돌려서 이후 검증 오류를 유도한다.
            }
        }

        return normalized;
    }

    public static String formatForDisplay(Number value) {
        if (value == null) {
            return "";
        }

        String plain = toPlainString(value);
        if (plain == null || plain.isBlank()) {
            return "";
        }

        String sign = "";
        String unsigned = plain;
        if (unsigned.startsWith("-")) {
            sign = "-";
            unsigned = unsigned.substring(1);
        } else if (unsigned.startsWith("+")) {
            unsigned = unsigned.substring(1);
        }

        String[] parts = unsigned.split("\\.", 2);
        String integerPart = parts.length > 0 ? parts[0] : "0";
        String fractionPart = parts.length > 1 ? parts[1] : "";

        integerPart = integerPart.replaceFirst("^0+(?=\\d)", "");
        if (integerPart.isBlank()) {
            integerPart = "0";
        }

        String groupedInteger = GROUPING_TARGET.matcher(integerPart).replaceAll(",");
        if ("0".equals(integerPart) && fractionPart.isEmpty()) {
            sign = "";
        }

        return sign + groupedInteger + (fractionPart.isEmpty() ? "" : "." + fractionPart);
    }

    public static String toPlainString(Number value) {
        if (value == null) {
            return "";
        }

        try {
            if (value instanceof BigDecimal decimal) {
                return strip(decimal);
            }
            if (value instanceof BigInteger integer) {
                return integer.toString();
            }
            if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
                return String.valueOf(value.longValue());
            }
            if (value instanceof Float || value instanceof Double) {
                double doubleValue = value.doubleValue();
                if (!Double.isFinite(doubleValue)) {
                    return String.valueOf(doubleValue);
                }
                return strip(BigDecimal.valueOf(doubleValue));
            }
            return strip(new BigDecimal(String.valueOf(value)));
        } catch (NumberFormatException ex) {
            return String.valueOf(value);
        }
    }

    private static String strip(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
