package app.rest.security;

import app.models.UserTeam;
import app.models.user.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Component
public class JWTokenUtil {

    @Value("${jwt.issuer}")
    private String ISSUER;

    @Value("${jwt.secret-key}")
    private String KEY;

    @Value("${jwt.expiration-seconds}")
    private int EXPIRATION;

    @Value("${jwt.refresh-expiration-seconds}")
    private int REFRESH_EXPIRATION;

    // Claims
    public static final String JWT_ROLE = "role";
    public static final String JWT_TEAM = "team";
    public static final String JWT_UTID = "utid";

    /**
     * Encode a UserTeam object into a token.
     *
     * @param userTeam
     * @return
     */
    public String encode(UserTeam userTeam) {
        return Jwts.builder()
                .claim(Claims.SUBJECT, userTeam.getUser().getId())
                .claim(JWT_ROLE, userTeam.getRole().getId())
                .claim(JWT_TEAM, userTeam.getTeam().getId())
                .claim(Claims.ID, userTeam.getUser().getId())
                .claim(JWT_UTID, userTeam.getId())
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000L))
                .signWith(getKey(KEY), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Encode a User object into a token.
     *
     * @param user
     * @return
     */
    public String encode(User user, long userTeamId) {
        return Jwts.builder()
                .claim(Claims.SUBJECT, user.getId())
                .claim(JWT_ROLE, 0)
                .claim(JWT_TEAM, 0)
                .claim(Claims.ID, user.getId())
                .claim(JWT_UTID, userTeamId)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000L))
                .signWith(getKey(KEY), SignatureAlgorithm.HS512)
                .compact();
    }

    public String refresh(JWTokenInfo token, UserTeam userTeam) {
        if (!isRenewable(token)) throw new notRenewable("The token is not renewable");

        return this.encode(userTeam);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    private class notRenewable extends RuntimeException {
        public notRenewable(String message) {
            super(message);
        }
    }

    private boolean isRenewable(JWTokenInfo token) {
        if (token.getExpirationDate().compareTo(new Date()) > 0) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(token.getIssuedAt());
        cal.add(Calendar.SECOND, REFRESH_EXPIRATION);

        return cal.getTime().compareTo(new Date()) > 0;
    }

    /**
     * Convert a token to a JWToken object.
     *
     * @param token
     * @return
     */
    private JWTokenInfo getInfo(String token, int exp) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey(KEY))
                    .setAllowedClockSkewSeconds(exp)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new JWTokenInfo(
                    claims.get(Claims.SUBJECT, Integer.class),
                    claims.get(JWT_ROLE, Integer.class),
                    claims.get(JWT_TEAM, Integer.class),
                    claims.get(Claims.ISSUED_AT, Date.class),
                    claims.get(Claims.EXPIRATION, Date.class),
                    claims.get(Claims.ID, Integer.class),
                    claims.get(JWT_UTID, Integer.class));

        } catch (ExpiredJwtException e) {
            throw new expired("Token is expired");
        } catch (JwtException e) {
            return null;
        }
    }

    public static class expired extends RuntimeException{
        public expired(String message){
            super(message);
        }
    }

    public JWTokenInfo getNonExpiredInfo(String tokenString) {
        return this.getInfo(tokenString, REFRESH_EXPIRATION);
    }

    public JWTokenInfo getInfo(String tokenString) {
        return this.getInfo(tokenString, 0);
    }

    /**
     * Get security key.
     *
     * @param keyIn
     * @return
     */
    public static Key getKey(String keyIn) {
        byte[] hmacKey = keyIn.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(hmacKey, SignatureAlgorithm.HS512.getJcaName());
    }
}
