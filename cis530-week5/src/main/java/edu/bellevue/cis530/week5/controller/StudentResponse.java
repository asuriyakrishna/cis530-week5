package edu.bellevue.cis530.week5.controller;

import java.math.BigDecimal;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String major,
        BigDecimal gpa,
        Integer enrollmentYear,
        Profile profile) {

    public record Profile(
            Long id,
            String phone,
            String address,
            String city,
            String state,
            String zip,
            String emergencyContactName,
            String emergencyContactPhone) {
    }
}
