package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.vo.Verification;
import java.util.UUID;

public record UserEmailVerificationRequest(UUID userId, Verification verification) {

}
