package com.grzegorzkartasiewicz.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SqlLoginRepositoryTest {

    @Autowired
    private SqlLoginRepository loginRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (100, 'Test', 'User', 30)");

        jdbcTemplate.update("INSERT INTO LOGINS (ID, NICK, PASSWORD, EMAIL, USER_ID) VALUES (1, 'testUser', 'password', 'test@test.com', 100)");
    }

    @Test
    @DisplayName("should find login by existing nick")
    void shouldFindLoginByExistingNick() {
        // when
        Optional<LoginSnapshot> found = loginRepository.findByNick("testUser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNick()).isEqualTo("testUser");
        assertThat(found.get().getUserId().id()).isEqualTo(100);
    }

    @Test
    @DisplayName("should not find login by non-existing nick")
    void shouldNotFindLoginByNonExistingNick() {
        // when
        Optional<LoginSnapshot> found = loginRepository.findByNick("nonExistentUser");

        // then
        assertThat(found).isNotPresent();
    }
}
