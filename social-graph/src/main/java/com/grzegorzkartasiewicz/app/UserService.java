package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.SelfInteractionException;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public void sendFriendRequest(UUID requesterId, UUID targetId) {
    if (requesterId.equals(targetId)) {
      throw new SelfInteractionException("You can't friend yourself.");
    }

    User requester = userRepository.findById(new UserId(requesterId)).orElseThrow();
    User target = userRepository.findById(new UserId(targetId)).orElseThrow();

    requester.sendFriendRequest(target.getId());
    target.receiveFriendRequest(requester.getId());
    //TODO sent notification

    userRepository.save(requester);
    userRepository.save(target);
  }

  public void acceptFriendRequest(UUID approverId, UUID requesterId) {
    User approver = userRepository.findById(new UserId(approverId)).orElseThrow();
    User requester = userRepository.findById(new UserId(requesterId)).orElseThrow();

    approver.acceptFriendRequest(requester.getId());
    requester.acceptFriendRequestSentByMe(approver.getId());

    userRepository.save(approver);
    userRepository.save(requester);
  }

  public void rejectFriendRequest(UUID rejectorId, UUID requesterId) {
    User rejector = userRepository.findById(new UserId(rejectorId)).orElseThrow();
    User requester = userRepository.findById(new UserId(requesterId)).orElseThrow();

    rejector.rejectFriendRequest(requester.getId());
    requester.rejectFriendRequestSentByMe(rejector.getId());

    userRepository.save(rejector);
    userRepository.save(requester);
  }

  public void removeFriend(UUID initiatorId, UUID friendId) {
    User initiator = userRepository.findById(new UserId(initiatorId)).orElseThrow();
    User friend = userRepository.findById(new UserId(friendId)).orElseThrow();

    initiator.removeFriend(friend.getId());
    friend.removeFriend(initiator.getId());

    userRepository.save(initiator);
    userRepository.save(friend);
  }

  public void followUser(UUID followerId, UUID followedId) {
    if (followerId.equals(followedId)) {
      throw new SelfInteractionException("You can't follow yourself.");
    }

    User follower = userRepository.findById(new UserId(followerId)).orElseThrow();
    User followed = userRepository.findById(new UserId(followedId)).orElseThrow();

    follower.follow(followed.getId());
    followed.addFollower(follower.getId());

    userRepository.save(follower);
    userRepository.save(followed);
  }

  public void unfollowUser(UUID followerId, UUID followedId) {
    User follower = userRepository.findById(new UserId(followerId)).orElseThrow();
    User followed = userRepository.findById(new UserId(followedId)).orElseThrow();

    follower.unfollow(followed.getId());
    followed.removeFollower(follower.getId());

    userRepository.save(follower);
    userRepository.save(followed);
  }
}
