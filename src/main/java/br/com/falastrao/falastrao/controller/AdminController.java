package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.PrivacyModerationRequest;
import br.com.falastrao.falastrao.dto.response.ReviewResponse;
import br.com.falastrao.falastrao.service.admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping ("/admin")
public class AdminController {

    private AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
     }

    @PutMapping("/lock-account")
    public ResponseEntity<Map<String, Object>> lockAccount(@RequestBody Long userId) {
        boolean changed = service.lockAccount(userId);
        String message = changed ? "Account locked successfully" : "Account was already locked";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PutMapping("/unlock-account")
    public ResponseEntity<Map<String, Object>> unlockAccount(@RequestBody Long userId) {
        boolean changed = service.unlockAccount(userId);
        String message = changed ? "Account unlocked successfully" : "Account was already unlocked";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PutMapping("/promote-user")
    public ResponseEntity<Map<String, Object>> promoteUser(@RequestBody Long userId) {
        boolean changed = service.promoteUser(userId);
        String message = changed ? "User promoted to admin successfully" : "User is already an admin";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PutMapping("/demote-user")
    public ResponseEntity<Map<String, Object>> demoteUser(@RequestBody Long userId) {
        boolean changed = service.demoteUser(userId);
        String message = changed ? "User demoted successfully" : "User is already a regular user";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PatchMapping("/reviews/{externalId}/privacy")
    public ResponseEntity<ReviewResponse> toggleReviewPrivacy(
            @PathVariable UUID externalId,
            @Valid @RequestBody PrivacyModerationRequest request) {
        return ResponseEntity.ok(service.toggleReviewPrivacy(externalId, request.reason()));
    }

}
