package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdatePreferenceRequest(
    @NotNull UUID userId,
    @NotNull NotificationType type,
    @NotNull Channel channel,
    boolean active
) {

}
