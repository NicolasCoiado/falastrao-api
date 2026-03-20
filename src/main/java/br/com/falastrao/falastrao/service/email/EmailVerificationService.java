package br.com.falastrao.falastrao.service.email;

import br.com.falastrao.falastrao.exception.AccountNotVerifiedException;
import br.com.falastrao.falastrao.exception.ExpiredTokenException;
import br.com.falastrao.falastrao.exception.InvalidTokenException;
import br.com.falastrao.falastrao.exception.UserNotFoundException;
import br.com.falastrao.falastrao.model.EmailVerificationToken;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.EmailVerificationTokenRepository;
import br.com.falastrao.falastrao.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository, UserRepository userRepository, EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void createAndSendToken(User user) {

        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.flush();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(generateToken());
        verificationToken.setUser(user);
        verificationToken.setCreatedAt(OffsetDateTime.now());
        verificationToken.setExpiresAt(OffsetDateTime.now().plusHours(24));

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
    }

    @Transactional
    public void verifyToken(String token) {

        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (verificationToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ExpiredTokenException("Token has expired");
        }

        User user = verificationToken.getUser();

        user.setAccountVerified(true);

        userRepository.save(user);

        tokenRepository.deleteByUserId(user.getId());

    }

    @Transactional
    public void resendToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isAccountVerified()) {
            throw new AccountNotVerifiedException("Account is already verified");
        }

        createAndSendToken(user);
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