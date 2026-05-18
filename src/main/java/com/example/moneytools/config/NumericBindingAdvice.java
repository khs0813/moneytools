package com.example.moneytools.config;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

@ControllerAdvice
public class NumericBindingAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Double.class, new CommaAwareNumberEditor(Double.class));
        binder.registerCustomEditor(Integer.class, new CommaAwareNumberEditor(Integer.class));
        binder.registerCustomEditor(Long.class, new CommaAwareNumberEditor(Long.class));
        binder.registerCustomEditor(BigDecimal.class, new BigDecimalCommaEditor());
    }

    private static String normalize(String text) {
        if (text == null) return null;

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
                // 원본 값으로 검증 오류를 유도한다.
            }
        }

        return normalized;
    }

    private static class CommaAwareNumberEditor extends CustomNumberEditor {
        CommaAwareNumberEditor(Class<? extends Number> numberClass) {
            super(numberClass, true);
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            super.setAsText(normalize(text));
        }
    }

    private static class BigDecimalCommaEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            String normalized = normalize(text);
            if (normalized == null || normalized.isBlank()) {
                setValue(null);
                return;
            }
            setValue(new BigDecimal(normalized));
        }
    }
}
