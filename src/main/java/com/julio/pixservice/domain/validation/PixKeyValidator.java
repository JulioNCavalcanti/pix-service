package com.julio.pixservice.domain.validation;

import com.julio.pixservice.domain.model.PixKeyType;

import java.util.regex.Pattern;

public class PixKeyValidator {
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+55\\d{10,11}$");
    private static final Pattern EVP_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public static void validate(String key, PixKeyType type) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Pix key cannot be empty");
        }

        boolean isValid = switch (type) {
            case CPF -> CPF_PATTERN.matcher(key).matches();
            case EMAIL -> EMAIL_PATTERN.matcher(key).matches() && key.length() <= 77;
            case PHONE -> PHONE_PATTERN.matcher(key).matches();
            case EVP -> EVP_PATTERN.matcher(key).matches();
        };

        if (!isValid) {
            throw new IllegalArgumentException("Invalid format for Pix Key type: " + type);
        }
    }
}