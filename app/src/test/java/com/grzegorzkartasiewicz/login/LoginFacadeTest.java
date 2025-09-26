package com.grzegorzkartasiewicz.login;

import com.grzegorzkartasiewicz.DomainEventPublisher;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.UserFacade;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginFacadeTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private UserFacade userFacade;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private LoginFacade loginFacade;

    private Login login;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        var loginSnapshot = new LoginSnapshot(1, "testUser", "correctPass", "test@test.com", new UserId(10));
        login = Login.restore(loginSnapshot);

        userDTO = new UserDTO();
        userDTO.setId(10);
        userDTO.setName("Test");
        userDTO.setSurname("User");
    }

    @Test
    @DisplayName("should log in user with correct credentials")
    void shouldLogInUserWithCorrectCredentials() {
        // given
        when(loginRepository.findByNick("testUser")).thenReturn(Optional.of(login));
        when(userFacade.getUser(any(UserId.class))).thenReturn(userDTO);

        // when
        UserDTO result = loginFacade.logInUser("testUser", "correctPass");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userDTO.getId());
        assertThat(result.getName()).isEqualTo(userDTO.getName());
    }

    @Test
    @DisplayName("should not log in user with incorrect password")
    void shouldNotLogInUserWithIncorrectPassword() {
        // given
        when(loginRepository.findByNick("testUser")).thenReturn(Optional.of(login));

        // when
        UserDTO result = loginFacade.logInUser("testUser", "wrongPass");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should not log in non-existent user")
    void shouldNotLogInNonExistentUser() {
        // given
        when(loginRepository.findByNick(anyString())).thenReturn(Optional.empty());

        // when
        UserDTO result = loginFacade.logInUser("nonexistent", "anyPass");

        // then
        assertThat(result).isNull();
    }
}
