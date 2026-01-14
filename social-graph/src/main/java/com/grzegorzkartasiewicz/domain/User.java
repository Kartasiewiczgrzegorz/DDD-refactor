package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class User {

  @Getter
  UserId id;
  @Getter
  Name name;
  @Getter
  Email email;
  Set<Friend> friends;
  Set<Follower> followers;

  User(Name name, Email email) {
    this.id = new UserId(null);
    this.name = name;
    this.email = email;
    this.friends = new LinkedHashSet<>();
    this.followers = new LinkedHashSet<>();
  }

  public static User createNew(Name name, Email email) {
    return new User(name, email);
  }

  public Set<Friend> getFriends() {
    return Collections.unmodifiableSet(friends);
  }

  public Set<Follower> getFollowers() {
    return Collections.unmodifiableSet(followers);
  }

  public void addFriend(Friend friendToAdd) {
    friends.add(friendToAdd);
  }

  public void removeFriend(Friend friendToRemove) {
    friends.removeIf(friend -> friend.friendId().equals(friendToRemove.friendId()));
  }

  public void addFollower(Follower followerToAdd) {
    followers.add(followerToAdd);
  }

  public void removeFollower(Follower followerToRemove) {
    followers.removeIf(follower -> follower.followerId().equals(followerToRemove.followerId()));
  }
}
