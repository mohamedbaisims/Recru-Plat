package com.example.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.example.backend.user.User;
@Service
public class JwtService {
    @Value("${spring.application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${spring.application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${spring.application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims =extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("firstname", user.getFirstname());
        extraClaims.put("lastname", user.getLastname());

        return buildToken(extraClaims, user, jwtExpiration);
    }
    public String generateToken(Map<String,Object> extraClaims,
                                UserDetails userDetails){
        return buildToken(extraClaims,userDetails,jwtExpiration);

    }
    public String generateRefreshToken(
                                UserDetails userDetails){
        return buildToken(new HashMap<>(),userDetails,refreshExpiration);

    }

    private String buildToken(Map<String,Object> extraClaims,
                              UserDetails userDetails,
                              long expiration){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSeignIngKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenVaild(String token,UserDetails userDetails){
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername()))&& !isTokenExpired(token);

    }
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    private Claims extractAllClaims( String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSeignIngKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSeignIngKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
