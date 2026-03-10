package br.com.falastrao.falastrao.dto.response;

import java.time.OffsetDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    OffsetDateTime createdAt,
    boolean accountVerified,
    boolean accountNonLocked,
    String role
) {}
