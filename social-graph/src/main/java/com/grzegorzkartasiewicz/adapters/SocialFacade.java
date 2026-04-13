package com.grzegorzkartasiewicz.adapters;

import java.util.Set;
import java.util.UUID;

public interface SocialFacade {

  Set<UUID> getFriendsAndFollowers(UUID userId);
}
