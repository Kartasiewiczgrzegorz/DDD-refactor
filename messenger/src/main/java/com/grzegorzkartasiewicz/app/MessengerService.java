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

  public static final String CONVERSATION_DONT_EXISTS_MESSAGE = "Conversation with given ID: %s does not exist";
  public static final String MESSAGE_DONT_EXISTS_MESSAGE = "Message with given ID: %s does not exist";
  public static final String NOT_FRIENDS_MESSAGE = "Users %s and %s are not friends";
  public static final String NOT_PART_OF_CONVERSATION_MESSAGE = "User %s is not part of conversation %s";

  public MessageResponse sendMessage(SendMessageCommand command) {
    UserId senderId = new UserId(command.sender());
    UserId receiverId = new UserId(command.receiver());

    Conversation conversation = findOrCreateConversationAndValidate(senderId, receiverId);

    Message message = conversation.sendMessage(senderId, new MessageContent(command.text()));

    conversationRepository.save(message);
    conversationRepository.save(conversation);

    domainEventPublisher.publish(new MessageSent());

    return new MessageResponse(message.getSenderId().id(), message.getContent().value());
  }

  public void markAsRead(MarkAsReadCommand command) {
    MessageId messageId = new MessageId(command.messageId());
    Message message = findMessageOrThrow(messageId);
    
    UserId receiver = new UserId(command.receiver());
    message.markAsRead(receiver);

    conversationRepository.save(message);

    domainEventPublisher.publish(new MessageRead());
  }

  public List<MessageResponse> getHistory(UUID conversationId, UUID participantId) {
    ConversationId convId = new ConversationId(conversationId);
    Conversation conversation = findConversationOrThrow(convId);

    validateParticipant(conversation, new UserId(participantId), convId);

    List<Message> messagesByConversationId = conversationRepository.findMessagesByConversationId(
        convId);
    return mapMessagesToResponse(messagesByConversationId);
  }

  private Conversation findOrCreateConversationAndValidate(UserId senderId, UserId receiverId) {
    Optional<Conversation> conversationOptional = conversationRepository.findByParticipants(
        senderId, receiverId);

    if (conversationOptional.isPresent()) {
      return conversationOptional.get();
    }

    Conversation newConversation = Conversation.create(senderId, receiverId);

    if (!socialGraphPort.areFriends(senderId, receiverId)) {
      throw new NotFriendsException(
          String.format(NOT_FRIENDS_MESSAGE, senderId.id(), receiverId.id()));
    }

    return newConversation;
  }

  private Message findMessageOrThrow(MessageId messageId) {
    return conversationRepository.findMessageById(messageId)
        .orElseThrow(() -> new MessageNotExistsException(
            String.format(MESSAGE_DONT_EXISTS_MESSAGE, messageId.id())));
  }

  private Conversation findConversationOrThrow(ConversationId conversationId) {
    return conversationRepository.findById(conversationId)
        .orElseThrow(() -> new ConversationNotExistsException(
            String.format(CONVERSATION_DONT_EXISTS_MESSAGE, conversationId.id())));
  }

  private void validateParticipant(Conversation conversation, UserId participantId,
      ConversationId conversationId) {
    if (!conversation.isParticipant(participantId)) {
      throw new AccessDeniedException(
          String.format(NOT_PART_OF_CONVERSATION_MESSAGE, participantId.id(), conversationId.id()));
    }
  }

  private List<MessageResponse> mapMessagesToResponse(List<Message> messages) {
    return messages.stream().map(
            message -> new MessageResponse(message.getSenderId().id(), message.getContent().value()))
        .toList();
  }
}
