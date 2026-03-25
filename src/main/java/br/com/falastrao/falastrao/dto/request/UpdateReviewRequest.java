package br.com.falastrao.falastrao.dto.request;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateReviewRequest(
        @Size(min = 1, max = 100)
        String title,

        String thumbnailUrl,

        String content,

        Set<String> topics
) {}