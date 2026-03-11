package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.LoginRequest;
import br.com.falastrao.falastrao.dto.request.ResendVerificationRequest;
import br.com.falastrao.falastrao.service.AuthService;
import br.com.falastrao.falastrao.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final EmailVerificationService verificationService;

    public AuthController(AuthService service, EmailVerificationService verificationService) {
        this.service = service;
        this.verificationService = verificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("Message", "Login successful!");
        responseMap.put("JWT", service.login(request));

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {

        verificationService.verifyToken(token);

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ResendVerificationRequest request) {

        verificationService.resendToken(request.email());

        return ResponseEntity.ok("Verification email sent");
    }
}