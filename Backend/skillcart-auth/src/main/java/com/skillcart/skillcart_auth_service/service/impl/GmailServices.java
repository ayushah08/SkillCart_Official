package com.skillcart.skillcart_auth_service.service.impl;

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

    private final BrevoEmailService brevoEmailService;
    private final SecureRandom secureRandom = new SecureRandom();

    private final ConcurrentMap<String, OtpDetails> otpStore =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Instant> verifiedEmails =
            new ConcurrentHashMap<>();

    public GmailServices(BrevoEmailService brevoEmailService) {
        this.brevoEmailService = brevoEmailService;
    }

    private record OtpDetails(String otp, Instant createdAt) {}

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email must not be empty"
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    public void sendOtpToAdmin(String email) {
        String normalizedEmail = normalizeEmail(email);

        // A new OTP request invalidates previous verification.
        verifiedEmails.remove(normalizedEmail);

        String otp = String.format(
                Locale.ROOT,
                "%05d",
                secureRandom.nextInt(100000)
        );

        // Send first. Only store the OTP if Brevo accepts the request.
        brevoEmailService.sendOtpEmail(normalizedEmail, otp);

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

        if (elapsedSeconds < 0
                || elapsedSeconds >= OTP_VALIDITY_SECONDS) {
            otpStore.remove(normalizedEmail, details);
            return false;
        }

        if (!details.otp().equals(otp)) {
            return false;
        }

        // An OTP can be used only once.
        if (!otpStore.remove(normalizedEmail, details)) {
            return false;
        }

        verifiedEmails.put(
                normalizedEmail,
                Instant.now().plusSeconds(
                        VERIFICATION_VALIDITY_SECONDS
                )
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

        // Registration consumes the verification exactly once.
        return verifiedEmails.remove(normalizedEmail, expiresAt);
    }
}