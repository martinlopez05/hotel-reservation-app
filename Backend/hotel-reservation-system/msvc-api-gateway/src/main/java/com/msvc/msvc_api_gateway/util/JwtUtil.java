package com.msvc.msvc_api_gateway.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(userGenerator)
                    .build();

            return verifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Invalid or expired token");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public String extractRole(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("role").asString();
    }

}
