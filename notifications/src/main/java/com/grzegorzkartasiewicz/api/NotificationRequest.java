package com.grzegorzkartasiewicz.api;

import java.util.Map;
import java.util.UUID;

public record NotificationRequest(UUID userId, String type, String channel,
                                  Map<String, String> params) {

}
