package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.FollowUserRequest;
import com.grzegorzkartasiewicz.app.FriendRemovalRequest;
import com.grzegorzkartasiewicz.app.FriendRequestAcceptanceRequest;
import com.grzegorzkartasiewicz.app.FriendRequestRejectionRequest;
import com.grzegorzkartasiewicz.app.FriendRequestSendingRequest;
import com.grzegorzkartasiewicz.app.SocialService;
import com.grzegorzkartasiewicz.app.SocialUserResponse;
import com.grzegorzkartasiewicz.app.UnfollowUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
@Tag(name = "Social Graph", description = "Endpoints for managing friends and followers")
class SocialController {

  private final SocialService socialService;

  @Operation(summary = "Get current user's friends",
      description = "Retrieves a list of friends for the currently authenticated user.",
      responses = {
          @ApiResponse(responseCode = "200", description = "List of friends retrieved successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = SocialUserResponse.class))),
          @ApiResponse(responseCode = "404", description = "User not found")
      })
  @GetMapping("/friends")
  ResponseEntity<List<SocialUserResponse>> getFriends(Principal principal) {
    return ResponseEntity.ok(socialService.getFriends(UUID.fromString(principal.getName())));
  }

  @Operation(summary = "Get current user's followers",
      description = "Retrieves a list of followers for the currently authenticated user.",
      responses = {
          @ApiResponse(responseCode = "200", description = "List of followers retrieved successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = SocialUserResponse.class))),
          @ApiResponse(responseCode = "404", description = "User not found")
      })
  @GetMapping("/followers")
  ResponseEntity<List<SocialUserResponse>> getFollowers(Principal principal) {
    return ResponseEntity.ok(socialService.getFollowers(UUID.fromString(principal.getName())));
  }

  @Operation(summary = "Send a friend request",
      description = "Sends a friend request to another user.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Friend request sent successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request (e.g., self-request)"),
          @ApiResponse(responseCode = "404", description = "Target user not found"),
          @ApiResponse(responseCode = "409", description = "Request already sent or users are already friends")
      })
  @PostMapping("/friends/request")
  ResponseEntity<Void> sendFriendRequest(@RequestBody TargetIdDto targetDto, Principal principal) {
    UUID requesterId = UUID.fromString(principal.getName());
    socialService.sendFriendRequest(
        new FriendRequestSendingRequest(requesterId, targetDto.targetId()));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Accept a friend request",
      description = "Accepts a pending friend request from another user.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Friend request accepted successfully"),
          @ApiResponse(responseCode = "404", description = "Request or user not found")
      })
  @PostMapping("/friends/requests/{requesterId}/accept")
  ResponseEntity<Void> acceptFriendRequest(@PathVariable UUID requesterId, Principal principal) {
    UUID approverId = UUID.fromString(principal.getName());
    socialService.acceptFriendRequest(new FriendRequestAcceptanceRequest(approverId, requesterId));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Reject a friend request",
      description = "Rejects a pending friend request from another user.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Friend request rejected successfully"),
          @ApiResponse(responseCode = "404", description = "Request or user not found")
      })
  @PostMapping("/friends/requests/{requesterId}/reject")
  ResponseEntity<Void> rejectFriendRequest(@PathVariable UUID requesterId, Principal principal) {
    UUID rejectorId = UUID.fromString(principal.getName());
    socialService.rejectFriendRequest(new FriendRequestRejectionRequest(rejectorId, requesterId));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Remove a friend",
      description = "Removes a user from the friends list.",
      responses = {
          @ApiResponse(responseCode = "204", description = "Friend removed successfully"),
          @ApiResponse(responseCode = "404", description = "User not found")
      })
  @DeleteMapping("/friends/{friendId}")
  ResponseEntity<Void> removeFriend(@PathVariable UUID friendId, Principal principal) {
    UUID initiatorId = UUID.fromString(principal.getName());
    socialService.removeFriend(new FriendRemovalRequest(initiatorId, friendId));
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Follow a user",
      description = "Starts following another user.",
      responses = {
          @ApiResponse(responseCode = "200", description = "User followed successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request (e.g., self-follow)"),
          @ApiResponse(responseCode = "404", description = "Target user not found"),
          @ApiResponse(responseCode = "409", description = "Already following the user")
      })
  @PostMapping("/following")
  ResponseEntity<Void> followUser(@RequestBody TargetIdDto targetDto, Principal principal) {
    UUID followerId = UUID.fromString(principal.getName());
    socialService.followUser(new FollowUserRequest(followerId, targetDto.targetId()));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Unfollow a user",
      description = "Stops following another user.",
      responses = {
          @ApiResponse(responseCode = "204", description = "User unfollowed successfully"),
          @ApiResponse(responseCode = "404", description = "User not found")
      })
  @DeleteMapping("/following/{targetId}")
  ResponseEntity<Void> unfollowUser(@PathVariable UUID targetId, Principal principal) {
    UUID followerId = UUID.fromString(principal.getName());
    socialService.unfollowUser(new UnfollowUserRequest(followerId, targetId));
    return ResponseEntity.noContent().build();
  }

  record TargetIdDto(UUID targetId) {

  }
}