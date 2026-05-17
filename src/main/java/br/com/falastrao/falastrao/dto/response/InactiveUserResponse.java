package br.com.falastrao.falastrao.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InactiveUserResponse(
        UUID externalId,
        String username,
        String email,
        OffsetDateTime createdAt,
        boolean accountVerified,
        boolean accountNonLocked,
        String role,
        String profilePictureUrl,
        OffsetDateTime lastLogin
) {}
