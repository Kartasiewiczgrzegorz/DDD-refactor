package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Conversation {

  private ConversationId id;
  private Set<UserId> participants;

  public Conversation(Set<UserId> participants) {
    id = null;
    this.participants = participants;
  }

  public static Conversation create(UserId userA, UserId userB) {
    if (userA.equals(userB)) {
      throw new ValidationException("userA and UserB cannot be the same");
    }
    return new Conversation(Set.of(userA, userB));
  }


  public Message sendMessage(UserId userA, MessageContent content) {
    if (!participants.contains(userA)) {
      throw new NotAParticipantException("userA is not participant of this conversation");
    }
    return Message.create(id, userA, content);
  }

  public void markAsRead(Message message, UserId userB) {
    if (!participants.contains(userB)) {
      throw new NotAParticipantException("userB is not participant of this conversation");
    }
    message.markAsRead(userB);
  }
}
