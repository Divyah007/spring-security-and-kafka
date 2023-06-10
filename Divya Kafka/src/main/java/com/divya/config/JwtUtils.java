package com.divya.config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.divya.model.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {
  @Value("${divya.app.jwtSecret}")
  private String jwtSecret;

  @Value("${divya.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(User user) {
    // JSON Web Token (JWT) is an standard that defines a
    // way for securely transmitting information

    return Jwts.builder().setSubject(user.getPhone()).setIssuedAt(new Date())
      .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(key(), SignatureAlgorithm.HS256)
      .compact();
  }

  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public static String getJWTToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return String.valueOf(authentication.getCredentials());
  }
}
