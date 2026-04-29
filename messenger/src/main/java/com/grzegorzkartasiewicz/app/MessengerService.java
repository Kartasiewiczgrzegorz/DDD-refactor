package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Conversation;
import com.grzegorzkartasiewicz.domain.ConversationRepository;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Message;
import com.grzegorzkartasiewicz.domain.MessageRead;
import com.grzegorzkartasiewicz.domain.MessageSent;
import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.MessageId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessengerService {

  private final ConversationRepository conversationRepository;
  private final SocialGraphPort socialGraphPort;
  private final DomainEventPublisher domainEventPublisher;

  public MessageResponse sendMessage(SendMessageCommand command) {
    UserId senderId = new UserId(command.sender());
    UserId receiverId = new UserId(command.receiver());
    Optional<Conversation> conversationOptional = conversationRepository.findByParticipants(
        senderId, receiverId);
    Conversation conversation = conversationOptional.orElse(
        Conversation.create(senderId, receiverId));

    if (conversationOptional.isEmpty() && !socialGraphPort.areFriends(senderId, receiverId)) {
      throw new NotFriendsException("Not friends");
    }

    Message message = conversation.sendMessage(senderId, new MessageContent(command.text()));

    conversationRepository.save(message);
    conversationRepository.save(conversation);

    domainEventPublisher.publish(new MessageSent());

    return new MessageResponse(message.getSenderId().id(), message.getContent().value());
  }

  public void markAsRead(MarkAsReadCommand command) {
    MessageId messageId = new MessageId(command.messageId());
    Message message = conversationRepository.findMessageById(messageId)
        .orElseThrow(() -> new MessageNotExistsException("Message not found"));
    UserId receiver = new UserId(command.receiver());
    message.markAsRead(receiver);

    conversationRepository.save(message);

    domainEventPublisher.publish(new MessageRead());
  }

  public List<MessageResponse> getHistory(UUID conversationId, UUID participantId) {
    conversationRepository.findById(
        new ConversationId(conversationId)).map(conv -> {
      if (!conv.isParticipant(new UserId(participantId))) {
        throw new AccessDeniedException("Not part of conversation");
      }
      return conv;
    }).orElseThrow(() -> new ConversationNotExistsException("Conversation not found"));
    List<Message> messagesByConversationId = conversationRepository.findMessagesByConversationId(
        new ConversationId(conversationId));
    return messagesByConversationId.stream().map(
            message -> new MessageResponse(message.getSenderId().id(), message.getContent().value()))
        .toList();
  }
}
