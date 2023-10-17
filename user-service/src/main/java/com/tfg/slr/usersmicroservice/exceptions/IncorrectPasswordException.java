package com.tfg.slr.usersmicroservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IncorrectPasswordException extends IllegalArgumentException {

    public IncorrectPasswordException(String message){

        super(message);
    }
}
