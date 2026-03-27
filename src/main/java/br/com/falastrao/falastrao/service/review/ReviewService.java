package br.com.falastrao.falastrao.service.review;

import br.com.falastrao.falastrao.dto.request.ReviewRequest;
import br.com.falastrao.falastrao.dto.request.UpdateReviewRequest;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.exception.ReviewNotFoundException;
import br.com.falastrao.falastrao.exception.UserWithoutPermissionException;
import br.com.falastrao.falastrao.mapper.ReviewMapper;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.repository.ReviewRepository;
import br.com.falastrao.falastrao.service.topic.TopicService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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

    @Caching(evict = {
            @CacheEvict(value = "rankedTopics", allEntries = true),
            @CacheEvict(value = "topicDetails", allEntries = true)
    })
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
        Page<ReviewResponse> result = reviewRepository.findAllByPrivateReviewFalse(pageable)
                .map(reviewMapper::toResponse);
        return PageResponse.from(result);
    }

    public ReviewResponse getReviewByExternalId(UUID externalId) {
        Review review = reviewRepository.findByExternalIdAndPrivateReviewFalse(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return reviewMapper.toResponse(review);
    }

    @Transactional
    public ReviewResponse togglePrivacy(UUID externalId, User user) {
        Review review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UserWithoutPermissionException("You are not allowed to change this review's privacy");
        }

        review.setPrivateReview(!review.isPrivateReview());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    @CacheEvict(value = "topicDetails", allEntries = true)
    public ReviewResponse updateReview(UUID externalId, User user, ReviewRequest request) {
        Review review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UserWithoutPermissionException("You are not allowed to edit this review");
        }

        review.setTitle(request.title());
        review.setThumbnailUrl(request.thumbnailUrl());
        review.setContent(request.content());
        review.setUpdatedAt(OffsetDateTime.now());

        Set<Topic> topics = topicService.verifyAndCreateTopics(request.topics());
        review.setTopics(topics);

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    @CacheEvict(value = "topicDetails", allEntries = true)
    public ReviewResponse partialUpdateReview(UUID externalId, User user, UpdateReviewRequest request) {
        Review review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UserWithoutPermissionException("You are not allowed to edit this review");
        }

        if (request.title() != null && !request.title().isBlank()) {
            review.setTitle(request.title());
        }

        if (request.thumbnailUrl() != null) {
            review.setThumbnailUrl(request.thumbnailUrl());
        }

        if (request.content() != null && !request.content().isBlank()) {
            review.setContent(request.content());
        }

        if (request.topics() != null && !request.topics().isEmpty()) {
            Set<Topic> topics = topicService.verifyAndCreateTopics(request.topics());
            review.setTopics(topics);
        }

        review.setUpdatedAt(OffsetDateTime.now());

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    @CacheEvict(value = "topicDetails", allEntries = true)
    public void deleteReview(UUID externalId) {
        Review review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        review.getTopics().clear();
        reviewRepository.delete(review);
    }

}