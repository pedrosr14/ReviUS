package com.tfg.slr.usersmicroservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass //Makes all methods static
public class UserServiceUtils {

    public boolean checkPasswordConstraints(String password){
        if (password.length() < 8 || password.length() > 32 || !password.matches("^(?=.*[ña-z])(?=.*[ÑA-Z])(?=.*\\d)[ña-zÑA-Z\\d]{8,32}$")) {
            return false;
        }else{
            return true;
        }
    }
}
