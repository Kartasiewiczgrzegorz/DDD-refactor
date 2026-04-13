package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.SocialGraphPort;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SocialGraphAdapter implements SocialGraphPort {

  private final SocialFacade socialFacade;

  @Override
  public Set<UUID> getFriendsAndFollowers(UUID actorId) {
    return socialFacade.getFriendsAndFollowers(actorId);
  }
}
