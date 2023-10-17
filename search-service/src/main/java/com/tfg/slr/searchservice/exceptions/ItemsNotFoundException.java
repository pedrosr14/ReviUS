package com.tfg.slr.searchservice.exceptions;

public class ItemsNotFoundException extends IllegalArgumentException{

    public ItemsNotFoundException(String message){
        super(message);
    }
}
