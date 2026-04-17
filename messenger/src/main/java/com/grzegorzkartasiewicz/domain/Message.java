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
  private UserId senderId;
  private MessageContent content;

  public Message(ConversationId conversationId, UserId senderId,
      MessageContent content) {
    this.messageId = null;
    this.conversationId = conversationId;
    this.status = MessageStatus.SENT;
    this.senderId = senderId;
    this.content = content;
  }

  public static Message create(ConversationId conversationId, UserId sender,
      MessageContent content) {
    return new Message(conversationId, sender, content);
  }

  public void markAsRead(UserId receiver) {
    if (status == MessageStatus.READ) {
      return;
    }
    if (senderId.equals(receiver)) {
      throw new CannotReadOwnMessageException("Cannot read own message");
    }
    status = MessageStatus.READ;
  }
}
