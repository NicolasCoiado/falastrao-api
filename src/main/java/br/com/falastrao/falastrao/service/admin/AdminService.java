package br.com.falastrao.falastrao.service.admin;

import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.exception.ReviewNotFoundException;
import br.com.falastrao.falastrao.exception.UserNotFoundException;
import br.com.falastrao.falastrao.mapper.ReviewMapper;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.model.enums.UserRoles;
import br.com.falastrao.falastrao.repository.ReviewRepository;
import br.com.falastrao.falastrao.repository.UserRepository;
import br.com.falastrao.falastrao.service.notification.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final NotificationService notificationService;

    public AdminService(UserRepository userRepository, ReviewRepository reviewRepository, ReviewMapper reviewMapper, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.notificationService = notificationService;
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

    @Transactional
    public ReviewResponse toggleReviewPrivacy(UUID externalId, String reason) {
        Review review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        boolean isBeingPrivated = !review.isPrivateReview();
        review.setPrivateReview(isBeingPrivated);

        if (isBeingPrivated) {
            notificationService.sendSystemNotification(
                    review.getUser(),
                    "notification.review.privated",
                    new Object[]{review.getTitle(), reason}
            );
        }

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

}
