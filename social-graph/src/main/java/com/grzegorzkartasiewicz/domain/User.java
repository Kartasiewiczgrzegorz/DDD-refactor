package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.FriendRequest;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Collections;
import java.util.HashSet;
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
  Set<FriendRequest> sentFriendRequests;
  Set<FriendRequest> receivedFriendRequests;

  User(Name name, Email email) {
    this.id = new UserId(null);
    this.name = name;
    this.email = email;
    this.friends = new HashSet<>();
    this.followers = new HashSet<>();
    this.sentFriendRequests = new HashSet<>();
    this.receivedFriendRequests = new HashSet<>();
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

  public Set<FriendRequest> getSentFriendRequests() {
    return Collections.unmodifiableSet(sentFriendRequests);
  }

  public Set<FriendRequest> getReceivedFriendRequests() {
    return Collections.unmodifiableSet(receivedFriendRequests);
  }

  public void addFriend(UserId friendToAdd) {
    if (sentFriendRequests.stream().map(FriendRequest::friendRequestId)
        .noneMatch(userId -> userId.equals(friendToAdd)) && receivedFriendRequests.stream()
        .map(FriendRequest::friendRequestId)
        .noneMatch(userId -> userId.equals(friendToAdd))) {
      throw new RequestNotExistsException("Request for this friendship don't exists.");
    }
    friends.add(new Friend(friendToAdd));
    sentFriendRequests.removeIf(
        friendRequest -> friendRequest.friendRequestId().equals(friendToAdd));
    receivedFriendRequests.removeIf(
        friendRequest -> friendRequest.friendRequestId().equals(friendToAdd));
  }

  public void removeFriend(UserId friendToRemove) {
    friends.removeIf(friend -> friend.friendId().equals(friendToRemove));
  }

  public boolean isFriend(UserId possibleFriend) {
    return friends.stream().map(Friend::friendId).anyMatch(userId -> userId.equals(possibleFriend));
  }

  public void addFollower(UserId followerToAdd) {
    followers.add(new Follower(followerToAdd));
  }

  public void removeFollower(UserId followerToRemove) {
    followers.removeIf(follower -> follower.followerId().equals(followerToRemove));
  }

  public boolean isFriendRequestAlreadySent(UserId target) {
    return sentFriendRequests.stream().map(FriendRequest::friendRequestId)
        .anyMatch(userId -> userId.equals(target));
  }

  public boolean isFriendRequestAlreadyReceived(UserId requester) {
    return receivedFriendRequests.stream().map(FriendRequest::friendRequestId)
        .anyMatch(userId -> userId.equals(requester));
  }

  public void sendFriendRequest(UserId target) {
    if (id.equals(target)) {
      throw new SelfInteractionException("You can't friend yourself.");
    }
    if (friends.stream().map(Friend::friendId).anyMatch(userId -> userId.equals(target))) {
      throw new RelationAlreadyExistsException(
          String.format("Relation already exists for userId: %s", target.id()));
    }
    if (sentFriendRequests.stream().map(FriendRequest::friendRequestId)
        .anyMatch(userId -> userId.equals(target))) {
      throw new RequestAlreadySentException(
          String.format("Request already sent for userId: %s", target.id()));
    }
    sentFriendRequests.add(new FriendRequest(target));
  }

  public void receiveFriendRequest(UserId requester) {
    receivedFriendRequests.add(new FriendRequest(requester));
  }
}
