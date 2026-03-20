package br.com.falastrao.falastrao.service.user;

import br.com.falastrao.falastrao.dto.request.UserRequest;
import br.com.falastrao.falastrao.dto.response.UserResponse;
import br.com.falastrao.falastrao.mapper.UserMapper;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.UserRepository;
import br.com.falastrao.falastrao.service.email.EmailVerificationService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final EmailVerificationService verificationService;

    public UserService(UserRepository repository, UserMapper mapper, PasswordEncoder encoder, EmailVerificationService verificationService) {
        this.repository = repository;
        this.mapper = mapper;
        this.encoder = encoder;
        this.verificationService = verificationService;
    }

    public User getActiveVerifiedUserById(Long id) {

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isAccountVerified()) {
            throw new RuntimeException("User account not verified");
        }

        if (!user.isAccountNonLocked()) {
            throw new RuntimeException("User account locked");
        }

        return user;
    } // TODO: Refactor exception handling

    @Transactional
    public UserResponse save(UserRequest userRequest) {

        User user = mapper.toEntity(userRequest);
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = repository.save(user);

        verificationService.createAndSendToken(savedUser);

        return mapper.toResponse(savedUser);
    }

}
