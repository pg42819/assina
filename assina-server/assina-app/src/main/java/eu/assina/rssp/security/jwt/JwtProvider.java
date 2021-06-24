package eu.assina.rssp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtProvider {

    private static final String TYPE_CLAIM_NAME = "type";
    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private JwtProviderConfig jwtConfig;

    public JwtProvider(JwtProviderConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public JwtToken createToken(String subject) {
        // Use java8 time library for better expiry handling
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(jwtConfig.getLifetimeMinutes(), ChronoUnit.MINUTES);

        log.debug("Issued JWT token at: {}, expires at: {}", issuedAt, expiration);
        log.info("Issiued JWT token at: {}", expiration);

        JwtToken token = new JwtToken(jwtConfig.getType(), subject);

        String rawToken = Jwts.builder()
                       .setSubject(subject)
                       .setIssuedAt(Date.from(issuedAt))
                       .setExpiration(Date.from(expiration))
                       .claim(TYPE_CLAIM_NAME, jwtConfig.getType())
                       .signWith(SignatureAlgorithm.HS512, jwtConfig.getTokenSecret())
                       .compact();
        token.setRawToken(rawToken);
        return token;
    }

    public JwtToken parseToken(String rawToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getTokenSecret())
                .parseClaimsJws(rawToken)
                .getBody();

        JwtToken token = new JwtToken(claims.getSubject(),
                claims.get(TYPE_CLAIM_NAME).toString());
        token.setRawToken(rawToken);
        return token;
    }

    public JwtToken validateToken(String rawToken) {
        try {
            JwtToken token = parseToken(rawToken);
            if (!token.getType().equals(jwtConfig.getType())) {
                return JwtToken.invalidToken(
                        String.format("Unexpected tokentype: should be of type %s",
                                jwtConfig.getType()));
            }
            else {
                return token;
            }
        } catch (SignatureException ex) {
            return JwtToken.invalidToken("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            return JwtToken.invalidToken("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            return JwtToken.expiredToken();
        } catch (UnsupportedJwtException ex) {
            return JwtToken.invalidToken("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            return JwtToken.invalidToken("JWT claims string is empty.");
        }
    }

}
