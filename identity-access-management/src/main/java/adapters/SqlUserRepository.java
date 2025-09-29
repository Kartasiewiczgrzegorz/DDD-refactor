package adapters;

import domain.Email;
import domain.User;
import domain.UserId;
import domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.Repository;

public interface SqlUserRepository extends Repository<UserEntity, Long> {

  UserEntity save(UserEntity signedUser);

  UserEntity findUserById(UserId id);

  void deleteById(UserId userId);

  UserEntity findByEmail(Email email);
}

