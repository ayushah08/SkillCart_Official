package com.skillcart.skillcart_auth_service.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class GmailServices {

    private static final long OTP_VALIDITY_SECONDS = 180;
    private static final long VERIFICATION_VALIDITY_SECONDS = 600;

    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    private final ConcurrentMap<String, OtpDetails> otpStore =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Instant> verifiedEmails =
            new ConcurrentHashMap<>();

    public GmailServices(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private record OtpDetails(String otp, Instant createdAt) {}

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    public void sendOtpToAdmin(String email) {
        String normalizedEmail = normalizeEmail(email);

        verifiedEmails.remove(normalizedEmail);

        String otp = String.format(
                Locale.ROOT,
                "%05d",
                secureRandom.nextInt(100000)
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("skillcart.team.co@gmail.com");
        message.setTo(normalizedEmail);
        message.setSubject("SkillCart - Email Verification OTP");
        message.setText(
                "Welcome to SkillCart!\n\n"
                        + "Your email verification OTP is: " + otp + "\n\n"
                        + "This OTP expires in 3 minutes.\n"
                        + "Do not share this OTP with anyone.\n\n"
                        + "If you did not request this OTP, please ignore this email."
        );

        mailSender.send(message);

        otpStore.put(
                normalizedEmail,
                new OtpDetails(otp, Instant.now())
        );
    }

    public boolean validateOtp(String email, String otp) {
        if (email == null || email.isBlank()
                || otp == null
                || !otp.matches("\\d{5}")) {
            return false;
        }

        String normalizedEmail = normalizeEmail(email);
        OtpDetails details = otpStore.get(normalizedEmail);

        if (details == null) {
            return false;
        }

        long elapsedSeconds = Duration.between(
                details.createdAt(),
                Instant.now()
        ).getSeconds();

        if (elapsedSeconds < 0 || elapsedSeconds >= OTP_VALIDITY_SECONDS) {
            otpStore.remove(normalizedEmail, details);
            return false;
        }

        if (!details.otp().equals(otp)) {
            return false;
        }

        if (!otpStore.remove(normalizedEmail, details)) {
            return false;
        }

        verifiedEmails.put(
                normalizedEmail,
                Instant.now().plusSeconds(VERIFICATION_VALIDITY_SECONDS)
        );

        return true;
    }

    public boolean isEmailVerified(String email) {
        String normalizedEmail = normalizeEmail(email);
        Instant expiresAt = verifiedEmails.get(normalizedEmail);

        if (expiresAt == null) {
            return false;
        }

        if (!Instant.now().isBefore(expiresAt)) {
            verifiedEmails.remove(normalizedEmail, expiresAt);
            return false;
        }

        return true;
    }

    public boolean consumeEmailVerification(String email) {
        String normalizedEmail = normalizeEmail(email);
        Instant expiresAt = verifiedEmails.get(normalizedEmail);

        if (expiresAt == null) {
            return false;
        }

        if (!Instant.now().isBefore(expiresAt)) {
            verifiedEmails.remove(normalizedEmail, expiresAt);
            return false;
        }

        return verifiedEmails.remove(normalizedEmail, expiresAt);
    }
}
