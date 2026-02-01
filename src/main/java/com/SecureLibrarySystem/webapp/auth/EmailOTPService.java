package com.SecureLibrarySystem.webapp.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailOTPService {

    private Map<String, String> otpStore = new HashMap<>();

    @Autowired
    private JavaMailSender mailSender;

    public void sendOTP(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Secure Library System - OTP");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);
        System.out.println("OTP sent to email: " + otp);
    }

    public boolean verifyOTP(String email, String otp) {
        return otp.equals(otpStore.get(email));
    }
}
