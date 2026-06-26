package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Message;
import com.grzegorzkartasiewicz.domain.MessageStatus;
import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.MessageId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
class MessageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Convert(converter = ConversationIdConverter.class)
  private ConversationId conversationId;

  @Enumerated(EnumType.STRING)
  private MessageStatus status;

  @Convert(converter = UserIdConverter.class)
  private UserId senderId;

  @Convert(converter = UserIdConverter.class)
  private UserId receiverId;

  @Convert(converter = MessageContentConverter.class)
  private MessageContent content;

  static MessageEntity fromDomain(Message message) {
    MessageEntity entity = new MessageEntity();
    entity.id = message.getMessageId() == null ? null : message.getMessageId().id();
    entity.conversationId = message.getConversationId();
    entity.status = message.getStatus();
    entity.senderId = message.getSenderId();
    entity.receiverId = message.getReceiverId();
    entity.content = message.getContent();
    return entity;
  }

  Message toDomain() {
    return new Message(
        id == null ? null : new MessageId(id),
        conversationId,
        status,
        senderId,
        receiverId,
        content
    );
  }
}
