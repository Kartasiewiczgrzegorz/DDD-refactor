package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEvent;
import com.grzegorzkartasiewicz.domain.UserId;

public record ResetPasswordEvent(UserId userId, String newPassword) implements DomainEvent {

}
