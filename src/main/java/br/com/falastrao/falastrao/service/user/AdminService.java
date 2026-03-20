package br.com.falastrao.falastrao.service.user;

import br.com.falastrao.falastrao.exception.UserNotFoundException;
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

    public boolean lockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isAccountNonLocked()) return false;

        user.setAccountNonLocked(false);
        userRepository.save(user);
        return true;
    }

    public boolean unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isAccountNonLocked()) return false;

        user.setAccountNonLocked(true);
        userRepository.save(user);
        return true;
    }

    public boolean promoteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() == UserRoles.ADMIN) return false;

        user.setRole(UserRoles.ADMIN);
        userRepository.save(user);
        return true;
    }

    public boolean demoteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() == UserRoles.USER) return false;

        user.setRole(UserRoles.USER);
        userRepository.save(user);
        return true;
    }

    // TODO: Soft delete review
}
