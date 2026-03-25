package br.com.falastrao.falastrao.dto.response;

public record TrendingTopicResponse(
        String subject,
        long reviewCount
) {}