package br.com.falastrao.falastrao.security.context;

import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.jwt.JwtUserData;
import br.com.falastrao.falastrao.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserFacade {

    private final AuthenticatedUserService authenticatedUserService;
    private final UserService userService;

    public AuthenticatedUserFacade(
            AuthenticatedUserService authenticatedUserService,
            UserService userService
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.userService = userService;
    }

    public User getCurrentUser() {

        JwtUserData jwtUser =
                authenticatedUserService.getAuthenticatedUserData();

        return userService.getActiveVerifiedUserById(jwtUser.userId());
    }

    public Long getCurrentUserId() {

        return authenticatedUserService
                .getAuthenticatedUserData()
                .userId();
    }

    public String getCurrentUserEmail() {

        return authenticatedUserService
                .getAuthenticatedUserData()
                .email();
    }

    public boolean isAdmin() {

        return authenticatedUserService
                .getAuthenticatedUserData()
                .role()
                .name()
                .equals("ADMIN");
    }
}