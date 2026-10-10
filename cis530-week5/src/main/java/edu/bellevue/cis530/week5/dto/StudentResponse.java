package edu.bellevue.cis530.week5.dto;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Profile profile) {

    public record Profile(
            Long id,
            String phoneNumber,
            String address,
            String major,
            LocalDate dateOfBirth) {
    }
}
