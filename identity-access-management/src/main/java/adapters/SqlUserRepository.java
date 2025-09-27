package adapters;

import domain.Email;
import domain.User;
import domain.UserId;
import domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.Repository;

public interface SqlUserRepository extends Repository<User, Long> {

  User save(User signedUser);

  User findUserById(UserId id);

  void delete(User user);

  User findByEmail(Email email);
}

