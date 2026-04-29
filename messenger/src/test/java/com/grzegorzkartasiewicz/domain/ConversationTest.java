package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.UserId;
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
        .hasMessageContaining("userA and UserB cannot be the same");
  }

  @Test
  void participantCanSendAMessage() {
    Conversation conversation = Conversation.create(userA, userB);
    MessageContent content = new MessageContent("Hello from A!");

    Message message = conversation.sendMessage(userA, content);

    assertThat(message.getSenderId()).isEqualTo(userA);
    assertThat(message.getReceiverId()).isEqualTo(userB);
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
  void receiverCanMarkMessageAsRead() {
    Conversation conversation = Conversation.create(userA, userB);
    Message message = conversation.sendMessage(userA, new MessageContent("Hi B!"));

    message.markAsRead(userB);

    assertThat(message.getStatus()).isEqualTo(MessageStatus.READ);
  }

  @Test
  void senderCannotMarkOwnMessageAsRead() {
    Conversation conversation = Conversation.create(userA, userB);
    Message message = conversation.sendMessage(userA, new MessageContent("Hi B!"));

    assertThatThrownBy(() -> message.markAsRead(userA))
        .isInstanceOf(CannotReadOwnMessageException.class);
  }

  @Test
  void nonReceiverCannotMarkMessageAsRead() {
    Conversation conversation = Conversation.create(userA, userB);
    Message message = conversation.sendMessage(userA, new MessageContent("Top Secret"));

    assertThatThrownBy(() -> message.markAsRead(hackerC))
        .isInstanceOf(NotAParticipantException.class);
  }
}
