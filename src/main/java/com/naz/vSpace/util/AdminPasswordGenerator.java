package com.naz.vSpace.util;

import org.apache.commons.lang3.RandomStringUtils;

public class AdminPasswordGenerator {
    private static final int PASSWORD_LENGTH = 12;

    public static String generatePassword(){
        String password = RandomStringUtils.random(PASSWORD_LENGTH, 0, 0,
                true, true, null);
        return password;
    }
}
