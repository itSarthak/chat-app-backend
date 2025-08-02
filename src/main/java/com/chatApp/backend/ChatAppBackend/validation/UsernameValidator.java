package com.chatApp.backend.ChatAppBackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<UsernameConstruct, String> {
    private static final Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) return true;
        if (username.trim().isEmpty()) return false;
        return VALID_USERNAME.matcher(username).matches();
    }
}
