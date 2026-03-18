package br.com.falastrao.falastrao.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TopicSuggestionRequest(
        @NotBlank String title,
        @NotBlank String content
) {}