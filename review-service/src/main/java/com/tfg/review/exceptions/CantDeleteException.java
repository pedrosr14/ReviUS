package com.tfg.review.exceptions;

public class CantDeleteException extends RuntimeException{

    public CantDeleteException(String message) {
        super(message);
    }
}
