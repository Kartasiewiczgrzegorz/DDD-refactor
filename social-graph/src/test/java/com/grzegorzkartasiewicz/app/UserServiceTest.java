package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.AlreadyFollowingException;
import com.grzegorzkartasiewicz.domain.RelationAlreadyExistsException;
import com.grzegorzkartasiewicz.domain.RequestAlreadySentException;
import com.grzegorzkartasiewicz.domain.RequestNotExistsException;
import com.grzegorzkartasiewicz.domain.SelfInteractionException;
import com.grzegorzkartasiewicz.domain.User;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Followed;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.FriendRequest;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
  private UserId requesterId;
  private UserId targetId;

  @BeforeEach
  void setUp() {
    requesterId = new UserId(UUID.randomUUID());
    targetId = new UserId(UUID.randomUUID());
    requester = createUser(requesterId);
    target = createUser(targetId);
  }

  private User createUser(UserId id) {
    return new User(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  private User createUserWithFriend(UserId id, UserId friendId) {
    Set<Friend> friends = new HashSet<>();
    friends.add(new Friend(friendId));
    return new User(id, new Name("Test", "User"), new Email("test@example.com"),
        friends, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  private User createUserWithSentRequest(UserId id, UserId targetId) {
    Set<FriendRequest> requests = new HashSet<>();
    requests.add(new FriendRequest(targetId));
    return new User(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), requests, new HashSet<>());
  }

  private User createUserWithReceivedRequest(UserId id, UserId requesterId) {
    Set<FriendRequest> requests = new HashSet<>();
    requests.add(new FriendRequest(requesterId));
    return new User(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), requests);
  }

  private User createUserFollowing(UserId id, UserId targetId) {
    Set<Followed> followedUsers = new HashSet<>();
    followedUsers.add(new Followed(targetId));
    return new User(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), followedUsers, new HashSet<>(), new HashSet<>());
  }

  private User createUserFollowedBy(UserId id, UserId followerId) {
    Set<Follower> followers = new HashSet<>();
    followers.add(new Follower(followerId));
    return new User(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), followers, new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  @Test
  @DisplayName("sendFriendRequest should add request to both users when valid")
  void sendFriendRequest_shouldAddRequestToBothUsersWhenValid() {
    // given
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    userService.sendFriendRequest(new FriendRequestSendingRequest(requesterId.id(), targetId.id()));

    // then
    assertThat(requester.getSentFriendRequests()).extracting(FriendRequest::friendRequestId)
        .contains(targetId);
    assertThat(target.getReceivedFriendRequests()).extracting(FriendRequest::friendRequestId)
        .contains(requesterId);
    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  @Test
  @DisplayName("sendFriendRequest should throw SelfInteractionException when requester equals target")
  void sendFriendRequest_shouldThrowSelfInteractionExceptionWhenRequesterEqualsTarget() {
    FriendRequestSendingRequest request = new FriendRequestSendingRequest(requesterId.id(),
        requesterId.id());
    assertThrows(SelfInteractionException.class,
        () -> userService.sendFriendRequest(request));
  }

  @Test
  @DisplayName("sendFriendRequest should throw RelationAlreadyExistsException when already friends")
  void sendFriendRequest_shouldThrowRelationAlreadyExistsExceptionWhenAlreadyFriends() {
    // given
    requester = createUserWithFriend(requesterId, targetId);
    target = createUserWithFriend(targetId, requesterId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when & then
    FriendRequestSendingRequest request = new FriendRequestSendingRequest(requesterId.id(),
        targetId.id());
    assertThrows(RelationAlreadyExistsException.class,
        () -> userService.sendFriendRequest(request));
  }

  @Test
  @DisplayName("sendFriendRequest should throw RequestAlreadySentException when request already sent")
  void sendFriendRequest_shouldThrowRequestAlreadySentExceptionWhenRequestAlreadySent() {
    // given
    requester = createUserWithSentRequest(requesterId, targetId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when & then
    FriendRequestSendingRequest request = new FriendRequestSendingRequest(requesterId.id(),
        targetId.id());
    assertThrows(RequestAlreadySentException.class,
        () -> userService.sendFriendRequest(request));
  }

  @Test
  @DisplayName("acceptFriendRequest should establish friendship and remove requests")
  void acceptFriendRequest_shouldEstablishFriendshipAndRemoveRequests() {
    // given
    requester = createUserWithSentRequest(requesterId, targetId);
    target = createUserWithReceivedRequest(targetId, requesterId);

    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));

    // when
    userService.acceptFriendRequest(
        new FriendRequestAcceptanceRequest(targetId.id(), requesterId.id()));

    // then
    assertThat(target.getReceivedFriendRequests()).isEmpty();
    assertThat(requester.getSentFriendRequests()).isEmpty();

    assertThat(target.getFriends()).extracting(Friend::friendId).contains(requesterId);
    assertThat(requester.getFriends()).extracting(Friend::friendId).contains(targetId);

    verify(userRepository).save(target);
    verify(userRepository).save(requester);
  }

  @Test
  @DisplayName("acceptFriendRequest should throw RequestNotExistsException when no request found")
  void acceptFriendRequest_shouldThrowRequestNotExistsExceptionWhenNoRequestFound() {
    // given
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));

    // when & then
    FriendRequestAcceptanceRequest request = new FriendRequestAcceptanceRequest(targetId.id(),
        requesterId.id());
    assertThrows(RequestNotExistsException.class,
        () -> userService.acceptFriendRequest(request));
  }

  @Test
  @DisplayName("rejectFriendRequest should remove requests and not establish friendship")
  void rejectFriendRequest_shouldRemoveRequestsAndNotEstablishFriendship() {
    // given
    requester = createUserWithSentRequest(requesterId, targetId);
    target = createUserWithReceivedRequest(targetId, requesterId);

    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));

    // when
    userService.rejectFriendRequest(
        new FriendRequestRejectionRequest(targetId.id(), requesterId.id()));

    // then
    assertThat(target.getReceivedFriendRequests()).isEmpty();
    assertThat(requester.getSentFriendRequests()).isEmpty();

    assertThat(target.getFriends()).isEmpty();
    assertThat(requester.getFriends()).isEmpty();

    verify(userRepository).save(target);
    verify(userRepository).save(requester);
  }

  @Test
  @DisplayName("removeFriend should remove users from each others friend lists")
  void removeFriend_shouldRemoveUsersFromEachOthersFriendLists() {
    // given
    requester = createUserWithFriend(requesterId, targetId);
    target = createUserWithFriend(targetId, requesterId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    userService.removeFriend(new FriendRemovalRequest(requesterId.id(), targetId.id()));

    // then
    assertThat(requester.getFriends()).doesNotContain(new Friend(targetId));
    assertThat(target.getFriends()).doesNotContain(new Friend(requesterId));

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  @Test
  @DisplayName("followUser should add following relation")
  void followUser_shouldAddFollowingRelation() {
    // given
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    userService.followUser(new FollowUserRequest(requesterId.id(), targetId.id()));

    // then
    assertThat(requester.getFollowedUsers()).extracting(Followed::followedId).contains(targetId);
    assertThat(target.getFollowers()).extracting(Follower::followerId).contains(requesterId);

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  @Test
  @DisplayName("followUser should throw SelfInteractionException when user tries to follow self")
  void followUser_shouldThrowSelfInteractionExceptionWhenUserTriesToFollowSelf() {
    FollowUserRequest request = new FollowUserRequest(requesterId.id(), requesterId.id());
    assertThrows(SelfInteractionException.class,
        () -> userService.followUser(request));
  }

  @Test
  @DisplayName("followUser should throw AlreadyFollowingException when relation already exists")
  void followUser_shouldThrowAlreadyFollowingExceptionWhenRelationAlreadyExists() {
    // given
    requester = createUserFollowing(requesterId, targetId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when & then
    FollowUserRequest request = new FollowUserRequest(requesterId.id(), targetId.id());
    assertThrows(AlreadyFollowingException.class,
        () -> userService.followUser(request));
  }

  @Test
  @DisplayName("unfollowUser should remove following relation")
  void unfollowUser_shouldRemoveFollowingRelation() {
    // given
    requester = createUserFollowing(requesterId, targetId);
    target = createUserFollowedBy(targetId, requesterId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    userService.unfollowUser(new UnfollowUserRequest(requesterId.id(), targetId.id()));

    // then
    assertThat(requester.getFollowedUsers()).doesNotContain(new Followed(targetId));
    assertThat(target.getFollowers()).doesNotContain(new Follower(requesterId));

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }
}
