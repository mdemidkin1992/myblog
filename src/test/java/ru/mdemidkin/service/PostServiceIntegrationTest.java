package ru.mdemidkin.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.mdemidkin.exception.EntityNotFoundException;
import ru.mdemidkin.exception.ImageNotFoundException;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.model.Paging;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.repository.api.PostRepository;
import ru.mdemidkin.service.api.PostService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void createPost_PostObject_shouldSaveAndReturnPost() {
        Post post = Post.builder().title("тестовый титул").text("тестовый текст").build();
        Post saved = postService.createPost(post);
        assertNotNull(saved.getId());
        assertEquals("тестовый титул", saved.getTitle());

        Optional<Post> byId = postRepository.findById(saved.getId());
        assertTrue(byId.isPresent());
    }

    @Test
    void getPostById_existingPost_shouldReturnPost() {
        Post post = Post.builder().title("тестовый титул").text("тестовый текст").build();
        Post saved = postRepository.save(post);

        Post result = postService.getPostById(saved.getId());
        assertEquals("тестовый титул", result.getTitle());
    }

    @Test
    void getPostById_nonExistingPost_shouldThrowException() {
        Long nonExistingId = 999L;
        Exception ex = assertThrows(EntityNotFoundException.class, () -> postService.getPostById(nonExistingId));
        assertTrue(ex.getMessage().contains("не найден пост с id: " + nonExistingId));
    }

    @Test
    void getPosts_shouldReturnListOfPosts() {
        Post post1 = Post.builder().title("Пост 1").build();
        Post post2 = Post.builder().title("Пост 2").build();
        postRepository.save(post1);
        postRepository.save(post2);

        var posts = postService.getPosts("", 10, 1);
        assertTrue(posts.size() >= 2);
    }

    @Test
    void getPaging_shouldReturnCorrectPaging() {
        for (int i = 0; i < 25; i++) {
            postRepository.save(Post.builder().title("Пост " + i).build());
        }
        Paging paging = postService.getPaging("", 10, 2);
        assertTrue(paging.hasNext());
        assertTrue(paging.hasPrevious());
        assertEquals(2, paging.pageNumber());
        assertEquals(10, paging.pageSize());
    }

    @Test
    void createPost_withImageAndTags_shouldSavePostWithImageAndTags() {
        String title = "новый пост";
        String text = "новый текст";
        String tags = "tag1, tag2";
        byte[] imageBytes = "картинка".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", imageBytes);

        Post result = postService.createPost(title, text, image, tags);
        assertNotNull(result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(text, result.getText());
        assertArrayEquals(imageBytes, result.getImageData());

        Set<String> expectedTags = new HashSet<>(Arrays.asList("tag1", "tag2"));
        assertEquals(expectedTags, result.getTags());
    }

    @Test
    void createPost_withEmptyImageAndEmptyTags_shouldSavePostWithoutImageAndTags() {
        String title = "пост без картинки";
        String text = "текст";
        String tags = "";
        MockMultipartFile image = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        Post result = postService.createPost(title, text, image, tags);
        assertNotNull(result.getId());
        assertNull(result.getImageData());
        assertTrue(result.getTags() == null || result.getTags().isEmpty());
    }

    @Test
    void getPostImage_existingImage_shouldReturnResource() {
        byte[] imageBytes = "картинка".getBytes();
        Post post = Post.builder().title("Пост с изображением").imageData(imageBytes).build();
        Post saved = postRepository.save(post);

        ByteArrayResource resource = postService.getPostImage(saved.getId());
        assertArrayEquals(imageBytes, resource.getByteArray());
    }

    @Test
    void getPostImage_noImage_shouldThrowException() {
        Post post = Post.builder().title("Пост без изображения").build();
        Post saved = postRepository.save(post);

        Exception ex = assertThrows(ImageNotFoundException.class, () -> postService.getPostImage(saved.getId()));
        assertTrue(ex.getMessage().contains("не найдено изображение для поста с id: " + saved.getId()));
    }

    @Test
    void updateLikes_increaseLikes_shouldIncrementLikes() {
        Post post = Post.builder().title("Пост для лайков").likesCount(5).build();
        Post saved = postRepository.save(post);

        postService.updateLikes(saved.getId(), true);
        Post updated = postRepository.findById(saved.getId()).orElseThrow();
        assertEquals(6, updated.getLikesCount());
    }

    @Test
    void updateLikes_decreaseLikes_shouldDecrementLikes() {
        Post post = Post.builder().title("Пост для лайков").likesCount(5).build();
        Post saved = postRepository.save(post);

        postService.updateLikes(saved.getId(), false);
        Post updated = postRepository.findById(saved.getId()).orElseThrow();
        assertEquals(4, updated.getLikesCount());
    }

    @Test
    void updatePost_shouldUpdatePostFields() {
        Post post = Post.builder().title("старый заголовок").text("старый текст").build();
        Post saved = postRepository.save(post);

        byte[] newImageBytes = "новая картинка".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", "new.jpg", "image/jpeg", newImageBytes);

        postService.updatePost(saved.getId(), "новый заголовок", "новый текст", image, "tag3, tag4");

        Post updated = postRepository.findById(saved.getId()).orElseThrow();
        assertEquals("новый заголовок", updated.getTitle());
        assertEquals("новый текст", updated.getText());
        assertArrayEquals(newImageBytes, updated.getImageData());
        Set<String> expectedTags = new HashSet<>(Arrays.asList("tag3", "tag4"));
        assertEquals(expectedTags, updated.getTags());
    }

    @Test
    void addComment_shouldAddCommentToPost() {
        Post post = Post.builder().title("Пост с комментарием").build();
        Post saved = postRepository.save(post);

        postService.addComment(saved.getId(), "текст комментария");
        Post updated = postRepository.findById(saved.getId()).orElseThrow();

        assertFalse(updated.getComments().isEmpty());
        assertEquals("текст комментария", updated.getComments().get(0).getText());
    }

    @Test
    void updateComment_shouldUpdateComment() {
        Comment comment = Comment.builder().postId(1L).text("старый комментарий").build();
        comment = commentRepository.save(comment);

        postService.updateComment(comment.getPostId(), comment.getId(), "новый комментарий");
        Comment updated = commentRepository.findById(comment.getId()).get();
        assertEquals("новый комментарий", updated.getText());
    }

    @Test
    void deleteComment_shouldDeleteComment() {
        Comment comment = Comment.builder().postId(1L).build();
        comment = commentRepository.save(comment);

        postService.deleteComment(comment.getPostId(), comment.getId());
        assertTrue(commentRepository.findById(comment.getId()).isEmpty());
    }

    @Test
    void deletePost_shouldDeletePost() {
        Post post = Post.builder().title("Пост для удаления").build();
        Post saved = postRepository.save(post);

        postService.deletePost(saved.getId());
        assertTrue(postRepository.findById(saved.getId()).isEmpty());
    }
}
