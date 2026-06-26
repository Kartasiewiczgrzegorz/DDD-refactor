package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Conversation;
import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conversations")
@AllArgsConstructor
@NoArgsConstructor
class ConversationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "conversation_participants", joinColumns = @JoinColumn(name = "conversation_id"))
  @Column(name = "user_id")
  @Convert(converter = UserIdConverter.class)
  private Set<UserId> participants = new HashSet<>();

  static ConversationEntity fromDomain(Conversation conversation) {
    ConversationEntity entity = new ConversationEntity();
    entity.id = conversation.getId() == null ? null : conversation.getId().id();
    entity.participants = new HashSet<>(conversation.getParticipants());
    return entity;
  }

  Conversation toDomain() {
    return new Conversation(
        id == null ? null : new ConversationId(id),
        participants
    );
  }
}
