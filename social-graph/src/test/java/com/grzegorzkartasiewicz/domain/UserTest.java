package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  @DisplayName("createNew should create user with empty friends and followers")
  void createNew_shouldCreateUserWithEmptyFriendsAndFollowers() {
    // given
    Name name = new Name("John", "Doe");
    Email email = new Email("john.doe@example.com");

    // when
    User user = User.createNew(name, email);

    // then
    assertThat(user.getName()).isEqualTo(name);
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getId().id()).isNull(); // ID is null until saved
    assertThat(user.getFriends()).isEmpty();
    assertThat(user.getFollowers()).isEmpty();
  }

  @Test
  @DisplayName("addFriend should add friend to set")
  void addFriend_shouldAddFriendToSet() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Friend friend = new Friend(new UserId(UUID.randomUUID()));

    // when
    user.addFriend(friend);

    // then
    assertThat(user.getFriends()).hasSize(1);
    assertThat(user.getFriends()).contains(friend);
  }

  @Test
  @DisplayName("addFriend should not add duplicate friend")
  void addFriend_shouldNotAddDuplicateFriend() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Friend friend = new Friend(new UserId(UUID.randomUUID()));
    user.addFriend(friend);

    // when
    user.addFriend(friend);

    // then
    assertThat(user.getFriends()).hasSize(1);
  }

  @Test
  @DisplayName("removeFriend should remove friend from set")
  void removeFriend_shouldRemoveFriendFromSet() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Friend friend = new Friend(new UserId(UUID.randomUUID()));
    user.addFriend(friend);

    // when
    user.removeFriend(friend);

    // then
    assertThat(user.getFriends()).isEmpty();
  }

  @Test
  @DisplayName("removeFriend should do nothing when friend not found")
  void removeFriend_shouldDoNothingWhenFriendNotFound() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Friend friend = new Friend(new UserId(UUID.randomUUID()));
    user.addFriend(friend);
    Friend otherFriend = new Friend(new UserId(UUID.randomUUID()));

    // when
    user.removeFriend(otherFriend);

    // then
    assertThat(user.getFriends()).hasSize(1);
    assertThat(user.getFriends()).contains(friend);
  }

  @Test
  @DisplayName("addFollower should add follower to set")
  void addFollower_shouldAddFollowerToSet() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Follower follower = new Follower(new UserId(UUID.randomUUID()));

    // when
    user.addFollower(follower);

    // then
    assertThat(user.getFollowers()).hasSize(1);
    assertThat(user.getFollowers()).contains(follower);
  }

  @Test
  @DisplayName("addFollower should not add duplicate follower")
  void addFollower_shouldNotAddDuplicateFollower() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Follower follower = new Follower(new UserId(UUID.randomUUID()));
    user.addFollower(follower);

    // when
    user.addFollower(follower);

    // then
    assertThat(user.getFollowers()).hasSize(1);
  }

  @Test
  @DisplayName("removeFollower should remove follower from set")
  void removeFollower_shouldRemoveFollowerFromSet() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Follower follower = new Follower(new UserId(UUID.randomUUID()));
    user.addFollower(follower);

    // when
    user.removeFollower(follower);

    // then
    assertThat(user.getFollowers()).isEmpty();
  }

  @Test
  @DisplayName("removeFollower should do nothing when follower not found")
  void removeFollower_shouldDoNothingWhenFollowerNotFound() {
    // given
    User user = User.createNew(new Name("John", "Doe"), new Email("john.doe@example.com"));
    Follower follower = new Follower(new UserId(UUID.randomUUID()));
    user.addFollower(follower);
    Follower otherFollower = new Follower(new UserId(UUID.randomUUID()));

    // when
    user.removeFollower(otherFollower);

    // then
    assertThat(user.getFollowers()).hasSize(1);
    assertThat(user.getFollowers()).contains(follower);
  }
}
