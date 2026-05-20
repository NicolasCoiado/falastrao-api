package br.com.falastrao.falastrao.service.review;

import br.com.falastrao.falastrao.dto.response.FavoriteToggleResponse;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.exception.ReviewNotFoundException;
import br.com.falastrao.falastrao.mapper.ReviewMapper;
import br.com.falastrao.falastrao.model.FavoriteReview;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.FavoriteReviewRepository;
import br.com.falastrao.falastrao.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FavoriteReviewService {

    private final FavoriteReviewRepository favoriteReviewRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public FavoriteReviewService(
            FavoriteReviewRepository favoriteReviewRepository,
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper
    ) {
        this.favoriteReviewRepository = favoriteReviewRepository;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public FavoriteToggleResponse toggleFavorite(User user, UUID reviewExternalId) {
        Review review = reviewRepository.findByExternalId(reviewExternalId)
                .filter(r -> !r.isPrivateReview() || r.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (favoriteReviewRepository.existsByUserAndReview(user, review)) {
            favoriteReviewRepository.deleteByUserAndReview(user, review);
            return new FavoriteToggleResponse(false);
        }

        FavoriteReview favorite = new FavoriteReview();
        favorite.setUser(user);
        favorite.setReview(review);
        favoriteReviewRepository.save(favorite);
        return new FavoriteToggleResponse(true);
    }

    public PageResponse<ReviewResponse> getFavorites(User user, int page, int size) {
        return PageResponse.from(
                favoriteReviewRepository.findFavoriteReviewsByUser(user, PageRequest.of(page, size))
                        .map(reviewMapper::toResponse)
        );
    }
}
