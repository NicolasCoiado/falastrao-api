package br.com.falastrao.falastrao.service;

import br.com.falastrao.falastrao.dto.request.UserRequest;
import br.com.falastrao.falastrao.dto.response.UserResponse;
import br.com.falastrao.falastrao.mapper.UserMapper;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, UserMapper mapper, PasswordEncoder encoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.encoder = encoder;
    }

    @Transactional
    public UserResponse save(UserRequest userRequest) {
        // TODO: Implementar envio de e-mail.
        User user = mapper.toEntity(userRequest);
        user.setPassword(encoder.encode(user.getPassword()));
        return mapper.toResponse(repository.save(user));
    }

}
