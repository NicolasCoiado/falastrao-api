package br.com.falastrao.falastrao.security.context;

import br.com.falastrao.falastrao.security.jwt.JwtUserData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    public JwtUserData getAuthenticatedUserData() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof JwtUserData jwtUser)) {
            throw new RuntimeException("User not authenticated");
        }

        return jwtUser;
    }
}