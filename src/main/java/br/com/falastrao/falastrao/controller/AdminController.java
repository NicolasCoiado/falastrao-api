package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.service.user.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

}
