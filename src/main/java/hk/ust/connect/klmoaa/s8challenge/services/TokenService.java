package hk.ust.connect.klmoaa.s8challenge.services;

import hk.ust.connect.klmoaa.s8challenge.utils.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private Logger logger = LoggerFactory.getLogger(TokenService.class);
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private JWTUserDetailsService jwtUserDetailsService;

    public String getToken(String clientId) {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(clientId);

        String token = tokenManager.generateToken(userDetails);

        logger.info(String.format("Generated token %s for client %s", token, clientId));

        return token;
    }

    public String getClientIdFromToken(String token) {
        String clientId = tokenManager.getClientIdFromToken(token);

        logger.info(String.format("Extracted Client ID %s from token %s", clientId, token));

        return clientId;
    }
}
