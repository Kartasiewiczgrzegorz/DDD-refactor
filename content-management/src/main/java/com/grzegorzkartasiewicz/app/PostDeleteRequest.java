package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record PostDeleteRequest(UUID id, UUID authorId) {

}
