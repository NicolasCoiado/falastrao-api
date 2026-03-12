package br.com.falastrao.falastrao.service;

import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.model.enums.UserRoles;
import br.com.falastrao.falastrao.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void lockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // TODO: Change to a custom exception
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    public void unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // TODO: Change to a custom exception
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    public void promoteUser (Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // TODO: Change to a custom exception
        user.setRole(UserRoles.ADMIN);
        userRepository.save(user);
    }

    public void demoteUser (Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // TODO: Change to a custom exception
        user.setRole(UserRoles.USER);
        userRepository.save(user);
    }
}
