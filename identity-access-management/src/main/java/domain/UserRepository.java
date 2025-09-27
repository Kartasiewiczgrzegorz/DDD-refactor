package domain;

public interface UserRepository {

  User save(User signedUser);

  User findUserById(UserId verifiedUserId);

  void delete(User user);

  User findUserByEmail(Email email);
}
