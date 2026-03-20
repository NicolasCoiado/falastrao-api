package br.com.falastrao.falastrao.dto.response;

import java.util.UUID;

public record ReviewAuthorResponse(
        UUID externalId,
        String username,
        String memberSince
) {}