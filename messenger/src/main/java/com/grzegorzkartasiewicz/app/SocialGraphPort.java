package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Set;
import java.util.UUID;

public interface SocialGraphPort {

  Set<UUID> getFriendsAndFollowers(UUID actorId);

  boolean areFriends(UserId senderId, UserId receiverId);
}
