package app;

import domain.User;
import domain.UserId;
import domain.UserRepository;
import domain.Verification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public RegisteredUser signIn(UserRegistrationRequest userRegistrationRequest) {
    //validation + creation
    User signedUser = new User(userRegistrationRequest);
    userRepository.save(signedUser);
    //TODO send verification email

    return new RegisteredUser(signedUser.getName().name(), signedUser.getName().surname(),
        signedUser.getEmail().email());
  }

  public void verifyUser(UserId verifiedUserId, Verification verification) {
    User user = userRepository.findUserById(verifiedUserId);
    if (verification.equals(Verification.VERIFIED)) {
      user.verify();
      userRepository.save(user);
    } else if (verification.equals(Verification.UNVERIFIED)) {
      userRepository.delete(user);
    }

  }
}
