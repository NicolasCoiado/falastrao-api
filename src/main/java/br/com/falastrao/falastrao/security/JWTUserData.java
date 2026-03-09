package br.com.falastrao.falastrao.security;

import br.com.falastrao.falastrao.model.enums.UserRoles;
import lombok.Builder;

@Builder
public record JWTUserData(
        Long userId,
        String email,
        UserRoles role
) {}