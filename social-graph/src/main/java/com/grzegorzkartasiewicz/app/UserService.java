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

    approver.addFriend(requester.getId());
    requester.addFriend(approver.getId());

    userRepository.save(approver);
    userRepository.save(requester);
  }
}
