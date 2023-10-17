package com.tfg.slr.usersmicroservice.exceptions;

public class ServiceDownException extends RuntimeException {

    public ServiceDownException(String message){
        super(message);
    }
}
