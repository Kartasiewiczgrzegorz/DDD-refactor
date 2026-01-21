package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.SelfInteractionException;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Application Service for managing social graph interactions. Handles friend requests, friendships,
 * and following relationships.
 */
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * Sends a friend request from one user to another.
   *
   * @param request The request containing requester and target IDs.
   * @throws SelfInteractionException If requesterId equals targetId.
   * @throws NoSuchElementException   If either user is not found.
   */
  public void sendFriendRequest(FriendRequestSendingRequest request) {
    validateSelfInteraction(request.requesterId(), request.targetId());

    User requester = findUserOrThrow(request.requesterId());
    User target = findUserOrThrow(request.targetId());

    requester.sendFriendRequest(target.getId());
    target.receiveFriendRequest(requester.getId());
    //TODO sent notification

    saveUsers(requester, target);
  }

  /**
   * Accepts a pending friend request.
   *
   * @param request The request containing approver and requester IDs.
   * @throws NoSuchElementException If either user is not found.
   */
  public void acceptFriendRequest(FriendRequestAcceptanceRequest request) {
    User approver = findUserOrThrow(request.approverId());
    User requester = findUserOrThrow(request.requesterId());

    approver.acceptFriendRequest(requester.getId());
    requester.acceptFriendRequestSentByMe(approver.getId());

    saveUsers(approver, requester);
  }

  /**
   * Rejects a pending friend request.
   *
   * @param request The request containing rejector and requester IDs.
   * @throws NoSuchElementException If either user is not found.
   */
  public void rejectFriendRequest(FriendRequestRejectionRequest request) {
    User rejector = findUserOrThrow(request.rejectorId());
    User requester = findUserOrThrow(request.requesterId());

    rejector.rejectFriendRequest(requester.getId());
    requester.rejectFriendRequestSentByMe(rejector.getId());

    saveUsers(rejector, requester);
  }

  /**
   * Removes a friend relationship between two users.
   *
   * @param request The request containing initiator and friend IDs.
   * @throws NoSuchElementException If either user is not found.
   */
  public void removeFriend(FriendRemovalRequest request) {
    User initiator = findUserOrThrow(request.initiatorId());
    User friend = findUserOrThrow(request.friendId());

    initiator.removeFriend(friend.getId());
    friend.removeFriend(initiator.getId());

    saveUsers(initiator, friend);
  }

  /**
   * Establishes a following relationship (initiator follows target).
   *
   * @param request The request containing follower and followed IDs.
   * @throws SelfInteractionException If followerId equals followedId.
   * @throws NoSuchElementException   If either user is not found.
   */
  public void followUser(FollowUserRequest request) {
    validateSelfInteraction(request.followerId(), request.followedId());

    User follower = findUserOrThrow(request.followerId());
    User followed = findUserOrThrow(request.followedId());

    follower.follow(followed.getId());
    followed.addFollower(follower.getId());

    saveUsers(follower, followed);
  }

  /**
   * Removes a following relationship.
   *
   * @param request The request containing follower and followed IDs.
   * @throws NoSuchElementException If either user is not found.
   */
  public void unfollowUser(UnfollowUserRequest request) {
    User follower = findUserOrThrow(request.followerId());
    User followed = findUserOrThrow(request.followedId());

    follower.unfollow(followed.getId());
    followed.removeFollower(follower.getId());

    saveUsers(follower, followed);
  }

  private void validateSelfInteraction(UUID id1, UUID id2) {
    if (id1.equals(id2)) {
      throw new SelfInteractionException(
          "Self interaction is not allowed. User cannot perform this action on themselves.");
    }
  }

  private User findUserOrThrow(UUID userId) {
    return userRepository.findById(new UserId(userId))
        .orElseThrow(
            () -> new NoSuchElementException(String.format("User with ID %s not found.", userId)));
  }

  private void saveUsers(User... users) {
    for (User user : users) {
      userRepository.save(user);
    }
  }
}
