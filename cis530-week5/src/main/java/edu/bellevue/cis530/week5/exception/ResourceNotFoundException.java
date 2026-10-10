package edu.bellevue.cis530.week5.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long id) {
        super("Student with id " + id + " was not found");
    }
}
