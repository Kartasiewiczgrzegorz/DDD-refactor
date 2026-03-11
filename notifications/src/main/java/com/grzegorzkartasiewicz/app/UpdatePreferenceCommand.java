package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;
import java.util.UUID;

public record UpdatePreferenceCommand(UUID userId, NotificationType type, Channel channel,
                                      boolean active) {

}
