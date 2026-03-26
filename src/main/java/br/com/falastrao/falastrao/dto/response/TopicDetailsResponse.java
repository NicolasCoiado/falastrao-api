package br.com.falastrao.falastrao.dto.response;

import java.util.List;

public record TopicDetailsResponse(
        String subject,
        long totalReviews,
        long distinctAuthors,
        String firstUsed,
        String lastUsed,
        long reviewsLastMonth,
        List<String> relatedTopics,
        List<TopicRecentReviewResponse> recentReviews
) {}