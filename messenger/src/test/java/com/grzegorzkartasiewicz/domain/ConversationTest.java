package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Iterator;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationTest {

  private UserId userA;
  private UserId userB;
  private UserId hackerC;

  @BeforeEach
  void setUp() {
    userA = new UserId(UUID.randomUUID());
    userB = new UserId(UUID.randomUUID());
    hackerC = new UserId(UUID.randomUUID());
  }

  @Test
  void shouldCreateConversationWithTwoParticipants() {
    Conversation conversation = Conversation.create(userA, userB);

    assertThat(conversation.getParticipants()).containsExactlyInAnyOrder(userA, userB);
  }

  @Test
  void shouldThrowExceptionWhenParticipantsAreTheSame() {
    assertThatThrownBy(() -> Conversation.create(userA, userA))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Cannot create a conversation with oneself");
  }

  @Test
  void shouldStoreParticipantsInConsistentOrderToSupportDatabaseUniqueConstraints() {
    // Given
    UserId smallerId = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    UserId largerId = new UserId(UUID.fromString("99999999-9999-9999-9999-999999999999"));

    Conversation conversation = Conversation.create(largerId, smallerId);

    Iterator<UserId> iterator = conversation.getParticipants().iterator();
    assertThat(iterator.next()).isEqualTo(smallerId);
    assertThat(iterator.next()).isEqualTo(largerId);
  }

  @Test
  void participantCanSendAMessage() {
    Conversation conversation = Conversation.create(userA, userB);
    MessageContent content = new MessageContent("Hello from A!");

    Message message = conversation.sendMessage(userA, content);

    assertThat(message.getSenderId()).isEqualTo(userA);
    assertThat(message.getStatus()).isEqualTo(MessageStatus.SENT);
    assertThat(message.getConversationId()).isEqualTo(conversation.getId());
  }

  @Test
  void nonParticipantCannotSendAMessage() {
    Conversation conversation = Conversation.create(userA, userB);
    MessageContent content = new MessageContent("I'm trying to spam!");

    assertThatThrownBy(() -> conversation.sendMessage(hackerC, content))
        .isInstanceOf(NotAParticipantException.class);
  }

  @Test
  void participantCanMarkReceivedMessageAsRead() {
    Conversation conversation = Conversation.create(userA, userB);
    Message message = conversation.sendMessage(userA, new MessageContent("Hi B!"));

    conversation.markAsRead(message, userB);

    assertThat(message.getStatus()).isEqualTo(MessageStatus.READ);
  }

  @Test
  void senderCannotMarkOwnMessageAsRead() {
    Conversation conversation = Conversation.create(userA, userB);
    Message message = conversation.sendMessage(userA, new MessageContent("Hi B!"));

    assertThatThrownBy(() -> conversation.markAsRead(message, userA))
        .isInstanceOf(CannotReadOwnMessageException.class);
  }

  @Test
  void nonParticipantCannotMarkMessageAsRead() {
    Conversation conversation = Conversation.create(userA, userB);
    Message message = conversation.sendMessage(userA, new MessageContent("Top Secret"));

    assertThatThrownBy(() -> conversation.markAsRead(message, hackerC))
        .isInstanceOf(NotAParticipantException.class);
  }
}