package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEvent;
import com.grzegorzkartasiewicz.domain.vo.UserId;

public record UserEmailVerificationNeededEvent(
    UserId id) implements DomainEvent {

}
