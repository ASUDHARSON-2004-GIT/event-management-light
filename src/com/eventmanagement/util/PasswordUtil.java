package com.eventmanagement.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// This class turns a plain password into a hashed value.
// We never keep the plain password in memory once it is
// hashed, this way a plain password is never stored anywhere.
public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainPassword.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte singleByte : hashBytes) {
                String hex = Integer.toHexString(0xff & singleByte);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in a standard Java installation
            // so this branch should never really run, but we handle it anyway.
            throw new RuntimeException("Unable to hash password", e);
        }
    }

    public static boolean matches(String plainPassword, String hashedPassword) {
        String hashOfInput = hashPassword(plainPassword);
        return hashOfInput.equals(hashedPassword);
    }
}
