package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Followed;
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

/**
 * Aggregate Root representing a User in the social graph context. Manages relationships such as
 * friends (symmetric) and following (asymmetric).
 */
@AllArgsConstructor
public class SocialUser {

  @Getter
  UserId id;
  @Getter
  Name name;
  @Getter
  Email email;

  /**
   * Users who are friends with this user.
   */
  private Set<Friend> friends;
  /**
   * Users who are following this user.
   */
  private Set<Follower> followers;
  /**
   * Users who this user is following.
   */
  private Set<Followed> followedUsers;

  /**
   * Friend requests sent by this user.
   */
  private Set<FriendRequest> sentFriendRequests;
  /**
   * Friend requests received by this user.
   */
  private Set<FriendRequest> receivedFriendRequests;

  SocialUser(Name name, Email email) {
    this.id = new UserId(null);
    this.name = name;
    this.email = email;
    this.friends = new HashSet<>();
    this.followers = new HashSet<>();
    this.followedUsers = new HashSet<>();
    this.sentFriendRequests = new HashSet<>();
    this.receivedFriendRequests = new HashSet<>();
  }

  /**
   * Factory method to create a new User.
   *
   * @param name  The name of the user.
   * @param email The email of the user.
   * @return A new User instance.
   */
  public static SocialUser createNew(Name name, Email email) {
    return new SocialUser(name, email);
  }

  public Set<Friend> getFriends() {
    return Collections.unmodifiableSet(friends);
  }

  public Set<Follower> getFollowers() {
    return Collections.unmodifiableSet(followers);
  }

  public Set<Followed> getFollowedUsers() {
    return Collections.unmodifiableSet(followedUsers);
  }

  public Set<FriendRequest> getSentFriendRequests() {
    return Collections.unmodifiableSet(sentFriendRequests);
  }

  public Set<FriendRequest> getReceivedFriendRequests() {
    return Collections.unmodifiableSet(receivedFriendRequests);
  }

  /**
   * Records that this user has sent a friend request to another user.
   *
   * @param targetId The ID of the user receiving the request.
   * @throws SelfInteractionException       if the user tries to friend themselves.
   * @throws RelationAlreadyExistsException if they are already friends.
   * @throws RequestAlreadySentException    if a request was already sent.
   */
  public void sendFriendRequest(UserId targetId) {
    validateRequestEligibility(targetId);
    sentFriendRequests.add(new FriendRequest(targetId));
  }

  /**
   * Records that this user has received a friend request from another user.
   *
   * @param requesterId The ID of the user who sent the request.
   * @throws RelationAlreadyExistsException if they are already friends.
   * @throws RequestAlreadySentException    if a request from this user to the requester already
   *                                        exists.
   */
  public void receiveFriendRequest(UserId requesterId) {
    validateRequestEligibility(requesterId);
    receivedFriendRequests.add(new FriendRequest(requesterId));
  }

  /**
   * Accepts a friend request received by this user.
   *
   * @param requesterId The ID of the user whose request is being accepted.
   * @throws RequestNotExistsException if the request does not exist in received requests.
   */
  public void acceptFriendRequest(UserId requesterId) {
    if (!isFriendRequestReceivedFrom(requesterId)) {
      throw new RequestNotExistsException(
          String.format("Friend request from %s to %s not found.", requesterId.id(), id.id()));
    }

    receivedFriendRequests.removeIf(req -> req.friendRequestId().equals(requesterId));

    friends.add(new Friend(requesterId));
  }

  /**
   * Updates state when a friend request sent by this user is accepted by the target.
   *
   * @param targetId The ID of the user who accepted the request.
   */
  public void acceptFriendRequestSentByMe(UserId targetId) {
    sentFriendRequests.removeIf(req -> req.friendRequestId().equals(targetId));
    friends.add(new Friend(targetId));
  }

  /**
   * Rejects a friend request received by this user.
   *
   * @param requesterId The ID of the user whose request is being rejected.
   */
  public void rejectFriendRequest(UserId requesterId) {
    receivedFriendRequests.removeIf(req -> req.friendRequestId().equals(requesterId));
  }

  /**
   * Updates state when a friend request sent by this user is rejected by the target.
   *
   * @param targetId The ID of the user who rejected the request.
   */
  public void rejectFriendRequestSentByMe(UserId targetId) {
    sentFriendRequests.removeIf(req -> req.friendRequestId().equals(targetId));
  }

  /**
   * Removes a user from the friends list.
   *
   * @param friendId The ID of the friend to remove.
   */
  public void removeFriend(UserId friendId) {
    friends.removeIf(friend -> friend.friendId().equals(friendId));
  }

  /**
   * Starts following another user.
   *
   * @param targetId The ID of the user to follow.
   * @throws SelfInteractionException  if the user tries to follow themselves.
   * @throws AlreadyFollowingException if the user is already following the target.
   */
  public void follow(UserId targetId) {
    validateSelfInteraction(targetId);
    if (isFollowing(targetId)) {
      throw new AlreadyFollowingException(
          String.format("User %s is already following user %s.", id.id(), targetId.id()));
    }
    followedUsers.add(new Followed(targetId));
  }

  /**
   * Stops following another user.
   *
   * @param targetId The ID of the user to unfollow.
   */
  public void unfollow(UserId targetId) {
    followedUsers.removeIf(f -> f.followedId().equals(targetId));
  }

  /**
   * Records that another user has started following this user.
   *
   * @param followerId The ID of the user who is now following this user.
   */
  public void addFollower(UserId followerId) {
    followers.add(new Follower(followerId));
  }

  /**
   * Records that another user has stopped following this user.
   *
   * @param followerId The ID of the user who is no longer following this user.
   */
  public void removeFollower(UserId followerId) {
    followers.removeIf(f -> f.followerId().equals(followerId));
  }

  /**
   * Checks if a user is a friend.
   *
   * @param userId The ID to check.
   * @return true if they are friends.
   */
  public boolean isFriend(UserId userId) {
    return friends.stream().anyMatch(f -> f.friendId().equals(userId));
  }

  /**
   * Checks if this user is following another user.
   *
   * @param userId The ID of the other user.
   * @return true if following.
   */
  public boolean isFollowing(UserId userId) {
    return followedUsers.stream().anyMatch(f -> f.followedId().equals(userId));
  }

  /**
   * Checks if another user is following this user.
   *
   * @param userId The ID of the other user.
   * @return true if they are a follower.
   */
  public boolean isFollowedBy(UserId userId) {
    return followers.stream().anyMatch(f -> f.followerId().equals(userId));
  }

  private boolean isFriendRequestSentTo(UserId userId) {
    return sentFriendRequests.stream().anyMatch(req -> req.friendRequestId().equals(userId));
  }

  private boolean isFriendRequestReceivedFrom(UserId userId) {
    return receivedFriendRequests.stream().anyMatch(req -> req.friendRequestId().equals(userId));
  }

  private void validateSelfInteraction(UserId otherId) {
    if (this.id != null && this.id.equals(otherId)) {
      throw new SelfInteractionException(
          String.format("User %s cannot interact with themselves.", id.id()));
    }
  }

  private void validateRequestEligibility(UserId otherId) {
    validateSelfInteraction(otherId);
    if (isFriend(otherId)) {
      throw new RelationAlreadyExistsException(
          String.format("User %s is already a friend of %s.", id.id(), otherId.id()));
    }
    if (isFriendRequestSentTo(otherId)) {
      throw new RequestAlreadySentException(
          String.format("Friend request from %s to %s has already been sent.", id.id(),
              otherId.id()));
    }
  }
}