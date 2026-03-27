package br.com.falastrao.falastrao.dto.response;

import br.com.falastrao.falastrao.model.enums.NotificationOrigin;

import java.util.UUID;

public record NotificationResponse(
        UUID externalId,
        NotificationOrigin origin,
        String senderUsername,
        String text,
        boolean read,
        String createdAt
) {}