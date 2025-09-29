package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Email;

public record ResetPasswordRequest(Email email, String password) {

}
