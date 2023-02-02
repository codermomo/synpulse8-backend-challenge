package hk.ust.connect.klmoaa.s8challenge.services;

import hk.ust.connect.klmoaa.s8challenge.constants.TransactionConstraints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(JWTUserDetailsService.class);

    // store the credentials of all the users with access to the API endpoint in this tech challenge (using token)
    // in reality, we may use a database to query the user details
    private final HashMap<String, String> credentials;

    {
        credentials = new HashMap<>();
        for (var clientId: TransactionConstraints.ClientIdSupported) {
            credentials.put(clientId, "");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (credentials.get(username) != null) {
            logger.info(String.format("Client %s is found in database", credentials.get(username)));
            return new User(username, credentials.get(username), new ArrayList<>());
        }

        logger.error(String.format("Client %s is not found in database", credentials.get(username)));
        throw new UsernameNotFoundException("User not found with username " + username);
    }
}
