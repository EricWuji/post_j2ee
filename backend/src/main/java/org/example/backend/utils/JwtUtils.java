package org.example.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private int expiration;

    @Resource
    StringRedisTemplate template;

    private boolean deleteToken(String uuid, Date expiration) {
        if (this.isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(expiration.getTime() - now.getTime(), 0);
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidToken(String uuid) {
        return template.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

    public boolean invalidToken(String headerToken) {
        String token = this.convertToken(headerToken);
        if (token == null) {
            return false;
        }
        Algorithm algorithm = Algorithm.HMAC256(secret);
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String id = decodedJWT.getId();
            return deleteToken(id, decodedJWT.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String createJwt(UserDetails userDetails, int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date expirationDate = this.expirationDate();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public Date expirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, expiration * 24);
        return calendar.getTime();
    }

    public DecodedJWT resolveJwt(String headerToken) {
        String token = convertToken(headerToken);
//        System.out.println(headerToken);
        if  (token == null) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            if (isInvalidToken(decodedJWT.getId())) return null;
            Date expiration = decodedJWT.getExpiresAt();
            return expiration.before(new Date()) ? null : decodedJWT;
        } catch (JWTVerificationException exception) {
            return null;
        }

    }

    private String convertToken(String token) {
        if (token == null) return null;
        if (!token.startsWith("Bearer ")) return null;
        return  token.substring(7);
    }

    public UserDetails toUserDetails(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return claims.get("id").asInt();
    }
}
