package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.api.NotificationFacade;
import com.grzegorzkartasiewicz.api.NotificationRequest;
import com.grzegorzkartasiewicz.domain.PostChangedEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostEventListener {

  private final NotificationFacade notificationFacade;

  @EventListener
  public void handlePostChanged(PostChangedEvent event) {
    Map<String, String> params = new HashMap<>();
    params.put("postId", event.postId().id().toString());
    params.put("actorId", event.actorId().id().toString());
    event.commentId().ifPresent(cId -> params.put("commentId", cId.id().toString()));

    NotificationRequest request = new NotificationRequest(
        event.recipientId().id(),
        event.action().name(),
        "EMAIL",
        params
    );

    notificationFacade.sendNotification(request);
  }
}
