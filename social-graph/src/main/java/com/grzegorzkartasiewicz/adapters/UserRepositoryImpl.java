package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.SocialUser;
import com.grzegorzkartasiewicz.domain.UserRepository;
import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserRepositoryImpl implements UserRepository {

  private final SqlUserRepository sqlUserRepository;

  @Override
  public SocialUser save(SocialUser socialUser) {
    return sqlUserRepository.save(SocialUserEntity.fromDomain(socialUser)).toDomain();
  }

  @Override
  public Optional<SocialUser> findById(UserId id) {
    return sqlUserRepository.findById(id.id())
        .map(SocialUserEntity::toDomain);
  }

  @Override
  public List<SocialUser> findAllByIds(Collection<UserId> ids) {
    return sqlUserRepository.findAllByIdIn(ids.stream().map(UserId::id).toList())
        .stream()
        .map(SocialUserEntity::toDomain)
        .toList();
  }
}
