package com.tfg.review.exceptions;

public class DataSourceNotFoundException extends RuntimeException{

    public DataSourceNotFoundException(String message) {
        super(message);
    }
}
