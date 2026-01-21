package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.AlreadyFollowingException;
import com.grzegorzkartasiewicz.domain.RelationAlreadyExistsException;
import com.grzegorzkartasiewicz.domain.RequestAlreadySentException;
import com.grzegorzkartasiewicz.domain.RequestNotExistsException;
import com.grzegorzkartasiewicz.domain.SelfInteractionException;
import com.grzegorzkartasiewicz.domain.SocialUser;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Followed;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.FriendRequest;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.HashSet;
import java.util.List;
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
class SocialServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private SocialService socialService;

  private SocialUser requester;
  private SocialUser target;
  private UserId requesterId;
  private UserId targetId;

  @BeforeEach
  void setUp() {
    requesterId = new UserId(UUID.randomUUID());
    targetId = new UserId(UUID.randomUUID());
    requester = createUser(requesterId);
    target = createUser(targetId);
  }

  private SocialUser createUser(UserId id) {
    return new SocialUser(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  private SocialUser createUserWithFriend(UserId id, UserId friendId) {
    Set<Friend> friends = new HashSet<>();
    friends.add(new Friend(friendId));
    return new SocialUser(id, new Name("Test", "User"), new Email("test@example.com"),
        friends, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  private SocialUser createUserWithSentRequest(UserId id, UserId targetId) {
    Set<FriendRequest> requests = new HashSet<>();
    requests.add(new FriendRequest(targetId));
    return new SocialUser(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), requests, new HashSet<>());
  }

  private SocialUser createUserWithReceivedRequest(UserId id, UserId requesterId) {
    Set<FriendRequest> requests = new HashSet<>();
    requests.add(new FriendRequest(requesterId));
    return new SocialUser(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), requests);
  }

  private SocialUser createUserFollowing(UserId id, UserId targetId) {
    Set<Followed> followedUsers = new HashSet<>();
    followedUsers.add(new Followed(targetId));
    return new SocialUser(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), new HashSet<>(), followedUsers, new HashSet<>(), new HashSet<>());
  }

  private SocialUser createUserFollowedBy(UserId id, UserId followerId) {
    Set<Follower> followers = new HashSet<>();
    followers.add(new Follower(followerId));
    return new SocialUser(id, new Name("Test", "User"), new Email("test@example.com"),
        new HashSet<>(), followers, new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  // UC1: Send Friend Request

  @Test
  @DisplayName("sendFriendRequest should add request to both users when valid")
  void sendFriendRequest_shouldAddRequestToBothUsersWhenValid() {
    // given
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    socialService.sendFriendRequest(
        new FriendRequestSendingRequest(requesterId.id(), targetId.id()));

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
        () -> socialService.sendFriendRequest(request));
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
        () -> socialService.sendFriendRequest(request));
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
        () -> socialService.sendFriendRequest(request));
  }

  // UC2: Accept Friend Request

  @Test
  @DisplayName("acceptFriendRequest should establish friendship and remove requests")
  void acceptFriendRequest_shouldEstablishFriendshipAndRemoveRequests() {
    // given
    requester = createUserWithSentRequest(requesterId, targetId);
    target = createUserWithReceivedRequest(targetId, requesterId);

    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));

    // when
    socialService.acceptFriendRequest(
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
        () -> socialService.acceptFriendRequest(request));
  }

  // UC3: Reject Friend Request

  @Test
  @DisplayName("rejectFriendRequest should remove requests and not establish friendship")
  void rejectFriendRequest_shouldRemoveRequestsAndNotEstablishFriendship() {
    // given
    requester = createUserWithSentRequest(requesterId, targetId);
    target = createUserWithReceivedRequest(targetId, requesterId);

    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));

    // when
    socialService.rejectFriendRequest(
        new FriendRequestRejectionRequest(targetId.id(), requesterId.id()));

    // then
    assertThat(target.getReceivedFriendRequests()).isEmpty();
    assertThat(requester.getSentFriendRequests()).isEmpty();

    assertThat(target.getFriends()).isEmpty();
    assertThat(requester.getFriends()).isEmpty();

    verify(userRepository).save(target);
    verify(userRepository).save(requester);
  }

  // UC4: Remove Friend

  @Test
  @DisplayName("removeFriend should remove users from each others friend lists")
  void removeFriend_shouldRemoveUsersFromEachOthersFriendLists() {
    // given
    requester = createUserWithFriend(requesterId, targetId);
    target = createUserWithFriend(targetId, requesterId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    socialService.removeFriend(new FriendRemovalRequest(requesterId.id(), targetId.id()));

    // then
    assertThat(requester.getFriends()).doesNotContain(new Friend(targetId));
    assertThat(target.getFriends()).doesNotContain(new Friend(requesterId));

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  // UC5: Follow User

  @Test
  @DisplayName("followUser should add following relation")
  void followUser_shouldAddFollowingRelation() {
    // given
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    socialService.followUser(new FollowUserRequest(requesterId.id(), targetId.id()));

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
        () -> socialService.followUser(request));
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
        () -> socialService.followUser(request));
  }

  // UC6: Unfollow User

  @Test
  @DisplayName("unfollowUser should remove following relation")
  void unfollowUser_shouldRemoveFollowingRelation() {
    // given
    requester = createUserFollowing(requesterId, targetId);
    target = createUserFollowedBy(targetId, requesterId);

    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

    // when
    socialService.unfollowUser(new UnfollowUserRequest(requesterId.id(), targetId.id()));

    // then
    assertThat(requester.getFollowedUsers()).doesNotContain(new Followed(targetId));
    assertThat(target.getFollowers()).doesNotContain(new Follower(requesterId));

    verify(userRepository).save(requester);
    verify(userRepository).save(target);
  }

  @Test
  @DisplayName("getFriends should return list of friends")
  void getFriends_shouldReturnListOfFriends() {
    // given
    requester = createUserWithFriend(requesterId, targetId);
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findAllByIds(any())).thenReturn(List.of(target));

    // when
    List<SocialUserResponse> friends = socialService.getFriends(requesterId.id());

    // then
    assertThat(friends).hasSize(1);
    assertThat(friends.get(0).id()).isEqualTo(targetId.id());
  }

  @Test
  @DisplayName("getFollowers should return list of followers")
  void getFollowers_shouldReturnListOfFollowers() {
    // given
    requester = createUserFollowedBy(requesterId, targetId);
    when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
    when(userRepository.findAllByIds(any())).thenReturn(List.of(target));

    // when
    List<SocialUserResponse> followers = socialService.getFollowers(requesterId.id());

    // then
    assertThat(followers).hasSize(1);
    assertThat(followers.get(0).id()).isEqualTo(targetId.id());
  }
}