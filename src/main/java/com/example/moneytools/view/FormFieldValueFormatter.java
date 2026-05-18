package com.example.moneytools.view;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

@Component("formFieldValueFormatter")
public class FormFieldValueFormatter {
    private static final String DEFAULT_FORM_NAME = "form";

    private final NumberViewFormatter numberViewFormatter;

    public FormFieldValueFormatter(NumberViewFormatter numberViewFormatter) {
        this.numberViewFormatter = numberViewFormatter;
    }

    public String formatInteger(Object variables, Object form, String fieldName) {
        return numberViewFormatter.formatInteger(resolveValue(variables, form, fieldName));
    }

    public String formatDecimal(Object variables, Object form, String fieldName) {
        return numberViewFormatter.formatDecimal(resolveValue(variables, form, fieldName));
    }

    private Object resolveValue(Object variables, Object form, String fieldName) {
        BindingResult bindingResult = extractBindingResult(variables);
        if (bindingResult != null) {
            FieldError fieldError = bindingResult.getFieldError(fieldName);
            if (fieldError != null && fieldError.getRejectedValue() != null) {
                return fieldError.getRejectedValue();
            }

            Object rawFieldValue = bindingResult.getRawFieldValue(fieldName);
            if (rawFieldValue != null) {
                return rawFieldValue;
            }

            Object fieldValue = bindingResult.getFieldValue(fieldName);
            if (fieldValue != null) {
                return fieldValue;
            }
        }

        if (form == null) {
            return null;
        }

        try {
            return new BeanWrapperImpl(form).getPropertyValue(fieldName);
        } catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private BindingResult extractBindingResult(Object variables) {
        if (!(variables instanceof Map<?, ?> rawMap)) {
            return null;
        }

        Object candidate = ((Map<String, Object>) rawMap).get(BindingResult.MODEL_KEY_PREFIX + DEFAULT_FORM_NAME);
        if (candidate instanceof BindingResult bindingResult) {
            return bindingResult;
        }
        return null;
    }
}
