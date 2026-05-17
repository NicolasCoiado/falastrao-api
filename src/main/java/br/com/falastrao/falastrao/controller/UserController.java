package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.UpdateLocaleRequest;
import br.com.falastrao.falastrao.dto.request.UserRequest;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.dto.response.UserResponse;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.annotation.CurrentUser;
import br.com.falastrao.falastrao.service.review.ReviewService;
import br.com.falastrao.falastrao.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService service;
    private final ReviewService reviewService;

    public UserController(UserService service, ReviewService reviewService) {
        this.service = service;
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create (@Valid @RequestBody UserRequest user) {
        UserResponse response = service.save(user);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "User successfully created!");
        responseMap.put("user", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    @PatchMapping("/profile-picture")
    public ResponseEntity<Map<String, Object>> updateProfilePicture(
            @CurrentUser User user,
            @RequestBody @NotBlank String profilePictureUrl) {

        UserResponse response = service.updateProfilePicture(user, profilePictureUrl);

        return ResponseEntity.ok(Map.of(
                "message", "Profile picture updated successfully",
                "user", response
        ));
    }

    @PatchMapping("/locale")
    public ResponseEntity<UserResponse> updateLocale(
            @CurrentUser User user,
            @Valid @RequestBody UpdateLocaleRequest request) {
        return ResponseEntity.ok(service.updateLocale(user, request.locale()));
    }

    @GetMapping("/{externalId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getUserReviews(
            @PathVariable UUID externalId,
            @CurrentUser User requester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(reviewService.getUserReviews(externalId, requester, page, size, sortBy, direction));
    }

}
