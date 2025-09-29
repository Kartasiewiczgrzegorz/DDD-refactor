package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.UserId;
import com.grzegorzkartasiewicz.domain.Verification;

public record UserEmailVerificationEvent(UserId verifiedUserId, Verification verification) {

}
