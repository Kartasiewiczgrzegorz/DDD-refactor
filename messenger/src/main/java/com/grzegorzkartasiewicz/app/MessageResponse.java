package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record MessageResponse(UUID senderId, String content) {

}
