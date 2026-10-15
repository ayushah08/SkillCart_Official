
package com.skillcart.skillcart_auth_service.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class BrevoEmailService {

    private final RestClient restClient;
    private final String senderEmail;
    private final String senderName;

    public BrevoEmailService(
            RestClient.Builder restClientBuilder,
            @Value("${brevo.api-url}") String apiUrl,
            @Value("${brevo.api-key}") String apiKey,
            @Value("${brevo.sender-email}") String senderEmail,
            @Value("${brevo.sender-name:SkillCart}") String senderName) {

        this.restClient = restClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendOtpEmail(String recipientEmail, String otp) {

        String subject = "Your SkillCart verification code";

        String textContent = """
                Hello,

                Your SkillCart email verification code is: %s

                This code expires in 3 minutes.
                If you did not request this code, you can ignore this email.

                Team SkillCart
                """.formatted(otp);

        String htmlContent = """
                <!DOCTYPE html>
                <html>
                  <body style="font-family:Arial,sans-serif;color:#222;">
                    <h2>Verify your SkillCart email</h2>
                    <p>Use the verification code below to continue:</p>
                    <p style="font-size:30px;font-weight:bold;letter-spacing:6px;">
                      %s
                    </p>
                    <p>This code expires in <strong>3 minutes</strong>.</p>
                    <p>If you did not request this code, ignore this email.</p>
                    <p>Team SkillCart</p>
                  </body>
                </html>
                """.formatted(otp);

        Map<String, Object> payload = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of("email", recipientEmail)
                ),
                "subject", subject,
                "textContent", textContent,
                "htmlContent", htmlContent
        );

        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {
            // Never log the API key or the OTP.
            throw new IllegalStateException(
                    "Brevo rejected the email request. HTTP status: "
                            + ex.getStatusCode().value(),
                    ex
            );
        } catch (org.springframework.web.client.ResourceAccessException ex) {
            throw new IllegalStateException(
                    "Could not connect to the Brevo email API.",
                    ex
            );
        }
    }
}
