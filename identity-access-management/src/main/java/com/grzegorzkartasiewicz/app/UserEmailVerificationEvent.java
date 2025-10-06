package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEvent;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import com.grzegorzkartasiewicz.domain.vo.Verification;

public record UserEmailVerificationEvent(UserId verifiedUserId, Verification verification) implements
    DomainEvent {

}
