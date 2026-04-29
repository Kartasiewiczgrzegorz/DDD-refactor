package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record SendMessageCommand(UUID sender, UUID receiver, String text) {

}
