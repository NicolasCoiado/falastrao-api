package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.response.NotificationResponse;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.security.annotation.CurrentUser;
import br.com.falastrao.falastrao.service.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> getNotifications(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.getNotifications(user, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> countUnread(@CurrentUser User user) {
        return ResponseEntity.ok(Map.of("unread", service.countUnread(user)));
    }

    @PatchMapping("/{externalId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID externalId,
            @CurrentUser User user) {
        return ResponseEntity.ok(service.markAsRead(externalId, user));
    }
}