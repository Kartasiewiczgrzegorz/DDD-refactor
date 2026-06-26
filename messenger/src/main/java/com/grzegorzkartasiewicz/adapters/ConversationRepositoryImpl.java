package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Conversation;
import com.grzegorzkartasiewicz.domain.ConversationRepository;
import com.grzegorzkartasiewicz.domain.Message;
import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.MessageId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class ConversationRepositoryImpl implements ConversationRepository {

  private final SqlConversationRepository conversationRepository;
  private final SqlMessageRepository messageRepository;

  @Override
  public Optional<Conversation> findByParticipants(UserId senderId, UserId receiverId) {
    return conversationRepository.findByParticipantsExact(senderId, receiverId)
        .map(ConversationEntity::toDomain);
  }

  @Override
  public Conversation save(Conversation conversation) {
    return conversationRepository.save(ConversationEntity.fromDomain(conversation)).toDomain();
  }

  @Override
  public Message save(Message message) {
    return messageRepository.save(MessageEntity.fromDomain(message)).toDomain();
  }

  @Override
  public Optional<Message> findMessageById(MessageId messageId) {
    return messageRepository.findById(messageId.id()).map(MessageEntity::toDomain);
  }

  @Override
  public Optional<Conversation> findById(ConversationId conversationId) {
    return conversationRepository.findById(conversationId.id()).map(ConversationEntity::toDomain);
  }

  @Override
  public List<Message> findMessagesByConversationId(ConversationId conversationId) {
    return messageRepository.findByConversationId(conversationId).stream()
        .map(MessageEntity::toDomain)
        .collect(Collectors.toList());
  }
}
