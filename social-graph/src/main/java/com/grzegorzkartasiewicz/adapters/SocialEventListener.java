package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.api.NotificationFacade;
import com.grzegorzkartasiewicz.api.NotificationRequest;
import com.grzegorzkartasiewicz.domain.FriendRequestSent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocialEventListener {

  private final NotificationFacade notificationFacade;

  @EventListener
  public void handleFriendRequestSent(FriendRequestSent event) {
    notificationFacade.sendNotification(new NotificationRequest(
        event.targetId().id(),
        "FRIEND_REQUEST",
        "EMAIL",
        Map.of("requesterId", event.requesterId().id().toString())
    ));
  }
}
