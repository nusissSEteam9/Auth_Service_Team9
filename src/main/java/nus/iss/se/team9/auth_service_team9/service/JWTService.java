package nus.iss.se.team9.auth_service_team9.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateJWT(String username,String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)  // Adding role claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token valid for 10 hours
                .signWith(SignatureAlgorithm.HS256, jwtSecret) // Use the same secret key as in filter
                .compact();
    }
}
