package edu.bellevue.cis530.week5.entity;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CurrentYearOrEarlierValidator implements ConstraintValidator<CurrentYearOrEarlier, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || value <= Year.now().getValue();
    }
}
