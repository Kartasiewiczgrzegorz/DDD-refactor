package com.grzegorzkartasiewicz.domain;

import com.grzegorzkartasiewicz.domain.vo.UserId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  Optional<SocialUser> findById(UserId userId);

  List<SocialUser> findAllByIds(Collection<UserId> ids);

  SocialUser save(SocialUser socialUserToSave);
}
