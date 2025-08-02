package com.chatApp.backend.ChatAppBackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy =  UsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameConstruct {
    String message() default "Invalid Username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
