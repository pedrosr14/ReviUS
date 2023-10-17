package com.tfg.review.exceptions;

public class ServiceDownException extends RuntimeException {

    public ServiceDownException(String message){
        super(message);
    }
}
