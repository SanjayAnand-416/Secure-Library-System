package com.SecureLibrarySystem.webapp.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OTPService {

    private Map<String, String> otpStore = new HashMap<>();

    public void generateOTP(String username, String email) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(username, otp);

        // LAB SIMULATION
        System.out.println("OTP for " + username +
                " (mobile: " + email + ") is: " + otp);
    }

    public boolean verifyOTP(String username, String otp) {
        return otp.equals(otpStore.get(username));
    }
}
