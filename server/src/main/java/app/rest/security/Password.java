package app.rest.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class Password {

    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(13, new SecureRandom());

    /**
     * Hash user password.
     * @param password
     * @return the hash of the input.
     */
    public String passwordHash(String password) {
        return this.bcrypt.encode(password);
    }

    /**
     * Verify user password.
     * @param rawIn
     * @param hashIn
     * @return verification result.
     */
    public Boolean passwordVerify(String rawIn, String hashIn) {
        return this.bcrypt.matches(rawIn, hashIn);
    }
}
