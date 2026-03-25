package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.ReviewRequest;
import br.com.falastrao.falastrao.dto.request.UpdateReviewRequest;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.annotation.CurrentUser;
import br.com.falastrao.falastrao.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview (@CurrentUser User user, @RequestBody ReviewRequest reviewRequest) {

        ReviewResponse reviewResponse = service.createReview(user, reviewRequest);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Review successfully created!");
        responseMap.put("Review", reviewResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReviewResponse>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getReviews(page, size));
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable UUID externalId) {
        return ResponseEntity.ok(service.getReviewByExternalId(externalId));
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID externalId,
            @CurrentUser User user,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(service.updateReview(externalId, user, request));
    }

    @PatchMapping("/{externalId}")
    public ResponseEntity<ReviewResponse> partialUpdateReview(
            @PathVariable UUID externalId,
            @CurrentUser User user,
            @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(service.partialUpdateReview(externalId, user, request));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        service.deleteReview(externalId);
        return ResponseEntity.noContent().build();
    }

}