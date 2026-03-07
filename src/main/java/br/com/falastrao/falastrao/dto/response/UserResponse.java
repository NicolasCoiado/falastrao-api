package br.com.falastrao.falastrao.dto.response;

public record UserResponse(
    Long id,
    String username,
    String email,
    String createdAt,
    boolean accountVerified,
    boolean accountNonLocked,
    String role
) {}
