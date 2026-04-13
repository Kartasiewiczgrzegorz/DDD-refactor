package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.NotificationService;
import com.grzegorzkartasiewicz.app.ResetPasswordEvent;
import com.grzegorzkartasiewicz.app.TriggerNotificationCommand;
import com.grzegorzkartasiewicz.app.UserEmailVerificationNeededEvent;
import com.grzegorzkartasiewicz.domain.Channel;
import com.grzegorzkartasiewicz.domain.FriendRequestSent;
import com.grzegorzkartasiewicz.domain.NotificationType;
import com.grzegorzkartasiewicz.domain.PostChangedEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationService notificationService;

  @TransactionalEventListener
  public void handle(UserEmailVerificationNeededEvent event) {
    TriggerNotificationCommand command = new TriggerNotificationCommand(
        event.id().id(), // actorId is the user
        event.id().id(), // targetId is also the user
        NotificationType.EMAIL_VERIFICATION,
        Channel.EMAIL,
        Map.of()
    );
    notificationService.triggerNotification(command);
  }

  @TransactionalEventListener
  public void handle(ResetPasswordEvent event) {
    TriggerNotificationCommand command = new TriggerNotificationCommand(
        event.userId().id(),
        event.userId().id(),
        NotificationType.PASSWORD_RESET,
        Channel.EMAIL,
        Map.of()
    );
    notificationService.triggerNotification(command);
  }

  @TransactionalEventListener
  public void handle(FriendRequestSent event) {
    TriggerNotificationCommand command = new TriggerNotificationCommand(
        event.requesterId().id(),
        event.targetId().id(),
        NotificationType.FRIEND_REQUEST,
        Channel.EMAIL,
        Map.of("requesterId", event.requesterId().id().toString())
    );
    notificationService.triggerNotification(command);
  }

  @TransactionalEventListener
  public void handle(PostChangedEvent event) {
    Map<String, String> params = new HashMap<>();
    params.put("postId", event.postId().id().toString());
    params.put("actorId", event.actorId().id().toString());
    event.commentId().ifPresent(cId -> params.put("commentId", cId.id().toString()));

    NotificationType type = NotificationType.valueOf(event.action().name());

    TriggerNotificationCommand command = new TriggerNotificationCommand(
        event.actorId().id(),
        event.recipientId() != null ? event.recipientId().id() : null,
        type,
        Channel.EMAIL,
        params
    );
    notificationService.triggerNotification(command);
  }
}
