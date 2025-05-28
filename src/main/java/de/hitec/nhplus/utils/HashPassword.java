package de.hitec.nhplus.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passwordBytes = password.getBytes();
            md.update(passwordBytes);
            byte[] hashBytes = md.digest();
            BigInteger bigInt = new BigInteger(1, hashBytes);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Failed to Hash Password", exception);
        }
    }
}
