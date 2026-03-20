package br.com.falastrao.falastrao.service.auth;

import br.com.falastrao.falastrao.dto.request.LoginRequest;
import br.com.falastrao.falastrao.dto.response.LoginResponse;
import br.com.falastrao.falastrao.exception.InvalidCredentialsException;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.UserRepository;
import br.com.falastrao.falastrao.security.jwt.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePassword =
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    );

            Authentication authentication =
                    authenticationManager.authenticate(usernamePassword);

            User user = (User) authentication.getPrincipal();

            user.setLastLogin(OffsetDateTime.now());
            userRepository.save(user);

            String token = jwtTokenService.generateToken(user);
            return new LoginResponse(token);

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
}