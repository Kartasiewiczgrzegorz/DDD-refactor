package com.grzegorzkartasiewicz.comment;

import com.grzegorzkartasiewicz.comment.CommentSnapshot;
import com.grzegorzkartasiewicz.post.vo.PostId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SqlCommentRepositoryTest {

    @Autowired
    private SqlCommentRepository commentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PostId postIdToDelete;
    private PostId otherPostId;

    @BeforeEach
    void setup() {
        // Wstawiamy dane za pomocą JDBC, aby zachować hermetyzację
        jdbcTemplate.update("INSERT INTO USERS (ID, NAME, SURNAME, AGE) VALUES (101, 'Test', 'User', 30)");
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (201, 'Post to delete comments from', 101)");
        jdbcTemplate.update("INSERT INTO POSTS (ID, DESCRIPTION, USER_ID) VALUES (202, 'Another post', 101)");

        postIdToDelete = new PostId(201);
        otherPostId = new PostId(202);

        jdbcTemplate.update("INSERT INTO COMMENTS (ID, DESCRIPTION, POST_ID, USER_ID) VALUES (1, 'Comment 1 for post 1', 201, 101)");
        jdbcTemplate.update("INSERT INTO COMMENTS (ID, DESCRIPTION, POST_ID, USER_ID) VALUES (2, 'Comment 2 for post 1', 201, 101)");
        jdbcTemplate.update("INSERT INTO COMMENTS (ID, DESCRIPTION, POST_ID, USER_ID) VALUES (3, 'Comment 1 for post 2', 202, 101)");
    }

    @Test
    @DisplayName("should delete all comments for a given post id")
    void shouldDeleteAllCommentsForGivenPostId() {
        // when
        commentRepository.deleteAllByPostId(postIdToDelete);

        // then
        List<CommentSnapshot> remainingComments = commentRepository.findAll();
        assertThat(remainingComments).hasSize(1);
        assertThat(remainingComments.get(0).getPostId()).isEqualTo(otherPostId);
    }
}
