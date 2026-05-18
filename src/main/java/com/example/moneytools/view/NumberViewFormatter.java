package com.example.moneytools.view;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Iterator;

@Component("numberViewFormatter")
public class NumberViewFormatter {

    public String formatInteger(Object value) {
        return format(value, false);
    }

    public String formatDecimal(Object value) {
        return format(value, true);
    }

    private String format(Object value, boolean allowFraction) {
        Object resolved = unwrapValue(value);
        if (resolved == null) {
            return "";
        }

        String raw = String.valueOf(resolved).trim();
        if (raw.isEmpty()) {
            return "";
        }

        String normalized = raw
                .replace(",", "")
                .replace("，", "")
                .replaceAll("[\\s\\u00A0\\u2007\\u202F]", "")
                .trim();

        if (normalized.isEmpty()) {
            return "";
        }

        try {
            BigDecimal decimal = new BigDecimal(normalized);
            if (!allowFraction) {
                try {
                    return groupInteger(decimal.toBigIntegerExact().toString());
                } catch (ArithmeticException ex) {
                    BigDecimal stripped = decimal.stripTrailingZeros();
                    if (stripped.scale() <= 0) {
                        return groupInteger(stripped.toPlainString());
                    }
                    return raw;
                }
            }

            BigDecimal stripped = decimal.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : decimal.stripTrailingZeros();

            return groupDecimal(stripped.toPlainString());
        } catch (NumberFormatException ex) {
            return raw;
        }
    }

    private Object unwrapValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return value;
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0 ? unwrapValue(Array.get(value, 0)) : null;
        }
        if (value instanceof Iterable<?> iterable) {
            Iterator<?> iterator = iterable.iterator();
            return iterator.hasNext() ? unwrapValue(iterator.next()) : null;
        }
        return value;
    }

    private String groupInteger(String plain) {
        String sign = "";
        String digits = plain;
        if (digits.startsWith("-")) {
            sign = "-";
            digits = digits.substring(1);
        } else if (digits.startsWith("+")) {
            digits = digits.substring(1);
        }

        digits = digits.replaceFirst("^0+(?!$)", "");
        if (!digits.matches("\\d+")) {
            return plain;
        }
        if (digits.matches("0+")) {
            return "0";
        }

        return sign + digits.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
    }

    private String groupDecimal(String plain) {
        String sign = "";
        String working = plain;
        if (working.startsWith("-")) {
            sign = "-";
            working = working.substring(1);
        } else if (working.startsWith("+")) {
            working = working.substring(1);
        }

        String integerPart = working;
        String fractionPart = "";
        int dotIndex = working.indexOf('.');
        if (dotIndex >= 0) {
            integerPart = working.substring(0, dotIndex);
            fractionPart = working.substring(dotIndex + 1);
        }

        integerPart = integerPart.replaceFirst("^0+(?!$)", "");
        if (integerPart.isEmpty()) {
            integerPart = "0";
        }
        if (!integerPart.matches("\\d+")) {
            return plain;
        }

        String groupedInteger = integerPart.replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
        if (fractionPart.isEmpty()) {
            return (sign + groupedInteger).equals("-0") ? "0" : sign + groupedInteger;
        }
        return sign + groupedInteger + "." + fractionPart;
    }
}
