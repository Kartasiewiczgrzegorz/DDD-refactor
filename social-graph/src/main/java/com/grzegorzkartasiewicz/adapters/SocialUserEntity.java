package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.SocialUser;
import com.grzegorzkartasiewicz.domain.vo.Email;
import com.grzegorzkartasiewicz.domain.vo.Followed;
import com.grzegorzkartasiewicz.domain.vo.Follower;
import com.grzegorzkartasiewicz.domain.vo.Friend;
import com.grzegorzkartasiewicz.domain.vo.FriendRequest;
import com.grzegorzkartasiewicz.domain.vo.Name;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_profiles")
@AllArgsConstructor
@NoArgsConstructor
class SocialUserEntity {

  @Id
  private UUID id;

  @Convert(converter = NameConverter.class)
  private Name name;

  @Convert(converter = EmailConverter.class)
  private Email email;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "social_friends", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "friend_id")
  private Set<UUID> friends = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "social_followers", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "follower_id")
  private Set<UUID> followers = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "social_followed", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "followed_id")
  private Set<UUID> followedUsers = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "social_sent_requests", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "target_id")
  private Set<UUID> sentRequests = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "social_received_requests", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "requester_id")
  private Set<UUID> receivedRequests = new HashSet<>();

  static SocialUserEntity fromDomain(SocialUser socialUser) {
    return new SocialUserEntity(
        socialUser.getId().id(),
        socialUser.getName(),
        socialUser.getEmail(),
        socialUser.getFriends().stream().map(Friend::friendId).map(UserId::id)
            .collect(Collectors.toSet()),
        socialUser.getFollowers().stream().map(Follower::followerId).map(UserId::id)
            .collect(Collectors.toSet()),
        socialUser.getFollowedUsers().stream().map(Followed::followedId).map(UserId::id)
            .collect(Collectors.toSet()),
        socialUser.getSentFriendRequests().stream().map(FriendRequest::friendRequestId)
            .map(UserId::id)
            .collect(Collectors.toSet()),
        socialUser.getReceivedFriendRequests().stream().map(FriendRequest::friendRequestId)
            .map(UserId::id).collect(Collectors.toSet())
    );
  }

  SocialUser toDomain() {
    return new SocialUser(
        new UserId(id),
        name,
        email,
        friends.stream().map(UserId::new).map(Friend::new).collect(Collectors.toSet()),
        followers.stream().map(UserId::new).map(Follower::new).collect(Collectors.toSet()),
        followedUsers.stream().map(UserId::new).map(Followed::new).collect(Collectors.toSet()),
        sentRequests.stream().map(UserId::new).map(FriendRequest::new).collect(Collectors.toSet()),
        receivedRequests.stream().map(UserId::new).map(FriendRequest::new)
            .collect(Collectors.toSet())
    );
  }
}
