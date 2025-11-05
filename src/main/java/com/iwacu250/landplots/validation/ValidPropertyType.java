package com.iwacu250.landplots.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.iwacu250.landplots.entity.PropertyType;

@Documented
@Constraint(validatedBy = ValidPropertyType.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPropertyType {
    String message() default "Invalid property type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidPropertyType, PropertyType> {
        @Override
        public boolean isValid(PropertyType value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // Null values are handled by @NotNull
            }
            try {
                // Will throw IllegalArgumentException if value is not a valid enum constant
                PropertyType.valueOf(value.name());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
