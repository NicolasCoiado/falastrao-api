package br.com.falastrao.falastrao.dto.response;

import java.util.Set;

public record ReviewResponse(
    Long id,
    ReviewAuthorResponse author,
    String title,
    String thumbnailUrl,
    String content,
    String publishedAt,
    Set<String> topics
) {}