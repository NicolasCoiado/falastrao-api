package br.com.falastrao.falastrao.dto.response;

import java.util.Set;
import java.util.UUID;

public record ReviewResponse(
        UUID externalId,
        ReviewAuthorResponse author,
        String title,
        String thumbnailUrl,
        String content,
        String publishedAt,
        Set<String> topics
) {}