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

class SocialUserTest {

  private SocialUser socialUser;
  private UserId userId;
  private UserId otherUserId;

  @BeforeEach
  void setUp() {
    userId = new UserId(UUID.randomUUID());
    otherUserId = new UserId(UUID.randomUUID());
    socialUser = new SocialUser(userId, new Name("John", "Doe"), new Email("john@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  @Test
  @DisplayName("createNew should create user with empty collections")
  void createNew_shouldCreateUserWithEmptyCollections() {
    SocialUser newSocialUser = SocialUser.createNew(new Name("Jane", "Doe"),
        new Email("jane@example.com"));
    assertThat(newSocialUser.getFriends()).isEmpty();
    assertThat(newSocialUser.getFollowers()).isEmpty();
    assertThat(newSocialUser.getFollowedUsers()).isEmpty();
    assertThat(newSocialUser.getSentFriendRequests()).isEmpty();
    assertThat(newSocialUser.getReceivedFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("sendFriendRequest should add request to sent list")
  void sendFriendRequest_shouldAddRequestToSentList() {
    socialUser.sendFriendRequest(otherUserId);
    assertThat(socialUser.getSentFriendRequests()).extracting(FriendRequest::friendRequestId)
        .contains(otherUserId);
  }

  @Test
  @DisplayName("sendFriendRequest should throw SelfInteractionException when targeting self")
  void sendFriendRequest_shouldThrowSelfInteractionException() {
    assertThatThrownBy(() -> socialUser.sendFriendRequest(userId))
        .isInstanceOf(SelfInteractionException.class);
  }

  @Test
  @DisplayName("sendFriendRequest should throw RelationAlreadyExistsException when already friends")
  void sendFriendRequest_shouldThrowRelationAlreadyExistsException() {
    // given
    socialUser = new SocialUser(userId, new Name("John", "Doe"), new Email("john@example.com"),
        new HashSet<>(java.util.List.of(new Friend(otherUserId))),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

    assertThatThrownBy(() -> socialUser.sendFriendRequest(otherUserId))
        .isInstanceOf(RelationAlreadyExistsException.class);
  }

  @Test
  @DisplayName("sendFriendRequest should throw RequestAlreadySentException when request exists")
  void sendFriendRequest_shouldThrowRequestAlreadySentException() {
    socialUser.sendFriendRequest(otherUserId);
    assertThatThrownBy(() -> socialUser.sendFriendRequest(otherUserId))
        .isInstanceOf(RequestAlreadySentException.class);
  }

  @Test
  @DisplayName("receiveFriendRequest should add to received list")
  void receiveFriendRequest_shouldAddToReceivedList() {
    socialUser.receiveFriendRequest(otherUserId);
    assertThat(socialUser.getReceivedFriendRequests()).extracting(FriendRequest::friendRequestId)
        .contains(otherUserId);
  }

  @Test
  @DisplayName("receiveFriendRequest should throw RelationAlreadyExistsException when already friends")
  void receiveFriendRequest_shouldThrowRelationAlreadyExistsException() {
    // given
    socialUser = new SocialUser(userId, new Name("John", "Doe"), new Email("john@example.com"),
        new HashSet<>(java.util.List.of(new Friend(otherUserId))),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

    assertThatThrownBy(() -> socialUser.receiveFriendRequest(otherUserId))
        .isInstanceOf(RelationAlreadyExistsException.class);
  }

  @Test
  @DisplayName("receiveFriendRequest should throw RequestAlreadySentException when request exists")
  void receiveFriendRequest_shouldThrowRequestAlreadySentException() {
    socialUser.sendFriendRequest(otherUserId);
    assertThatThrownBy(() -> socialUser.receiveFriendRequest(otherUserId))
        .isInstanceOf(RequestAlreadySentException.class);
  }

  @Test
  @DisplayName("acceptFriendRequest should add friend and remove received request")
  void acceptFriendRequest_shouldAddFriendAndRemoveRequest() {
    // given
    socialUser.receiveFriendRequest(otherUserId);

    // when
    socialUser.acceptFriendRequest(otherUserId);

    // then
    assertThat(socialUser.getFriends()).extracting(Friend::friendId).contains(otherUserId);
    assertThat(socialUser.getReceivedFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("acceptFriendRequest should throw RequestNotExistsException if no request")
  void acceptFriendRequest_shouldThrowRequestNotExistsException() {
    assertThatThrownBy(() -> socialUser.acceptFriendRequest(otherUserId))
        .isInstanceOf(RequestNotExistsException.class);
  }

  @Test
  @DisplayName("acceptFriendRequestSentByMe should add friend and remove sent request")
  void acceptFriendRequestSentByMe_shouldAddFriendAndRemoveSentRequest() {
    // given
    socialUser.sendFriendRequest(otherUserId);

    // when
    socialUser.acceptFriendRequestSentByMe(otherUserId);

    // then
    assertThat(socialUser.getFriends()).extracting(Friend::friendId).contains(otherUserId);
    assertThat(socialUser.getSentFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("rejectFriendRequest should remove received request")
  void rejectFriendRequest_shouldRemoveReceivedRequest() {
    socialUser.receiveFriendRequest(otherUserId);
    socialUser.rejectFriendRequest(otherUserId);
    assertThat(socialUser.getReceivedFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("rejectFriendRequestSentByMe should remove sent request")
  void rejectFriendRequestSentByMe_shouldRemoveSentRequest() {
    socialUser.sendFriendRequest(otherUserId);
    socialUser.rejectFriendRequestSentByMe(otherUserId);
    assertThat(socialUser.getSentFriendRequests()).isEmpty();
  }

  @Test
  @DisplayName("removeFriend should remove friend from list")
  void removeFriend_shouldRemoveFriend() {
    // given
    socialUser.receiveFriendRequest(otherUserId);
    socialUser.acceptFriendRequest(otherUserId);

    // when
    socialUser.removeFriend(otherUserId);

    // then
    assertThat(socialUser.getFriends()).isEmpty();
  }

  @Test
  @DisplayName("follow should add to followed users list")
  void follow_shouldAddToFollowedUsersList() {
    socialUser.follow(otherUserId);
    assertThat(socialUser.getFollowedUsers()).extracting(Followed::followedId)
        .contains(otherUserId);
  }

  @Test
  @DisplayName("follow should throw SelfInteractionException")
  void follow_shouldThrowSelfInteractionException() {
    assertThatThrownBy(() -> socialUser.follow(userId))
        .isInstanceOf(SelfInteractionException.class);
  }

  @Test
  @DisplayName("follow should throw AlreadyFollowingException")
  void follow_shouldThrowAlreadyFollowingException() {
    socialUser.follow(otherUserId);
    assertThatThrownBy(() -> socialUser.follow(otherUserId))
        .isInstanceOf(AlreadyFollowingException.class);
  }

  @Test
  @DisplayName("unfollow should remove from followed users list")
  void unfollow_shouldRemoveFromFollowedUsersList() {
    socialUser.follow(otherUserId);
    socialUser.unfollow(otherUserId);
    assertThat(socialUser.getFollowedUsers()).isEmpty();
  }

  @Test
  @DisplayName("addFollower should add to followers list")
  void addFollower_shouldAddToFollowersList() {
    socialUser.addFollower(otherUserId);
    assertThat(socialUser.getFollowers()).extracting(Follower::followerId).contains(otherUserId);
  }

  @Test
  @DisplayName("removeFollower should remove from followers list")
  void removeFollower_shouldRemoveFromFollowersList() {
    socialUser.addFollower(otherUserId);
    socialUser.removeFollower(otherUserId);
    assertThat(socialUser.getFollowers()).isEmpty();
  }
}
