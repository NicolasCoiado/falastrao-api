package br.com.falastrao.falastrao.service.auth;

import br.com.falastrao.falastrao.exception.ExpiredTokenException;
import br.com.falastrao.falastrao.exception.InvalidTokenException;
import br.com.falastrao.falastrao.exception.UserNotFoundException;
import br.com.falastrao.falastrao.model.PasswordResetToken;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.PasswordResetTokenRepository;
import br.com.falastrao.falastrao.repository.UserRepository;
import br.com.falastrao.falastrao.service.email.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void requestReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.flush();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(generateToken());
        resetToken.setUser(user);
        resetToken.setCreatedAt(OffsetDateTime.now());
        resetToken.setExpiresAt(OffsetDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (resetToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ExpiredTokenException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.deleteByUserId(user.getId());
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}