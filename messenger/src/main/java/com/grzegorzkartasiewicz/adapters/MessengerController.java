package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.MarkAsReadCommand;
import com.grzegorzkartasiewicz.app.MessageCreationRequest;
import com.grzegorzkartasiewicz.app.MessageResponse;
import com.grzegorzkartasiewicz.app.MessengerService;
import com.grzegorzkartasiewicz.app.SendMessageCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messenger")
@RequiredArgsConstructor
@Tag(name = "Messenger", description = "Endpoints for messaging and conversations")
class MessengerController {

  private final MessengerService messengerService;

  @Operation(summary = "Send a new message",
      description = "Sends a new message to a specific receiver. Automatically infers the sender from the authenticated principal.",
      responses = {
          @ApiResponse(responseCode = "201", description = "Message sent successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data or users are not friends")
      })
  @PostMapping("/messages")
  ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageCreationRequest request,
      Principal principal) {
    SendMessageCommand command = new SendMessageCommand(
        UUID.fromString(principal.getName()),
        request.receiverId(),
        request.text()
    );
    MessageResponse response = messengerService.sendMessage(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Mark a message as read",
      description = "Marks a specific message as read. Automatically infers the receiver from the authenticated principal.",
      responses = {
          @ApiResponse(responseCode = "204", description = "Message marked as read successfully"),
          @ApiResponse(responseCode = "403", description = "Unauthorized to mark this message as read"),
          @ApiResponse(responseCode = "404", description = "Message not found")
      })
  @PatchMapping("/messages/{messageId}/read")
  ResponseEntity<Void> markAsRead(@PathVariable UUID messageId, Principal principal) {
    MarkAsReadCommand command = new MarkAsReadCommand(
        messageId,
        UUID.fromString(principal.getName())
    );
    messengerService.markAsRead(command);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get conversation history",
      description = "Retrieves the history of messages for a specific conversation.",
      responses = {
          @ApiResponse(responseCode = "200", description = "History retrieved successfully",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "403", description = "User is not a participant of the conversation"),
          @ApiResponse(responseCode = "404", description = "Conversation not found")
      })
  @GetMapping("/conversations/{conversationId}/messages")
  ResponseEntity<List<MessageResponse>> getHistory(@PathVariable UUID conversationId,
      Principal principal) {
    List<MessageResponse> history = messengerService.getHistory(
        conversationId,
        UUID.fromString(principal.getName())
    );
    return ResponseEntity.ok(history);
  }
}
