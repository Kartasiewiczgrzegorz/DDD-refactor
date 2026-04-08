package com.grzegorzkartasiewicz.adapters;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import com.grzegorzkartasiewicz.api.NotificationFacade;
import com.grzegorzkartasiewicz.domain.PostAction;
import com.grzegorzkartasiewicz.domain.PostChangedEvent;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostEventListenerTest {

  @Mock
  private NotificationFacade notificationFacade;

  @InjectMocks
  private PostEventListener postEventListener;

  @Test
  @DisplayName("handlePostChanged should call notificationFacade with correct request")
  void handlePostChanged_shouldCallFacade() {
    // given
    UUID postId = UUID.randomUUID();
    UUID actorId = UUID.randomUUID();
    UUID recipientId = UUID.randomUUID();
    UUID commentId = UUID.randomUUID();

    PostChangedEvent event = new PostChangedEvent(
        new PostId(postId),
        Optional.of(new CommentId(commentId)),
        new AuthorId(actorId),
        new AuthorId(recipientId),
        PostAction.COMMENT_LIKED
    );

    // when
    postEventListener.handlePostChanged(event);

    // then
    verify(notificationFacade).sendNotification(argThat(request ->
        request.userId().equals(recipientId) &&
            request.type().equals("COMMENT_LIKED") &&
            request.channel().equals("EMAIL") &&
            request.params().get("postId").equals(postId.toString()) &&
            request.params().get("actorId").equals(actorId.toString()) &&
            request.params().get("commentId").equals(commentId.toString())
    ));
  }
}
