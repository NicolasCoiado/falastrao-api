package br.com.falastrao.falastrao.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ReviewRequest(
    @NotEmpty
    @Size(min = 1, max = 100)
    String title,

    String thumbnailUrl,

    @NotEmpty
    String content,

    Set<String> topics
) {}
