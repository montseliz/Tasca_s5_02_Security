package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

import static cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.security.SecurityConstants.JWT_EXPIRATION;
import static cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.security.SecurityConstants.JWT_SECRET;

@Component
public class JwtGenerator {

    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(getSignWith(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignWith() {
        byte[] secretBytes = Decoders.BASE64.decode(JWT_SECRET);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSignWith()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSignWith()).build().parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();
            if (expirationDate.before(new Date())) {
                throw new AuthenticationCredentialsNotFoundException("Your token has expired");
            }
            return true;
        } catch (AuthenticationCredentialsNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("Your token is incorrect", e.getCause());
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignWith()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
