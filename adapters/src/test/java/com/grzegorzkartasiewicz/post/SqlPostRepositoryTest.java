package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.post.PostSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SqlPostRepositoryTest {

    @Autowired
    private SqlPostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 1. Wstawiamy użytkownika
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (101, 'Test', 'User', 30)");

        // 2. Wstawiamy posty powiązane z tym użytkownikiem
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (1, 'This is a test about Java', 101)");
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (2, 'Another TEST post', 101)");
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (3, 'A post about something else', 101)");
    }

    @Test
    @DisplayName("should find posts containing query, ignoring case")
    void shouldFindPostsContainingQueryIgnoringCase() {
        // when
        List<PostSnapshot> results = postRepository.findAllByDescriptionContainingIgnoreCase("test");

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(PostSnapshot::getDescription)
                .containsExactlyInAnyOrder("This is a test about Java", "Another TEST post");
    }

    @Test
    @DisplayName("should return empty list for non-matching query")
    void shouldReturnEmptyListForNonMatchingQuery() {
        // when
        List<PostSnapshot> results = postRepository.findAllByDescriptionContainingIgnoreCase("python");

        // then
        assertThat(results).isEmpty();
    }
}
