package adapters;

import domain.Email;
import domain.User;
import domain.UserId;
import domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
  private final SqlUserRepository repository;

  @Override
  public User save(User signedUser) {
    return repository.save(signedUser);
  }

  @Override
  public User findUserById(UserId verifiedUserId) {
    return repository.findUserById(verifiedUserId);
  }

  @Override
  public void delete(User user) {
    repository.delete(user);
  }

  @Override
  public User findUserByEmail(Email email) {
    return repository.findByEmail(email);
  }
}
