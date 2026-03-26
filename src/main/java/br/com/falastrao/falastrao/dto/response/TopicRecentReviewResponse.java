package br.com.falastrao.falastrao.dto.response;

import java.util.UUID;

public record TopicRecentReviewResponse(
        UUID externalId,
        String title,
        String author,
        String publishedAt
) {}