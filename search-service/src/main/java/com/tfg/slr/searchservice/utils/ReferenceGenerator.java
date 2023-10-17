package com.tfg.slr.searchservice.utils;

import net.bytebuddy.utility.RandomString;

import java.util.Random;

public class ReferenceGenerator {

    public static String generateRef(){
        //Generate 3 random letters
        String letters = RandomString.make(3);
        // Generate 4 random numbers
        Integer random = new Random().nextInt(99999);
        String numbers = random.toString();

        if(numbers.length()<5) {
            for (int i = 0; i < numbers.length(); i++) {
                numbers=0+numbers;
            }
        }
        return letters +"-"+ numbers;
    }
}
