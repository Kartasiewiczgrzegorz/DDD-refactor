package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.DomainEvent;
import com.grzegorzkartasiewicz.domain.vo.UserId;

public record ResetPasswordEvent(UserId userId, String newPassword) implements DomainEvent {

}
