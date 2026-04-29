package com.grzegorzkartasiewicz.app;

import java.util.UUID;

public record MarkAsReadCommand(UUID messageId, UUID receiver) {

}
