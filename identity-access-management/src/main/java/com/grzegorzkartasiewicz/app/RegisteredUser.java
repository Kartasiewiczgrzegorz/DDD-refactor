package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record RegisteredUser(UUID id, String firstName, String lastName, String email, String token) {

}
