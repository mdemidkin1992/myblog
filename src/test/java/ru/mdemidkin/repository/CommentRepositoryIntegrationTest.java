package ru.mdemidkin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.repository.impl.CommentRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Sql({"/schema-test.sql", "/data-test.sql"})
@Import(CommentRepositoryImpl.class)
class CommentRepositoryIntegrationTest {

    @Autowired
    private CommentRepositoryImpl commentRepository;

    @Test
    void testInsertAndFindById() {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setText("Первый комментарий");

        Comment saved = commentRepository.save(comment);
        assertNotNull(saved.getId());

        Comment found = commentRepository.findById(saved.getId()).get();
        assertNotNull(found);
        assertEquals("Первый комментарий", found.getText());
        assertEquals(1L, found.getPostId());
    }

    @Test
    void testUpdateComment() {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setText("Старый комментарий");

        Comment saved = commentRepository.save(comment);
        saved.setText("Обновленный комментарий");
        Comment updated = commentRepository.save(saved);

        Comment found = commentRepository.findById(updated.getId()).get();
        assertEquals("Обновленный комментарий", found.getText());
    }

    @Test
    void testFindByPostId() {
        Comment comment1 = new Comment();
        comment1.setPostId(2L);
        comment1.setText("Комментарий 1 для поста 2");

        Comment comment2 = new Comment();
        comment2.setPostId(2L);
        comment2.setText("Комментарий 2 для поста 2");

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        List<Comment> comments = commentRepository.findByPostId(2L);

        assertEquals(3, comments.size());
    }

    @Test
    void testDeleteById() {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setText("Комментарий для удаления");
        Comment saved = commentRepository.save(comment);
        Long id = saved.getId();

        commentRepository.deleteById(id);
        Optional<Comment> found = commentRepository.findById(id);

        assertNotNull(id);
        assertTrue(found.isEmpty());
    }

    @Test
    void testDeleteByPostId() {
        Comment comment1 = new Comment();
        comment1.setPostId(2L);
        comment1.setText("Комментарий 1 для поста 3");
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setPostId(2L);
        comment2.setText("Комментарий 2 для поста 3");
        commentRepository.save(comment2);

        List<Comment> commentsBefore = commentRepository.findByPostId(2L);
        assertEquals(3, commentsBefore.size());

        commentRepository.deleteByPostId(2L);
        List<Comment> commentsAfter = commentRepository.findByPostId(2L);
        assertTrue(commentsAfter.isEmpty());
    }

}
