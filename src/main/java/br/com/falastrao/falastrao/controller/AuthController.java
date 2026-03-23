package br.com.falastrao.falastrao.controller;

import br.com.falastrao.falastrao.dto.request.LoginRequest;
import br.com.falastrao.falastrao.dto.request.NewPasswordRequest;
import br.com.falastrao.falastrao.dto.request.PasswordResetRequest;
import br.com.falastrao.falastrao.dto.request.ResendVerificationRequest;
import br.com.falastrao.falastrao.service.auth.AuthService;
import br.com.falastrao.falastrao.service.auth.PasswordResetService;
import br.com.falastrao.falastrao.service.email.EmailVerificationService;
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
    private final PasswordResetService passwordResetService;


    public AuthController(AuthService service, EmailVerificationService verificationService, PasswordResetService passwordResetService) {
        this.service = service;
        this.verificationService = verificationService;
        this.passwordResetService = passwordResetService;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.requestReset(request.email());
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}