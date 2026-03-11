package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;
import java.util.Map;
import java.util.UUID;

public record TriggerNotificationCommand(UUID userId, NotificationType type, Channel channel,
                                         Map<String, String> params) {

}
