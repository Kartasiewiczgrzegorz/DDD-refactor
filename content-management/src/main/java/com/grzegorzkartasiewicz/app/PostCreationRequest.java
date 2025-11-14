package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record PostCreationRequest(String description, UUID authorId) {

}
