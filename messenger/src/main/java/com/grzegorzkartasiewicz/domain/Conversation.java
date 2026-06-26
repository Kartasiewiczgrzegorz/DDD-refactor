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

  public static final String PARTICIPANTS_CANNOT_BE_THE_SAME_MESSAGE = "User A and User B cannot be the same";
  public static final String SENDER_NOT_PARTICIPANT_MESSAGE = "Sender %s is not a participant of this conversation";
  public static final String COULD_NOT_FIND_RECEIVER_MESSAGE = "Could not find receiver for sender %s in this conversation";

  public Conversation(Set<UserId> participants) {
    id = null;
    this.participants = participants;
  }

  public static Conversation create(UserId userA, UserId userB) {
    validateDifferentParticipants(userA, userB);
    return new Conversation(Set.of(userA, userB));
  }

  private static void validateDifferentParticipants(UserId userA, UserId userB) {
    if (userA.equals(userB)) {
      throw new ValidationException(PARTICIPANTS_CANNOT_BE_THE_SAME_MESSAGE);
    }
  }

  public Message sendMessage(UserId sender, MessageContent content) {
    validateSender(sender);
    UserId receiver = determineReceiver(sender);
    return Message.create(id, sender, receiver, content);
  }

  public boolean isParticipant(UserId userId) {
    return participants.contains(userId);
  }

  private void validateSender(UserId sender) {
    if (!participants.contains(sender)) {
      throw new NotAParticipantException(
          String.format(SENDER_NOT_PARTICIPANT_MESSAGE, sender.id()));
    }
  }

  private UserId determineReceiver(UserId sender) {
    return participants.stream()
        .filter(participant -> !participant.equals(sender))
        .findFirst()
        .orElseThrow(() -> new NotAParticipantException(
            String.format(COULD_NOT_FIND_RECEIVER_MESSAGE, sender.id())));
  }
}
