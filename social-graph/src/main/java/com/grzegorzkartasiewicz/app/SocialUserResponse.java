package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record SocialUserResponse(UUID id, String firstName, String lastName, String email) {

}
