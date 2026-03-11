package br.com.falastrao.falastrao.service;

import br.com.falastrao.falastrao.dto.request.LoginRequest;
import br.com.falastrao.falastrao.dto.response.LoginResponse;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken usernamePassword =
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(usernamePassword);

        User user = (User) authentication.getPrincipal();

        String token = tokenService.generateToken(user);

        return new LoginResponse(token);
    }
}