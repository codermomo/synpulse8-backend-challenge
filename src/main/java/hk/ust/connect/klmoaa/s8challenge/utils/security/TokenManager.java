package hk.ust.connect.klmoaa.s8challenge.utils.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenManager implements Serializable {

    private Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private static final long serialVersionUID = 7008375124389347049L;
    public static final long TOKEN_VALIDITY = 10 * 60 * 60;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512 ,jwtSecret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        Boolean isExpired = claims.getExpiration().before(new Date());
        return !isExpired && userDetails.getUsername().equals(getClientIdFromToken(token));
    }

    public String getClientIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            logger.error(String.format("Encountered exception: %s", e));
        }
        return null;
    }
}
