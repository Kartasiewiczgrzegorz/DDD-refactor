package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User requester;
  private User target;

  @BeforeEach
  void setUp() {
    requester = new User(new UserId(UUID.randomUUID()), new Name("John", "Doe"),
        new Email("john@example.com"));
    target = new User(new UserId(UUID.randomUUID()), new Name("Jane", "Smith"),
        new Email("jane@example.com"));
  }

  @Test
  @DisplayName("sendFriendRequest should add request to both users when valid")
  void sendFriendRequest_shouldAddRequestToBothUsersWhenValid() {
    // given
    UUID requesterId = requester.getId().id();
    UUID targetId = target.getId().id();

    when(userRepository.findById(new UserId(requesterId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(targetId))).thenReturn(Optional.of(target));

    // when
    userService.sendFriendRequest(requesterId, targetId);

    // then
    assertThat(requester.getSentFriendRequests()).contains(target.getId());
    assertThat(target.getReceivedFriendRequests()).contains(requester.getId());
    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  @Test
  @DisplayName("sendFriendRequest should throw SelfInteractionException when requester equals target")
  void sendFriendRequest_shouldThrowSelfInteractionExceptionWhenRequesterEqualsTarget() {
    // given
    UUID requesterId = requester.getId().id();

    // when & then
    assertThrows(SelfInteractionException.class,
        () -> userService.sendFriendRequest(requesterId, requesterId));
  }

  @Test
  @DisplayName("sendFriendRequest should throw RelationAlreadyExistsException when users are already friends")
  void sendFriendRequest_shouldThrowRelationAlreadyExistsExceptionWhenAlreadyFriends() {
    // given
    UUID requesterId = requester.getId().id();
    UUID targetId = target.getId().id();

    // simulate existing friendship
    requester.addFriend(target.getId());
    target.addFriend(requester.getId());

    when(userRepository.findById(new UserId(requesterId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(targetId))).thenReturn(Optional.of(target));

    // when & then
    assertThrows(RelationAlreadyExistsException.class,
        () -> userService.sendFriendRequest(requesterId, targetId));
  }

  @Test
  @DisplayName("sendFriendRequest should throw RequestAlreadySentException when request already sent")
  void sendFriendRequest_shouldThrowRequestAlreadySentExceptionWhenRequestAlreadySent() {
    // given
    UUID requesterId = requester.getId().id();
    UUID targetId = target.getId().id();

    // simulate request already sent
    requester.addSentFriendRequest(target.getId());

    when(userRepository.findById(new UserId(requesterId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(targetId))).thenReturn(Optional.of(target));

    // when & then
    assertThrows(RequestAlreadySentException.class,
        () -> userService.sendFriendRequest(requesterId, targetId));
  }

  @Test
  @DisplayName("acceptFriendRequest should establish friendship and remove requests")
  void acceptFriendRequest_shouldEstablishFriendshipAndRemoveRequests() {
    // given
    UUID approverId = target.getId().id();
    UUID requesterId = requester.getId().id();

    // simulate pending request
    requester.addSentFriendRequest(target.getId());
    target.addReceivedFriendRequest(requester.getId());

    when(userRepository.findById(new UserId(approverId))).thenReturn(Optional.of(target));
    when(userRepository.findById(new UserId(requesterId))).thenReturn(Optional.of(requester));

    // when
    userService.acceptFriendRequest(approverId, requesterId);

    // then
    assertThat(target.getReceivedFriendRequests()).doesNotContain(requester.getId());
    assertThat(requester.getSentFriendRequests()).doesNotContain(target.getId());

    assertThat(target.getFriends()).contains(requester.getId());
    assertThat(requester.getFriends()).contains(target.getId());

    verify(userRepository).save(target);
    verify(userRepository).save(requester);
  }

  @Test
  @DisplayName("acceptFriendRequest should throw RequestNotExistsException when no request found")
  void acceptFriendRequest_shouldThrowRequestNotExistsExceptionWhenNoRequestFound() {
    // given
    UUID approverId = target.getId().id();
    UUID requesterId = requester.getId().id();

    when(userRepository.findById(new UserId(approverId))).thenReturn(Optional.of(target));
    when(userRepository.findById(new UserId(requesterId))).thenReturn(Optional.of(requester));

    // when & then
    assertThrows(RequestNotExistsException.class,
        () -> userService.acceptFriendRequest(approverId, requesterId));
  }

  @Test
  @DisplayName("rejectFriendRequest should remove requests and not establish friendship")
  void rejectFriendRequest_shouldRemoveRequestsAndNotEstablishFriendship() {
    // given
    UUID rejectorId = target.getId().id();
    UUID requesterId = requester.getId().id();

    // simulate pending request
    requester.addSentFriendRequest(target.getId());
    target.addReceivedFriendRequest(requester.getId());

    when(userRepository.findById(new UserId(rejectorId))).thenReturn(Optional.of(target));
    when(userRepository.findById(new UserId(requesterId))).thenReturn(Optional.of(requester));

    // when
    userService.rejectFriendRequest(rejectorId, requesterId);

    // then
    assertThat(target.getReceivedFriendRequests()).doesNotContain(requester.getId());
    assertThat(requester.getSentFriendRequests()).doesNotContain(target.getId());

    assertThat(target.getFriends()).doesNotContain(requester.getId());
    assertThat(requester.getFriends()).doesNotContain(target.getId());

    verify(userRepository).save(target);
    verify(userRepository).save(requester);
  }

  @Test
  @DisplayName("removeFriend should remove users from each others friend lists")
  void removeFriend_shouldRemoveUsersFromEachOthersFriendLists() {
    // given
    UUID initiatorId = requester.getId().id();
    UUID friendId = target.getId().id();

    // simulate friendship
    requester.addFriend(target.getId());
    target.addFriend(requester.getId());

    when(userRepository.findById(new UserId(initiatorId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(friendId))).thenReturn(Optional.of(target));

    // when
    userService.removeFriend(initiatorId, friendId);

    // then
    assertThat(requester.getFriends()).doesNotContain(target.getId());
    assertThat(target.getFriends()).doesNotContain(requester.getId());

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }


  @Test
  @DisplayName("followUser should add following relation")
  void followUser_shouldAddFollowingRelation() {
    // given
    UUID followerId = requester.getId().id();
    UUID followedId = target.getId().id();

    when(userRepository.findById(new UserId(followerId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(followedId))).thenReturn(Optional.of(target));

    // when
    userService.followUser(followerId, followedId);

    // then
    assertThat(requester.getFollowing()).contains(target.getId());
    assertThat(target.getFollowers()).contains(requester.getId());

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  @Test
  @DisplayName("followUser should throw SelfInteractionException when user tries to follow self")
  void followUser_shouldThrowSelfInteractionExceptionWhenUserTriesToFollowSelf() {
    // given
    UUID followerId = requester.getId().id();

    // when & then
    assertThrows(SelfInteractionException.class,
        () -> userService.followUser(followerId, followerId));
  }

  @Test
  @DisplayName("followUser should throw AlreadyFollowingException when relation already exists")
  void followUser_shouldThrowAlreadyFollowingExceptionWhenRelationAlreadyExists() {
    // given
    UUID followerId = requester.getId().id();
    UUID followedId = target.getId().id();

    requester.addFollowing(target.getId()); // Simulate existing relation

    when(userRepository.findById(new UserId(followerId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(followedId))).thenReturn(Optional.of(target));

    // when & then
    assertThrows(AlreadyFollowingException.class,
        () -> userService.followUser(followerId, followedId));
  }


  @Test
  @DisplayName("unfollowUser should remove following relation")
  void unfollowUser_shouldRemoveFollowingRelation() {
    // given
    UUID followerId = requester.getId().id();
    UUID followedId = target.getId().id();

    requester.addFollowing(target.getId());
    target.addFollower(requester.getId());

    when(userRepository.findById(new UserId(followerId))).thenReturn(Optional.of(requester));
    when(userRepository.findById(new UserId(followedId))).thenReturn(Optional.of(target));

    // when
    userService.unfollowUser(followerId, followedId);

    // then
    assertThat(requester.getFollowing()).doesNotContain(target.getId());
    assertThat(target.getFollowers()).doesNotContain(requester.getId());

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }
}
