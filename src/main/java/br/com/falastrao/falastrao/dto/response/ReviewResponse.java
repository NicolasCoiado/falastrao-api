package br.com.falastrao.falastrao.dto.response;

import java.util.Set;

public record ReviewResponse (
    Long id,
    UserResponse user,
    String title,
    String thumbnailUrl,
    String content,
    String publishedAt,
    Set<TopicResponse> topics
) {}
