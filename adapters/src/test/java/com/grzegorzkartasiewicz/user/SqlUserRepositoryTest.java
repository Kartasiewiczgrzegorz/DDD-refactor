package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.user.UserSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SqlUserRepositoryTest {

    @Autowired
    private SqlUserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (1, 'John', 'Doe', 30)");
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (2, 'Jane', 'Smith', 25)");
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (3, 'Johnny', 'Bravo', 40)");
    }

    @Test
    @DisplayName("should find users by name containing query")
    void shouldFindUsersByNameContainingQuery() {
        // when
        List<UserSnapshot> results = userRepository.findAllByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("john", "john");

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(UserSnapshot::getName).containsExactlyInAnyOrder("John", "Johnny");
    }

    @Test
    @DisplayName("should find users by surname containing query")
    void shouldFindUsersBySurnameContainingQuery() {
        // when
        List<UserSnapshot> results = userRepository.findAllByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("ith", "ith");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSurname()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("should return empty list if no user matches query")
    void shouldReturnEmptyListIfNoUserMatchesQuery() {
        // when
        List<UserSnapshot> results = userRepository.findAllByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("nonexistent", "nonexistent");

        // then
        assertThat(results).isEmpty();
    }
}
