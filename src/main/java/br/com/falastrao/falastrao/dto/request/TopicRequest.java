package br.com.falastrao.falastrao.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TopicRequest(
        @NotBlank String newSubject
) {}