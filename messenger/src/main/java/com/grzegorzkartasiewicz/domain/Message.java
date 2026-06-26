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

  public static final String CANNOT_READ_OWN_MESSAGE = "User %s cannot read their own message";
  public static final String NOT_A_RECEIVER_MESSAGE = "User %s is not the receiver of this message";

  public static Message create(ConversationId conversationId, UserId sender, UserId receiver,
      MessageContent content) {
    return new Message(conversationId, sender, receiver, content);
  }

  public void markAsRead(UserId receiver) {
    if (status == MessageStatus.READ) {
      return;
    }
    validateNotSender(receiver);
    validateReceiver(receiver);

    status = MessageStatus.READ;
  }

  private void validateNotSender(UserId receiver) {
    if (senderId.equals(receiver)) {
      throw new CannotReadOwnMessageException(
          String.format(CANNOT_READ_OWN_MESSAGE, receiver.id()));
    }
  }

  private void validateReceiver(UserId receiver) {
    if (!receiverId.equals(receiver)) {
      throw new NotAParticipantException(String.format(NOT_A_RECEIVER_MESSAGE, receiver.id()));
    }
  }
}
