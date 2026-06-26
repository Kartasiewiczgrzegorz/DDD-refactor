package com.grzegorzkartasiewicz.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grzegorzkartasiewicz.app.MessageCreationRequest;
import com.grzegorzkartasiewicz.app.MessengerService;
import com.grzegorzkartasiewicz.app.SendMessageCommand;
import com.grzegorzkartasiewicz.app.SocialGraphPort;
import com.grzegorzkartasiewicz.domain.ConversationRepository;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "00000000-0000-0000-0000-000000000000")
class MessengerControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MessengerService messengerService;

  @MockBean
  private SocialGraphPort socialGraphPort;

  @MockBean
  private DomainEventPublisher domainEventPublisher;

  @Autowired
  private ConversationRepository conversationRepository;

  @Test
  @DisplayName("should send message and return 201 Created")
  void shouldSendMessage() throws Exception {
    // given
    UUID receiverId = UUID.randomUUID();
    MessageCreationRequest request = new MessageCreationRequest(receiverId, "Hello IT Test!");

    when(socialGraphPort.areFriends(any(), any())).thenReturn(true);

    // when & then
    mockMvc.perform(post("/api/messenger/messages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.senderId").value("00000000-0000-0000-0000-000000000000"))
        .andExpect(jsonPath("$.content").value("Hello IT Test!"));
  }

  @Test
  @DisplayName("should mark message as read and return 204 No Content")
  void shouldMarkAsRead() throws Exception {
    // given
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.fromString("00000000-0000-0000-0000-000000000000"); // Mock user
    when(socialGraphPort.areFriends(any(), any())).thenReturn(true);

    messengerService.sendMessage(new SendMessageCommand(senderId, receiverId, "Please read this"));

    UUID messageId = conversationRepository.findByParticipants(
            new com.grzegorzkartasiewicz.domain.vo.UserId(senderId),
            new com.grzegorzkartasiewicz.domain.vo.UserId(receiverId))
        .map(conv -> conversationRepository.findMessagesByConversationId(conv.getId()).get(0)
            .getMessageId().id())
        .orElseThrow();

    // when & then
    mockMvc.perform(patch("/api/messenger/messages/{messageId}/read", messageId).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("should get conversation history and return 200 OK")
  void shouldGetHistory() throws Exception {
    // given
    UUID receiverId = UUID.randomUUID();
    UUID senderId = UUID.fromString("00000000-0000-0000-0000-000000000000"); // Mock user
    when(socialGraphPort.areFriends(any(), any())).thenReturn(true);

    messengerService.sendMessage(new SendMessageCommand(senderId, receiverId, "History 1"));
    messengerService.sendMessage(new SendMessageCommand(receiverId, senderId, "History 2"));

    UUID conversationId = conversationRepository.findByParticipants(
            new com.grzegorzkartasiewicz.domain.vo.UserId(senderId),
            new com.grzegorzkartasiewicz.domain.vo.UserId(receiverId))
        .orElseThrow()
        .getId()
        .id();

    // when & then
    mockMvc.perform(get("/api/messenger/conversations/{conversationId}/messages", conversationId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[?(@.content == 'History 1')]").exists())
        .andExpect(jsonPath("$[?(@.content == 'History 2')]").exists());
  }
}
