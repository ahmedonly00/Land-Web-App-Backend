package com.iwacu250.landplots.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.iwacu250.landplots.entity.PropertyStatus;

@Documented
@Constraint(validatedBy = ValidPropertyStatus.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPropertyStatus {
    String message() default "Invalid property status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidPropertyStatus, PropertyStatus> {
        @Override
        public boolean isValid(PropertyStatus value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // Null values are handled by @NotNull
            }
            try {
                // Will throw IllegalArgumentException if value is not a valid enum constant
                PropertyStatus.valueOf(value.name());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
