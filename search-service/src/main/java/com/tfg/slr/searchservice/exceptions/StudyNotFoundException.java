package com.tfg.slr.searchservice.exceptions;

public class StudyNotFoundException extends RuntimeException {

    public StudyNotFoundException (String message) {
        super(message);
    }
}
