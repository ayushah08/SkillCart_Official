package com.skillcart.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;


    // ==========================
    // Signing Key
    // ==========================

    private SecretKey getSignInKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    // ==========================
    // Extract User ID
    // ==========================

    public UUID extractUserId(
            String token
    ) {

        String userId =
                extractClaim(
                        token,
                        claims -> claims.get(
                                "userId",
                                String.class
                        )
                );

        return UUID.fromString(userId);
    }


    // ==========================
    // Extract Username
    // ==========================

    public String extractUsername(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // ==========================
    // Extract Role
    // ==========================

    public String extractRole(
            String token
    ) {

        return extractClaim(
                token,
                claims -> claims.get(
                        "role",
                        String.class
                )
        );
    }


    // ==========================
    // Generic Claim Extractor
    // ==========================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    // ==========================
    // Extract All Claims
    // ==========================

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(
                        getSignInKey()
                )

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}