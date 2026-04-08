package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record ConfirmResetPasswordRequest(UUID userId, String newPassword) {

}
