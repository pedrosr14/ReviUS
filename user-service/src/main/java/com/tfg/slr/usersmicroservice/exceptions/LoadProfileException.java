package com.tfg.slr.usersmicroservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoadProfileException extends RuntimeException{

    public LoadProfileException(String message){
        super(message);
    }
}
