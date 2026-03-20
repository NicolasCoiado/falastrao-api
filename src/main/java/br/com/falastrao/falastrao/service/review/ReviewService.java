package br.com.falastrao.falastrao.service.review;

import br.com.falastrao.falastrao.dto.request.ReviewRequest;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.exception.ReviewNotFoundException;
import br.com.falastrao.falastrao.mapper.ReviewMapper;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.ReviewRepository;
import br.com.falastrao.falastrao.service.topic.TopicService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TopicService topicService;
    private final ReviewMapper reviewMapper;

    public ReviewService(
            ReviewRepository reviewRepository,
            TopicService topicService,
            ReviewMapper reviewMapper
    ) {
        this.reviewRepository = reviewRepository;
        this.topicService = topicService;
        this.reviewMapper = reviewMapper;
    }

    @CacheEvict(value = "rankedTopics", allEntries = true)
    public ReviewResponse createReview(User user, ReviewRequest reviewRequest) {

        Review review = reviewMapper.toEntity(reviewRequest);

        review.setUser(user);

        Set<Topic> topics =
                topicService.verifyAndCreateTopics(reviewRequest.topics());

        review.setTopics(topics);

        review = reviewRepository.save(review);

        return reviewMapper.toResponse(review);
    }

    public PageResponse<ReviewResponse> getReviews(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<ReviewResponse> result = reviewRepository.findAll(pageable)
                .map(reviewMapper::toResponse);
        return PageResponse.from(result);
    }

    public ReviewResponse getReviewByExternalId(UUID externalId) {
        Review review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return reviewMapper.toResponse(review);
    }

}