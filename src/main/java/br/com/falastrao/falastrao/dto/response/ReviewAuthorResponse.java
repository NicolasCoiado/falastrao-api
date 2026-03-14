package br.com.falastrao.falastrao.dto.response;

public record ReviewAuthorResponse(
        Long id,
        String username,
        String memberSince
) {}