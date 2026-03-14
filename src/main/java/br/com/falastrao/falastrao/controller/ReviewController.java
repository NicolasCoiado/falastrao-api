package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.ReviewRequest;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.annotation.CurrentUser;
import br.com.falastrao.falastrao.service.review.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        responseMap.put("message", "User successfully created!");
        responseMap.put("user", reviewResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
}