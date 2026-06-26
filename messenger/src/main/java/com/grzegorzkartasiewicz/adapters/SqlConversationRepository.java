package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

interface SqlConversationRepository extends Repository<ConversationEntity, UUID> {

  ConversationEntity save(ConversationEntity conversation);

  Optional<ConversationEntity> findById(UUID id);

  @Query("SELECT c FROM ConversationEntity c " +
      "JOIN c.participants p1 " +
      "JOIN c.participants p2 " +
      "WHERE p1 = :user1 AND p2 = :user2 " +
      "AND SIZE(c.participants) = 2")
  Optional<ConversationEntity> findByParticipantsExact(@Param("user1") UserId user1,
      @Param("user2") UserId user2);
}
