package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.NotificationId;

public record NotificationSent(NotificationId notificationId) implements DomainEvent {

}
