package com.SecureLibrarySystem.webapp.hashing;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {

    // Holder class for hash + salt
    public static class HashResult {
        private final String hash;
        private final String salt;

        public HashResult(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash() {
            return hash;
        }

        public String getSalt() {
            return salt;
        }
    }

    // Generate salt
    private static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash password + salt
    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Public method used during registration
    public static HashResult hash(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return new HashResult(hash, salt);
    }

    // Verify password during login
    public static boolean verifyPassword(String enteredPassword, String storedHash, String storedSalt) {
        String newHash = hashPassword(enteredPassword, storedSalt);
        return newHash.equals(storedHash);
    }
}
