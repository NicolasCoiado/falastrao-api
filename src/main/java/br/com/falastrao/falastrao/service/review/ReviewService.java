package br.com.falastrao.falastrao.service.review;

import br.com.falastrao.falastrao.dto.request.ReviewRequest;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.mapper.ReviewMapper;
import br.com.falastrao.falastrao.model.Review;
import br.com.falastrao.falastrao.model.Topic;
import br.com.falastrao.falastrao.repository.ReviewRepository;
import br.com.falastrao.falastrao.security.context.AuthenticatedUserFacade;
import br.com.falastrao.falastrao.service.topic.TopicService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TopicService topicService;
    private final ReviewMapper reviewMapper;
    private final AuthenticatedUserFacade authenticatedUserFacade;

    public ReviewService(
            ReviewRepository reviewRepository,
            TopicService topicService,
            ReviewMapper reviewMapper,
            AuthenticatedUserFacade authenticatedUserFacade
    ) {
        this.reviewRepository = reviewRepository;
        this.topicService = topicService;
        this.reviewMapper = reviewMapper;
        this.authenticatedUserFacade = authenticatedUserFacade;
    }

    public ReviewResponse createReview(ReviewRequest reviewRequest) {

        Review review = reviewMapper.toEntity(reviewRequest);

        review.setUser(authenticatedUserFacade.getCurrentUser());

        Set<Topic> topics =
                topicService.verifyAndCreateTopics(reviewRequest.topicRequest());

        review.setTopics(topics);

        review = reviewRepository.save(review);

        return reviewMapper.toResponse(review);
    }
}