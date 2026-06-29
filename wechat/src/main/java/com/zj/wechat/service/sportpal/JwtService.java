package com.zj.wechat.service.sportpal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private static final long EXPIRE_MS = 7 * 24 * 60 * 60 * 1000L;

    @Value("${cfg.sportpal.jwtSecret:sportpal-default-secret-key-for-dev-only-2024!}")
    private String secretStr;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = new byte[32];
        byte[] src = secretStr.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, keyBytes, 0, Math.min(src.length, keyBytes.length));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String openId) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + EXPIRE_MS);
        return Jwts.builder()
                .claim("userId", userId)
                .claim("openId", openId)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object val = claims.get("userId");
        return val == null ? null : ((Number) val).longValue();
    }
}
