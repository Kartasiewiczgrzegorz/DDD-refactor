package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository {

  Optional<Conversation> findByParticipants(UserId senderId, UserId receiverId);

  Conversation save(Conversation conversation);

  Message save(Message message);

  Optional<Message> findMessageById(MessageId messageId);

  Optional<Conversation> findById(ConversationId conversationId);

  List<Message> findMessagesByConversationId(ConversationId conversationId);
}
