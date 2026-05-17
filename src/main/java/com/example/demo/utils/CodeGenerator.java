package com.example.demo.utils;

import java.security.SecureRandom;

public class CodeGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateCode(int length) {
        if (length < 4 || length > 10) {
            throw new IllegalArgumentException("Code length must be between 4 and 10");
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10)); // цифры от 0 до 9
        }
        return code.toString();
    }
}
