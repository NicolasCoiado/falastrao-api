package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.response.FavoriteToggleResponse;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.annotation.CurrentUser;
import br.com.falastrao.falastrao.service.review.FavoriteReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/favorites")
public class FavoriteReviewController {

    private final FavoriteReviewService service;

    public FavoriteReviewController(FavoriteReviewService service) {
        this.service = service;
    }

    @PostMapping("/{reviewExternalId}")
    public ResponseEntity<FavoriteToggleResponse> toggleFavorite(
            @CurrentUser User user,
            @PathVariable UUID reviewExternalId) {
        return ResponseEntity.ok(service.toggleFavorite(user, reviewExternalId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReviewResponse>> getFavorites(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getFavorites(user, page, size));
    }
}
