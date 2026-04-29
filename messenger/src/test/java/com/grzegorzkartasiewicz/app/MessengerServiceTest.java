package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.CannotReadOwnMessageException;
import com.grzegorzkartasiewicz.domain.Conversation;
import com.grzegorzkartasiewicz.domain.ConversationRepository;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.Message;
import com.grzegorzkartasiewicz.domain.MessageStatus;
import com.grzegorzkartasiewicz.domain.NotAParticipantException;
import com.grzegorzkartasiewicz.domain.ValidationException;
import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.MessageId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessengerServiceTest {

  @Mock
  private ConversationRepository conversationRepository;
  @Mock
  private SocialGraphPort socialGraphPort;
  @Mock
  private DomainEventPublisher domainEventPublisher;

  @InjectMocks
  private MessengerService messengerService;

  @Test
  @DisplayName("should create new conversation and send message when no conversation exists and users are friends")
  void sendMessage_shouldCreateNewConversationWhenUsersAreFriends() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    String text = "Hello!";
    SendMessageCommand command = new SendMessageCommand(senderId.id(), receiverId.id(), text);

    when(conversationRepository.findByParticipants(senderId, receiverId)).thenReturn(
        Optional.empty());
    when(socialGraphPort.areFriends(senderId, receiverId)).thenReturn(true);
    when(conversationRepository.save(any(Conversation.class))).thenAnswer(i -> i.getArgument(0));
    when(conversationRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

    MessageResponse response = messengerService.sendMessage(command);

    assertThat(response.senderId()).isEqualTo(senderId.id());
    assertThat(response.content()).isEqualTo(text);
    verify(conversationRepository).save(any(Conversation.class));
    verify(conversationRepository).save(any(Message.class));
    verify(domainEventPublisher).publish(any());
  }

  @Test
  @DisplayName("should throw NotFriendsException when users are not friends and no conversation exists")
  void sendMessage_shouldThrowExceptionWhenUsersAreNotFriends() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    SendMessageCommand command = new SendMessageCommand(senderId.id(), receiverId.id(), "Hi");

    when(conversationRepository.findByParticipants(senderId, receiverId)).thenReturn(
        Optional.empty());
    when(socialGraphPort.areFriends(senderId, receiverId)).thenReturn(false);

    assertThrows(NotFriendsException.class, () -> messengerService.sendMessage(command));
    verify(conversationRepository, never()).save(any(Conversation.class));
  }

  @Test
  @DisplayName("should throw ValidationException when sender and receiver are the same")
  void sendMessage_shouldThrowExceptionWhenParticipantsAreSame() {
    UUID sameUserId = UUID.randomUUID();
    SendMessageCommand command = new SendMessageCommand(sameUserId, sameUserId, "Self message");

    when(conversationRepository.findByParticipants(any(), any())).thenReturn(Optional.empty());

    assertThrows(ValidationException.class, () -> messengerService.sendMessage(command));
  }

  @Test
  @DisplayName("should throw ValidationException when message content is blank")
  void sendMessage_shouldThrowExceptionWhenContentIsBlank() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    SendMessageCommand command = new SendMessageCommand(senderId.id(), receiverId.id(), "   ");

    when(conversationRepository.findByParticipants(senderId, receiverId)).thenReturn(
        Optional.of(Conversation.create(senderId, receiverId)));

    assertThrows(ValidationException.class, () -> messengerService.sendMessage(command));
  }

  @Test
  @DisplayName("should use existing conversation without checking SocialGraphPort")
  void sendMessage_shouldUseExistingConversation() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    Conversation existingConversation = Conversation.create(senderId, receiverId);
    SendMessageCommand command = new SendMessageCommand(senderId.id(), receiverId.id(),
        "How are you?");

    when(conversationRepository.findByParticipants(senderId, receiverId)).thenReturn(
        Optional.of(existingConversation));
    when(conversationRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

    messengerService.sendMessage(command);

    verify(socialGraphPort, never()).areFriends(any(), any());
    verify(conversationRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("should mark message as read")
  void markAsRead_shouldUpdateStatus() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    MessageId messageId = new MessageId(UUID.randomUUID());
    Message message = Message.create(new ConversationId(UUID.randomUUID()), senderId, receiverId,
        new MessageContent("Secret"));
    MarkAsReadCommand command = new MarkAsReadCommand(messageId.id(), receiverId.id());

    when(conversationRepository.findMessageById(messageId)).thenReturn(Optional.of(message));

    messengerService.markAsRead(command);

    assertThat(message.getStatus()).isEqualTo(MessageStatus.READ);
    verify(conversationRepository).save(message);
    verify(domainEventPublisher).publish(any());
  }

  @Test
  @DisplayName("should throw MessageNotExistsException when marking non-existent message as read")
  void markAsRead_shouldThrowExceptionWhenMessageNotFound() {
    UUID messageId = UUID.randomUUID();
    MarkAsReadCommand command = new MarkAsReadCommand(messageId, UUID.randomUUID());

    when(conversationRepository.findMessageById(new MessageId(messageId))).thenReturn(
        Optional.empty());

    assertThrows(MessageNotExistsException.class, () -> messengerService.markAsRead(command));
  }

  @Test
  @DisplayName("should throw CannotReadOwnMessageException when sender tries to mark as read")
  void markAsRead_shouldThrowExceptionWhenSenderTriesToMarkAsRead() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    MessageId messageId = new MessageId(UUID.randomUUID());
    Message message = Message.create(new ConversationId(UUID.randomUUID()), senderId, receiverId,
        new MessageContent("Secret"));
    MarkAsReadCommand command = new MarkAsReadCommand(messageId.id(), senderId.id());

    when(conversationRepository.findMessageById(messageId)).thenReturn(Optional.of(message));

    assertThrows(CannotReadOwnMessageException.class, () -> messengerService.markAsRead(command));
  }

  @Test
  @DisplayName("should throw NotAParticipantException when unauthorized user tries to mark as read")
  void markAsRead_shouldThrowExceptionWhenUserIsNotReceiver() {
    UserId senderId = new UserId(UUID.randomUUID());
    UserId receiverId = new UserId(UUID.randomUUID());
    UserId hackerId = new UserId(UUID.randomUUID());
    MessageId messageId = new MessageId(UUID.randomUUID());
    Message message = Message.create(new ConversationId(UUID.randomUUID()), senderId, receiverId,
        new MessageContent("Secret"));
    MarkAsReadCommand command = new MarkAsReadCommand(messageId.id(), hackerId.id());

    when(conversationRepository.findMessageById(messageId)).thenReturn(Optional.of(message));

    assertThrows(NotAParticipantException.class, () -> messengerService.markAsRead(command));
  }

  @Test
  @DisplayName("should return history for participant")
  void getConversationHistory_shouldReturnMessagesForParticipant() {
    UserId userA = new UserId(UUID.randomUUID());
    UserId userB = new UserId(UUID.randomUUID());
    ConversationId convId = new ConversationId(UUID.randomUUID());
    Conversation conversation = Conversation.create(userA, userB);

    when(conversationRepository.findById(convId)).thenReturn(Optional.of(conversation));
    when(conversationRepository.findMessagesByConversationId(convId)).thenReturn(List.of(
        Message.create(convId, userA, userB, new MessageContent("Msg 1"))
    ));

    List<MessageResponse> history = messengerService.getHistory(convId.id(), userA.id());

    assertThat(history).hasSize(1);
    assertThat(history.get(0).content()).isEqualTo("Msg 1");
  }

  @Test
  @DisplayName("should throw AccessDeniedException when non-participant requests history")
  void getConversationHistory_shouldThrowExceptionForNonParticipant() {
    UserId userA = new UserId(UUID.randomUUID());
    UserId userB = new UserId(UUID.randomUUID());
    UserId hackerC = new UserId(UUID.randomUUID());
    ConversationId convId = new ConversationId(UUID.randomUUID());
    Conversation conversation = Conversation.create(userA, userB);

    when(conversationRepository.findById(convId)).thenReturn(Optional.of(conversation));

    assertThrows(AccessDeniedException.class,
        () -> messengerService.getHistory(convId.id(), hackerC.id()));
  }

  @Test
  @DisplayName("should throw ConversationNotExistsException when requesting history of non-existent conversation")
  void getConversationHistory_shouldThrowExceptionWhenConversationNotFound() {
    UUID convId = UUID.randomUUID();
    when(conversationRepository.findById(new ConversationId(convId))).thenReturn(Optional.empty());

    assertThrows(ConversationNotExistsException.class,
        () -> messengerService.getHistory(convId, UUID.randomUUID()));
  }

  @Test
  @DisplayName("should return empty list when no messages in conversation")
  void getConversationHistory_shouldReturnEmptyListWhenNoMessages() {
    UserId userA = new UserId(UUID.randomUUID());
    UserId userB = new UserId(UUID.randomUUID());
    ConversationId convId = new ConversationId(UUID.randomUUID());
    Conversation conversation = Conversation.create(userA, userB);

    when(conversationRepository.findById(convId)).thenReturn(Optional.of(conversation));
    when(conversationRepository.findMessagesByConversationId(convId)).thenReturn(
        Collections.emptyList());

    List<MessageResponse> history = messengerService.getHistory(convId.id(), userA.id());

    assertThat(history).isEmpty();
  }
}
