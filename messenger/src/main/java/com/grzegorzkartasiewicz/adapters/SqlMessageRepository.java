package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.Repository;

interface SqlMessageRepository extends Repository<MessageEntity, UUID> {

  MessageEntity save(MessageEntity message);

  Optional<MessageEntity> findById(UUID id);

  List<MessageEntity> findByConversationId(ConversationId conversationId);
}
