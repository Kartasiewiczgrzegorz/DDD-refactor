package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Email;

public record UserLogInRequest(Email email, String password) {

}
