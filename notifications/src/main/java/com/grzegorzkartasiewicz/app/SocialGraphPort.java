package com.grzegorzkartasiewicz.app;

import java.util.Set;
import java.util.UUID;

public interface SocialGraphPort {

  Set<UUID> getFriendsAndFollowers(UUID actorId);
}
