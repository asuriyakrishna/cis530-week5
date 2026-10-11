package edu.bellevue.cis530.week5.entity;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CurrentYearOrEarlierValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentYearOrEarlier {

    String message() default "must be less than or equal to the current year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
