package com.example.moneytools.view;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Iterator;

@Component("requestParamNumberFieldFormatter")
public class RequestParamNumberFieldFormatter {
    private final NumberViewFormatter numberViewFormatter;

    public RequestParamNumberFieldFormatter(NumberViewFormatter numberViewFormatter) {
        this.numberViewFormatter = numberViewFormatter;
    }

    public String formatInteger(Object requestParamValue, Object fallbackValue) {
        return numberViewFormatter.formatInteger(resolveValue(requestParamValue, fallbackValue));
    }

    public String formatDecimal(Object requestParamValue, Object fallbackValue) {
        return numberViewFormatter.formatDecimal(resolveValue(requestParamValue, fallbackValue));
    }

    private Object resolveValue(Object requestParamValue, Object fallbackValue) {
        Object firstRequestValue = firstValue(requestParamValue);
        if (firstRequestValue != null) {
            return firstRequestValue;
        }
        return fallbackValue;
    }

    private Object firstValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0 ? firstValue(Array.get(value, 0)) : null;
        }
        if (value instanceof Iterable<?> iterable) {
            Iterator<?> iterator = iterable.iterator();
            return iterator.hasNext() ? firstValue(iterator.next()) : null;
        }
        return value;
    }
}
