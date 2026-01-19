package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Followed;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.FriendRequest;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  private User user;
  private UserId userId;
  private UserId otherUserId;

  @BeforeEach
  void setUp() {
    userId = new UserId(UUID.randomUUID());
    otherUserId = new UserId(UUID.randomUUID());
    user = new User(userId, new Name("John", "Doe"), new Email("john@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  @Test
  @DisplayName("createNew should create user with empty collections")
  void createNew_shouldCreateUserWithEmptyCollections() {
    User newUser = User.createNew(new Name("Jane", "Doe"), new Email("jane@example.com"));
    assertThat(newUser.getFriends()).isEmpty();
    assertThat(newUser.getFollowers()).isEmpty();
    assertThat(newUser.getFollowedUsers()).isEmpty();
    assertThat(newUser.getSentFriendRequests()).isEmpty();
    assertThat(newUser.getReceivedFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("sendFriendRequest should add request to sent list")
  void sendFriendRequest_shouldAddRequestToSentList() {
    user.sendFriendRequest(otherUserId);
    assertThat(user.getSentFriendRequests()).extracting(FriendRequest::friendRequestId)
        .contains(otherUserId);
  }

  @Test
  @DisplayName("sendFriendRequest should throw SelfInteractionException when targeting self")
  void sendFriendRequest_shouldThrowSelfInteractionException() {
    assertThatThrownBy(() -> user.sendFriendRequest(userId))
        .isInstanceOf(SelfInteractionException.class);
  }

  @Test
  @DisplayName("sendFriendRequest should throw RelationAlreadyExistsException when already friends")
  void sendFriendRequest_shouldThrowRelationAlreadyExistsException() {
    // given
    user = new User(userId, new Name("John", "Doe"), new Email("john@example.com"),
        new HashSet<>(java.util.List.of(new Friend(otherUserId))),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

    assertThatThrownBy(() -> user.sendFriendRequest(otherUserId))
        .isInstanceOf(RelationAlreadyExistsException.class);
  }

  @Test
  @DisplayName("sendFriendRequest should throw RequestAlreadySentException when request exists")
  void sendFriendRequest_shouldThrowRequestAlreadySentException() {
    user.sendFriendRequest(otherUserId);
    assertThatThrownBy(() -> user.sendFriendRequest(otherUserId))
        .isInstanceOf(RequestAlreadySentException.class);
  }

  @Test
  @DisplayName("receiveFriendRequest should add to received list")
  void receiveFriendRequest_shouldAddToReceivedList() {
    user.receiveFriendRequest(otherUserId);
    assertThat(user.getReceivedFriendRequests()).extracting(FriendRequest::friendRequestId)
        .contains(otherUserId);
  }

  @Test
  @DisplayName("receiveFriendRequest should throw RelationAlreadyExistsException when already friends")
  void receiveFriendRequest_shouldThrowRelationAlreadyExistsException() {
    // given
    user = new User(userId, new Name("John", "Doe"), new Email("john@example.com"),
        new HashSet<>(java.util.List.of(new Friend(otherUserId))),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

    assertThatThrownBy(() -> user.receiveFriendRequest(otherUserId))
        .isInstanceOf(RelationAlreadyExistsException.class);
  }

  @Test
  @DisplayName("receiveFriendRequest should throw RequestAlreadySentException when request exists")
  void receiveFriendRequest_shouldThrowRequestAlreadySentException() {
    user.sendFriendRequest(otherUserId);
    assertThatThrownBy(() -> user.receiveFriendRequest(otherUserId))
        .isInstanceOf(RequestAlreadySentException.class);
  }

  @Test
  @DisplayName("acceptFriendRequest should add friend and remove received request")
  void acceptFriendRequest_shouldAddFriendAndRemoveRequest() {
    // given
    user.receiveFriendRequest(otherUserId);

    // when
    user.acceptFriendRequest(otherUserId);

    // then
    assertThat(user.getFriends()).extracting(Friend::friendId).contains(otherUserId);
    assertThat(user.getReceivedFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("acceptFriendRequest should throw RequestNotExistsException if no request")
  void acceptFriendRequest_shouldThrowRequestNotExistsException() {
    assertThatThrownBy(() -> user.acceptFriendRequest(otherUserId))
        .isInstanceOf(RequestNotExistsException.class);
  }

  @Test
  @DisplayName("acceptFriendRequestSentByMe should add friend and remove sent request")
  void acceptFriendRequestSentByMe_shouldAddFriendAndRemoveSentRequest() {
    // given
    user.sendFriendRequest(otherUserId);

    // when
    user.acceptFriendRequestSentByMe(otherUserId);

    // then
    assertThat(user.getFriends()).extracting(Friend::friendId).contains(otherUserId);
    assertThat(user.getSentFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("rejectFriendRequest should remove received request")
  void rejectFriendRequest_shouldRemoveReceivedRequest() {
    user.receiveFriendRequest(otherUserId);
    user.rejectFriendRequest(otherUserId);
    assertThat(user.getReceivedFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("rejectFriendRequestSentByMe should remove sent request")
  void rejectFriendRequestSentByMe_shouldRemoveSentRequest() {
    user.sendFriendRequest(otherUserId);
    user.rejectFriendRequestSentByMe(otherUserId);
    assertThat(user.getSentFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("removeFriend should remove friend from list")
  void removeFriend_shouldRemoveFriend() {
    // given
    user.receiveFriendRequest(otherUserId);
    user.acceptFriendRequest(otherUserId);

    // when
    user.removeFriend(otherUserId);

    // then
    assertThat(user.getFriends()).isEmpty();
  }

  @Test
  @DisplayName("follow should add to followed users list")
  void follow_shouldAddToFollowedUsersList() {
    user.follow(otherUserId);
    assertThat(user.getFollowedUsers()).extracting(Followed::followedId).contains(otherUserId);
  }

  @Test
  @DisplayName("follow should throw SelfInteractionException")
  void follow_shouldThrowSelfInteractionException() {
    assertThatThrownBy(() -> user.follow(userId))
        .isInstanceOf(SelfInteractionException.class);
  }

  @Test
  @DisplayName("follow should throw AlreadyFollowingException")
  void follow_shouldThrowAlreadyFollowingException() {
    user.follow(otherUserId);
    assertThatThrownBy(() -> user.follow(otherUserId))
        .isInstanceOf(AlreadyFollowingException.class);
  }

  @Test
  @DisplayName("unfollow should remove from followed users list")
  void unfollow_shouldRemoveFromFollowedUsersList() {
    user.follow(otherUserId);
    user.unfollow(otherUserId);
    assertThat(user.getFollowedUsers()).isEmpty();
  }

  @Test
  @DisplayName("addFollower should add to followers list")
  void addFollower_shouldAddToFollowersList() {
    user.addFollower(otherUserId);
    assertThat(user.getFollowers()).extracting(Follower::followerId).contains(otherUserId);
  }

  @Test
  @DisplayName("removeFollower should remove from followers list")
  void removeFollower_shouldRemoveFromFollowersList() {
    user.addFollower(otherUserId);
    user.removeFollower(otherUserId);
    assertThat(user.getFollowers()).isEmpty();
  }
}
