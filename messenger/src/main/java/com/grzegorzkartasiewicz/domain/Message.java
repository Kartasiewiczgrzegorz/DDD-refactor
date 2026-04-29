package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.MessageId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {

  private MessageId messageId;
  private ConversationId conversationId;
  private MessageStatus status;
  private final UserId senderId;
  private final UserId receiverId;
  private MessageContent content;

  public Message(ConversationId conversationId, UserId senderId, UserId receiverId,
      MessageContent content) {
    this.messageId = null;
    this.conversationId = conversationId;
    this.status = MessageStatus.SENT;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.content = content;
  }

  public static Message create(ConversationId conversationId, UserId sender, UserId receiver,
      MessageContent content) {
    return new Message(conversationId, sender, receiver, content);
  }

  public void markAsRead(UserId receiver) {
    if (status == MessageStatus.READ) {
      return;
    }
    if (senderId.equals(receiver)) {
      throw new CannotReadOwnMessageException("Cannot read own message");
    }
    if (!receiverId.equals(receiver)) {
      throw new NotAParticipantException("userB is not participant of this conversation");
    }
    status = MessageStatus.READ;
  }
}
