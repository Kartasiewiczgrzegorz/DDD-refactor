package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.NotificationType;

public record NotificationSetting(NotificationType type, Channel channel) {

}
