package com.paulrlutz.passwordgenerator;

import java.util.Random;

public class Utilities {
        public static int rand(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max + 1) + min;
    }
}
