package com.wiilisten.utils;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.wiilisten.request.UserDetail;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class TokenUtil implements Serializable {
    
    /**
     *
     */
    private static final long serialVersionUID = 680674901313380631L;
    
    @Value("${jwt.secret}")
    private String secret;

//    @Value("${jwt.expiry.time}")
    private Long expiration = 86399362L;

    static final String CLAIM_KEY_USERNAME = "sub";
    
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    public String generateToken() {

        final Map<String, Object> claims = new HashMap<>();
        final UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        final String username = userDetail.getEmail();
//        final String deviceUUID = userDetail.getUser().getDeviceUUID();
        if (username != null) {
            claims.put(CLAIM_KEY_USERNAME, username);
            return generateToken(claims, username);
        }
        return null;
    }
    
    public String generateToken(final Map<String, Object> claims, String username) {

    	return Jwts.builder()
    		.signWith(getKey(), SignatureAlgorithm.HS256)
    		.setClaims(claims)
    		.setSubject(username)
    		.setExpiration(generateExpirationDate())
//    		.setAudience(username)
    		.compact();
    	
    }

    public String getUsernameFromToken(final String token) {
        String username;
        try {
        	final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (final Exception e) {
            username = null;
            
        }
        return username;
    }

    private Key getKey() {
    	return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
    
    public Claims getClaimsFromToken(String token) {

        Claims claims;
        try {
            claims = Jwts.parserBuilder()
            			.setSigningKey(getKey())
            			.build()
            			.parseClaimsJws(token)
            			.getBody();
        } catch (final Exception e) {
            claims = null;
        }
        return claims;
    }

    private Boolean isTokenExpired(final String token) {

        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(final String token) {

        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (final Exception e) {
            expiration = null;
        }
        return expiration;
    }
    
 
}
