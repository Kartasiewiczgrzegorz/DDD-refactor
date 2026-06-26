package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record MessageCreationRequest(UUID receiverId, String text) {

}
