package edu.bellevue.cis530.week5.exception;

public class DuplicateStudentEmailException extends RuntimeException {

    public DuplicateStudentEmailException(String email) {
        super("A student with email " + email + " already exists");
    }
}
