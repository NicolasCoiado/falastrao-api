package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping ("/admin")
public class AdminController {

    private AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
     }

    @PutMapping("/lock-account")
    public ResponseEntity<Map<String, Object>> lockAccount (@RequestBody Long userId) {
        service.lockAccount(userId);
        return ResponseEntity.ok(Map.of("message", "Account locked successfully"));
    }

    @PutMapping("/unlock-account")
    public ResponseEntity<Map<String, Object>> unlockAccount (@RequestBody Long userId) {
        service.unlockAccount(userId);
        return ResponseEntity.ok(Map.of("message", "Account unlocked successfully"));
    }

    @PutMapping("/promote-user")
    public ResponseEntity<Map<String, Object>> promoteUser (@RequestBody Long userId) {
        service.promoteUser(userId );
        return ResponseEntity.ok(Map.of("message", "User promoted to admin successfully"));
    }

    @PutMapping("/demote-user")
    public ResponseEntity<Map<String, Object>> demoteUser (@RequestBody Long userId) {
        service.demoteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User demoted to user successfully"));
    }

}
