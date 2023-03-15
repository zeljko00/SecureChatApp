package etf.si.securechat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    public static final long JWT_VALIDITY = 5 * 60 * 60;
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String getUsernameFromToken(String token) {      //retrieve username from jwt
        return getAllClaimsFromToken(token).getSubject();
    }
    public Date getExpirationDateFromToken(String token) {       //retrieve expiration date from jwt
        return getAllClaimsFromToken(token).getExpiration();
    }
    private Claims getAllClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception e) {
            System.out.println("Error while getting claims from jwt - jwt probably changed or signed with different key!");
            e.printStackTrace();
            throw e;
        }
    }
    private Boolean isTokenExpired(String token) {
        boolean expired = getExpirationDateFromToken(token).before(new Date());
        if (expired)
            System.out.println("Token expired!");
        return expired;
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && userDetails.isAccountNonLocked());
    }
}