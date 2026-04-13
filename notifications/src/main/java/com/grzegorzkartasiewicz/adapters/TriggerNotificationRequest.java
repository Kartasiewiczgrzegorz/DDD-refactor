package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record TriggerNotificationRequest(
    @NotNull UUID actorId,
    UUID targetId,
    @NotNull NotificationType type,
    @NotNull Channel channel,
    Map<String, String> params
) {
}
