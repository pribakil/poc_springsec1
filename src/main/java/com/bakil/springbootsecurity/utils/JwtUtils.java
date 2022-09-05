package com.bakil.springbootsecurity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final String SECRET_KEY = "security_key"; // create strong key and save it on another server

    public static String generateToken( UserDetails userDetails ) {
        Map<String, Object> claims = new HashMap<>();
        return createToken( claims, userDetails.getUsername() );
    }

    public Boolean validateToken( String token, UserDetails userDetails ){
        String userName = extractUsername( token );
        return userName.equals( userDetails.getUsername() ) && !isTokenExpired( token );
    }

    private static String createToken( Map<String, Object> claims, String subject ) {
        Date now = new Date( System.currentTimeMillis() );
        Date until = new Date( System.currentTimeMillis() + 1000 * 60 * 60 * 10 ); // 10 hours
        return Jwts.builder().setClaims( claims ).setSubject( subject ).setIssuedAt( now ).setExpiration( until )
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before( new Date() );
    }

    private Date extractExpiration(String token) {
        return extractClaim( token, Claims::getExpiration );
    }

    public String extractUsername(String token) {
        return extractClaim( token, Claims::getSubject );
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims( token );
        return claimsResolver.apply( claims );
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey( SECRET_KEY ).parseClaimsJws( token ).getBody();
    }
}
