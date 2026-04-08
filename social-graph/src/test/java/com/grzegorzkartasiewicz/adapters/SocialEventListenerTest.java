package com.grzegorzkartasiewicz.adapters;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import com.grzegorzkartasiewicz.api.NotificationFacade;
import com.grzegorzkartasiewicz.domain.FriendRequestSent;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialEventListenerTest {

  @Mock
  private NotificationFacade notificationFacade;

  @InjectMocks
  private SocialEventListener socialEventListener;

  @Test
  @DisplayName("handleFriendRequestSent should call notificationFacade with correct request")
  void handleFriendRequestSent_shouldCallFacade() {
    // given
    UUID requesterId = UUID.randomUUID();
    UUID targetId = UUID.randomUUID();
    FriendRequestSent event = new FriendRequestSent(new UserId(requesterId), new UserId(targetId));

    // when
    socialEventListener.handleFriendRequestSent(event);

    // then
    verify(notificationFacade).sendNotification(argThat(request ->
        request.userId().equals(targetId) &&
            request.type().equals("FRIEND_REQUEST") &&
            request.channel().equals("EMAIL") &&
            request.params().get("requesterId").equals(requesterId.toString())
    ));
  }
}
