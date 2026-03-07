package br.com.falastrao.falastrao.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TopicRequest(
        @NotEmpty
        @Size(min = 1, max = 50)
        String subject
) {}
