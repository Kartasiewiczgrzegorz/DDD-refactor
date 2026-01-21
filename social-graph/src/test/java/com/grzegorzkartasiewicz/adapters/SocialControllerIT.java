package com.grzegorzkartasiewicz.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grzegorzkartasiewicz.app.SocialService;
import com.grzegorzkartasiewicz.domain.SocialUser;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "00000000-0000-0000-0000-000000000000")
class SocialControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SocialService socialService;

  private UUID currentUserId;
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    currentUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    otherUserId = UUID.randomUUID();

    // Create current user (actor)
    SocialUser currentUser = new SocialUser(new UserId(currentUserId), new Name("Current", "User"),
        new Email("current@example.com"), new HashSet<>(), new HashSet<>(), new HashSet<>(),
        new HashSet<>(), new HashSet<>());
    userRepository.save(currentUser);

    // Create other user (target)
    SocialUser otherUser = new SocialUser(new UserId(otherUserId), new Name("Other", "User"),
        new Email("other@example.com"), new HashSet<>(), new HashSet<>(), new HashSet<>(),
        new HashSet<>(), new HashSet<>());
    userRepository.save(otherUser);
  }

  @Test
  @DisplayName("sendFriendRequest should return 200 OK")
  void sendFriendRequest_shouldReturnOk() throws Exception {
    // given
    SocialController.TargetIdDto targetDto = new SocialController.TargetIdDto(otherUserId);

    // when & then
    mockMvc.perform(post("/social/friends/request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(targetDto)))
        .andExpect(status().isOk());

    // verify
    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();
    assertThat(otherUser.getReceivedFriendRequests())
        .extracting(req -> req.friendRequestId().id())
        .contains(currentUserId);
  }

  @Test
  @DisplayName("acceptFriendRequest should return 200 OK")
  void acceptFriendRequest_shouldReturnOk() throws Exception {
    // given (other user sent request to current user)
    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();
    otherUser.sendFriendRequest(new UserId(currentUserId));
    userRepository.save(otherUser);

    SocialUser currentUser = userRepository.findById(new UserId(currentUserId)).orElseThrow();
    currentUser.receiveFriendRequest(new UserId(otherUserId));
    userRepository.save(currentUser);

    // when & then
    mockMvc.perform(post("/social/friends/requests/{requesterId}/accept", otherUserId))
        .andExpect(status().isOk());

    // verify
    SocialUser updatedCurrentUser = userRepository.findById(new UserId(currentUserId))
        .orElseThrow();
    SocialUser updatedOtherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();

    assertThat(updatedCurrentUser.getFriends())
        .extracting(friend -> friend.friendId().id())
        .contains(otherUserId);
    assertThat(updatedCurrentUser.getReceivedFriendRequests()).isEmpty();

    assertThat(updatedOtherUser.getFriends())
        .extracting(friend -> friend.friendId().id())
        .contains(currentUserId);
  }

  @Test
  @DisplayName("rejectFriendRequest should return 200 OK")
  void rejectFriendRequest_shouldReturnOk() throws Exception {
    // given (request received)
    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();
    otherUser.sendFriendRequest(new UserId(currentUserId));
    userRepository.save(otherUser);

    SocialUser currentUser = userRepository.findById(new UserId(currentUserId)).orElseThrow();
    currentUser.receiveFriendRequest(new UserId(otherUserId));
    userRepository.save(currentUser);

    // when & then
    mockMvc.perform(post("/social/friends/requests/{requesterId}/reject", otherUserId))
        .andExpect(status().isOk());

    // verify
    SocialUser updatedCurrentUser = userRepository.findById(new UserId(currentUserId))
        .orElseThrow();
    assertThat(updatedCurrentUser.getReceivedFriendRequests()).isEmpty();
    assertThat(updatedCurrentUser.getFriends()).isEmpty();
  }

  @Test
  @DisplayName("removeFriend should return 204 No Content")
  void removeFriend_shouldReturnNoContent() throws Exception {
    // given (already friends)
    SocialUser currentUser = userRepository.findById(new UserId(currentUserId)).orElseThrow();
    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();

    currentUser.receiveFriendRequest(new UserId(otherUserId));
    currentUser.acceptFriendRequest(new UserId(otherUserId));

    otherUser.sendFriendRequest(new UserId(currentUserId));
    otherUser.acceptFriendRequestSentByMe(new UserId(currentUserId));

    userRepository.save(currentUser);
    userRepository.save(otherUser);

    // when & then
    mockMvc.perform(delete("/social/friends/{friendId}", otherUserId))
        .andExpect(status().isNoContent());

    // verify
    SocialUser updatedCurrentUser = userRepository.findById(new UserId(currentUserId))
        .orElseThrow();
    SocialUser updatedOtherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();

    assertThat(updatedCurrentUser.getFriends()).isEmpty();
    assertThat(updatedOtherUser.getFriends()).isEmpty();
  }

  @Test
  @DisplayName("followUser should return 200 OK")
  void followUser_shouldReturnOk() throws Exception {
    // given
    SocialController.TargetIdDto targetDto = new SocialController.TargetIdDto(otherUserId);

    // when & then
    mockMvc.perform(post("/social/following")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(targetDto)))
        .andExpect(status().isOk());

    // verify
    SocialUser updatedCurrentUser = userRepository.findById(new UserId(currentUserId))
        .orElseThrow();
    SocialUser updatedOtherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();

    assertThat(updatedCurrentUser.getFollowedUsers())
        .extracting(followed -> followed.followedId().id())
        .contains(otherUserId);

    assertThat(updatedOtherUser.getFollowers())
        .extracting(follower -> follower.followerId().id())
        .contains(currentUserId);
  }

  @Test
  @DisplayName("unfollowUser should return 204 No Content")
  void unfollowUser_shouldReturnNoContent() throws Exception {
    // given (already following)
    SocialUser currentUser = userRepository.findById(new UserId(currentUserId)).orElseThrow();
    currentUser.follow(new UserId(otherUserId));
    userRepository.save(currentUser);

    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();
    otherUser.addFollower(new UserId(currentUserId));
    userRepository.save(otherUser);

    // when & then
    mockMvc.perform(delete("/social/following/{targetId}", otherUserId))
        .andExpect(status().isNoContent());

    // verify
    SocialUser updatedCurrentUser = userRepository.findById(new UserId(currentUserId))
        .orElseThrow();
    SocialUser updatedOtherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();

    assertThat(updatedCurrentUser.getFollowedUsers()).isEmpty();
    assertThat(updatedOtherUser.getFollowers()).isEmpty();
  }

  @Test
  @DisplayName("getFriends should return 200 OK")
  void getFriends_shouldReturnOk() throws Exception {
    // given
    SocialUser currentUser = userRepository.findById(new UserId(currentUserId)).orElseThrow();
    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();

    currentUser.receiveFriendRequest(new UserId(otherUserId));
    currentUser.acceptFriendRequest(new UserId(otherUserId));
    otherUser.sendFriendRequest(new UserId(currentUserId));
    otherUser.acceptFriendRequestSentByMe(new UserId(currentUserId));

    userRepository.save(currentUser);
    userRepository.save(otherUser);

    // when & then
    mockMvc.perform(get("/social/friends"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(otherUserId.toString()))
        .andExpect(jsonPath("$[0].firstName").value("Other"))
        .andExpect(jsonPath("$[0].lastName").value("User"));
  }

  @Test
  @DisplayName("getFollowers should return 200 OK")
  void getFollowers_shouldReturnOk() throws Exception {
    // given (other user follows current user)
    SocialUser currentUser = userRepository.findById(new UserId(currentUserId)).orElseThrow();
    currentUser.addFollower(new UserId(otherUserId));
    userRepository.save(currentUser);

    SocialUser otherUser = userRepository.findById(new UserId(otherUserId)).orElseThrow();
    otherUser.follow(new UserId(currentUserId));
    userRepository.save(otherUser);

    // when & then
    mockMvc.perform(get("/social/followers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(otherUserId.toString()))
        .andExpect(jsonPath("$[0].firstName").value("Other"))
        .andExpect(jsonPath("$[0].lastName").value("User"));
  }
}