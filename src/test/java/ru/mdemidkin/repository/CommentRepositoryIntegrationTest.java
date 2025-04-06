package ru.mdemidkin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.mdemidkin.config.DataSourceConfiguration;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.repository.impl.CommentRepositoryImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, CommentRepositoryImpl.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void testInsertAndFindById() {
        Comment comment = new Comment();
        comment.setPostId(1L);
        comment.setText("Первый комментарий");

        Comment saved = commentRepository.save(comment);
        assertNotNull(saved.getId());

        Comment found = commentRepository.findById(saved.getId());
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

        Comment found = commentRepository.findById(updated.getId());
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
        Comment found = commentRepository.findById(id);

        assertNotNull(id);
        assertNull(found);
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
