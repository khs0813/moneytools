package com.example.moneytools.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("inputFieldValueFormatter")
public class InputFieldValueFormatter {
    private final NumberViewFormatter numberViewFormatter;

    public InputFieldValueFormatter(NumberViewFormatter numberViewFormatter) {
        this.numberViewFormatter = numberViewFormatter;
    }

    public String formatInteger(Object form, String fieldName) {
        return numberViewFormatter.formatInteger(resolveValue(form, fieldName));
    }

    public String formatDecimal(Object form, String fieldName) {
        return numberViewFormatter.formatDecimal(resolveValue(form, fieldName));
    }

    private Object resolveValue(Object form, String fieldName) {
        String requestValue = requestValue(fieldName);
        if (requestValue != null) {
            return requestValue;
        }

        if (form == null || !StringUtils.hasText(fieldName)) {
            return null;
        }

        try {
            return new BeanWrapperImpl(form).getPropertyValue(fieldName);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String requestValue(String fieldName) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return null;
        }

        String[] values = request.getParameterValues(fieldName);
        if (values == null || values.length == 0) {
            return null;
        }

        String first = values[0];
        return first != null ? first : null;
    }
}
