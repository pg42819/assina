package eu.assina.app.csc.services;

import eu.assina.app.common.config.CSCProperties;
import eu.assina.app.common.error.ApiException;
import eu.assina.app.csc.error.CSCInvalidRequest;
import eu.assina.app.security.jwt.JwtProvider;
import eu.assina.app.security.jwt.JwtProviderConfig;
import eu.assina.app.security.jwt.JwtToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CSCSADProvider {

    private static final Logger log = LoggerFactory.getLogger(CSCSADProvider.class);

    private JwtProvider jwtProvider;
    private final long lifetimeSeconds;

    public CSCSADProvider(CSCProperties cscProperties) {
        JwtProviderConfig jwtConfig = cscProperties.getSad();
        jwtProvider = new JwtProvider(jwtConfig);
        lifetimeSeconds = jwtConfig.getLifetimeMinutes() * 60;
    }

    public String createSAD(String credentialId) {
        final JwtToken token = jwtProvider.createToken(credentialId);
        return token.getRawToken();
    }

    public long getLifetimeSeconds() {
        return lifetimeSeconds;
    }

    public void validateSAD(String rawSAD) {
        JwtToken token = jwtProvider.validateToken(rawSAD);
        if (!token.isValid()) {
            log.error("Invalid SAD provided: {}", token.getError());
        }
        if (token.isExpired()) {
            // SAD expired - return the proper CSC error per 11.9 of the spec
            throw new ApiException(CSCInvalidRequest.SADExpired);
        }
        else if (!token.isValid()) {
            throw new ApiException(CSCInvalidRequest.InvalidSAD);
        }
    }
}
