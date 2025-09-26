package com.grzegorzkartasiewicz.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostEventsIntegrationTest {

    @Autowired
    private PostFacade postFacade;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final int postToDeleteId = 301;
    private final int remainingPostId = 302;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (101, 'Test', 'User', 30)");
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (?, 'Post to be deleted', 101)", postToDeleteId);
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (?, 'Another post', 101)", remainingPostId);
        jdbcTemplate.update("INSERT INTO COMMENTS (ID, DESCRIPTION, POST_ID, USER_ID) VALUES (1, 'Comment 1 for deleted post', ?, 101)", postToDeleteId);
        jdbcTemplate.update("INSERT INTO COMMENTS (ID, DESCRIPTION, POST_ID, USER_ID) VALUES (2, 'Comment 2 for deleted post', ?, 101)", postToDeleteId);
        jdbcTemplate.update("INSERT INTO COMMENTS (ID, DESCRIPTION, POST_ID, USER_ID) VALUES (3, 'Comment for another post', ?, 101)", remainingPostId);
    }

    @Test
    @DisplayName("should delete post and its comments after PostDeletedEvent")
    void shouldDeletePostAndAllItsComments() {
        // given
        Integer commentsBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM COMMENTS WHERE POST_ID = ?", Integer.class, postToDeleteId);
        assertThat(commentsBefore).isEqualTo(2);

        // when
        postFacade.deletePost(postToDeleteId);

        // then
        assertThat(postRepository.findById(postToDeleteId)).isNotPresent();

        Integer commentsAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM COMMENTS WHERE POST_ID = ?", Integer.class, postToDeleteId);
        assertThat(commentsAfter).isZero();

        assertThat(postRepository.findById(remainingPostId)).isPresent();
        Integer remainingComments = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM COMMENTS", Integer.class);
        assertThat(remainingComments).isEqualTo(1);
    }
}
