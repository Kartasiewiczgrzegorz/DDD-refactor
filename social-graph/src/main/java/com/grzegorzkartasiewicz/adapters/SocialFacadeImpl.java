package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.SocialService;
import com.grzegorzkartasiewicz.app.SocialUserResponse;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SocialFacadeImpl implements SocialFacade {

  private final SocialService socialService;

  @Override
  public Set<UUID> getFriendsAndFollowers(UUID userId) {
    return Stream.concat(
            socialService.getFriends(userId).stream(),
            socialService.getFollowers(userId).stream()
        )
        .map(SocialUserResponse::id)
        .collect(Collectors.toSet());
  }
}
