package ru.mdemidkin.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.mdemidkin.exception.EntityNotFoundException;
import ru.mdemidkin.exception.ImageNotFoundException;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.model.Paging;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.repository.api.PostRepository;
import ru.mdemidkin.service.impl.PostServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository repository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void createPost_PostObject_shouldSaveAndReturnPost() {
        Post post = Post.builder().title("тестовый титул").text("тестовый текст").build();

        when(repository.save(post)).thenAnswer(invocation -> {
            Post saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Post result = postService.createPost(post);
        assertNotNull(result.getId());
        assertEquals("тестовый титул", result.getTitle());
    }

    @Test
    void getPostById_existingPost_shouldReturnPost() {
        Post post = Post.builder().id(1L).title("тестовый титул").build();

        when(repository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(1L);
        assertEquals("тестовый титул", result.getTitle());
    }

    @Test
    void getPostById_nonExistingPost_shouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> postService.getPostById(1L));

        assertTrue(ex.getMessage().contains("не найден пост с id: 1"));
    }

    @Test
    void getPosts_shouldReturnListOfPosts() {
        Post post1 = Post.builder().id(1L).build();
        Post post2 = Post.builder().id(2L).build();

        when(repository.findPosts("", 10, 1)).thenReturn(Arrays.asList(post1, post2));

        assertEquals(2, postService.getPosts("", 10, 1).size());
    }

    @Test
    void getPaging_shouldReturnCorrectPaging() {
        when(repository.countPosts("")).thenReturn(25L);

        Paging paging = postService.getPaging("", 10, 2);

        assertTrue(paging.hasNext());
        assertTrue(paging.hasPrevious());
        assertEquals(2, paging.pageNumber());
        assertEquals(10, paging.pageSize());
    }

    @Test
    @SneakyThrows
    void createPost_withImageAndTags_shouldSavePostWithImageAndTags() {
        String title = "новый пост";
        String text = "новый текст";
        String tags = "tag1, tag2";
        byte[] imageBytes = "картинка".getBytes();
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", imageBytes);

        when(repository.save(any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(3L);
            return p;
        });

        Post result = postService.createPost(title, text, image, tags);
        assertNotNull(result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(text, result.getText());
        assertArrayEquals(imageBytes, result.getImageData());

        Set<String> expectedTags = new HashSet<>(Arrays.asList("tag1", "tag2"));
        assertEquals(expectedTags, result.getTags());
    }

    @Test
    @SneakyThrows
    void createPost_withEmptyImageAndEmptyTags_shouldSavePostWithoutImageAndTags() {
        String title = "пост без картинки";
        String text = "текст";
        String tags = "";
        MultipartFile image = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        when(repository.save(any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(4L);
            return p;
        });

        Post result = postService.createPost(title, text, image, tags);
        assertNotNull(result.getId());
        assertNull(result.getImageData());
        assertTrue(result.getTags() == null || result.getTags().isEmpty());
    }

    @Test
    void getPostImage_existingImage_shouldReturnResource() {
        byte[] imageBytes = "картинка".getBytes();
        Post post = Post.builder().id(1L).imageData(imageBytes).build();

        when(repository.findById(1L)).thenReturn(Optional.of(post));

        ByteArrayResource resource = postService.getPostImage(1L);
        assertArrayEquals(imageBytes, resource.getByteArray());
    }

    @Test
    void getPostImage_noImage_shouldThrowException() {
        Post post = Post.builder().id(1L).imageData(null).build();

        when(repository.findById(1L)).thenReturn(Optional.of(post));

        Exception ex = assertThrows(ImageNotFoundException.class, () -> postService.getPostImage(1L));
        assertTrue(ex.getMessage().contains("не найдено изображение для поста с id: 1"));
    }

    @Test
    void updateLikes_increaseLikes_shouldIncrementLikes() {
        Post post = Post.builder().id(1L).likesCount(5).build();

        when(repository.findById(1L)).thenReturn(Optional.of(post));
        when(repository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.updateLikes(1L, true);
        assertEquals(6, post.getLikesCount());
    }

    @Test
    void updateLikes_decreaseLikes_shouldDecrementLikes() {
        Post post = Post.builder().id(1L).likesCount(5).build();

        when(repository.findById(1L)).thenReturn(Optional.of(post));
        when(repository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.updateLikes(1L, false);
        assertEquals(4, post.getLikesCount());
    }

    @Test
    @SneakyThrows
    void updatePost_shouldUpdatePostFields() {
        Post post = Post.builder()
                .id(1L)
                .title("старый текст")
                .text("старый текст")
                .imageData(null)
                .build();

        byte[] newImageBytes = "картинка".getBytes();
        MultipartFile image = new MockMultipartFile("image", "new.jpg", "image/jpeg", newImageBytes);

        when(repository.findById(1L)).thenReturn(Optional.of(post));
        when(repository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.updatePost(1L, "новый текст", "новый текст", image, "tag3, tag4");

        assertEquals("новый текст", post.getTitle());
        assertEquals("новый текст", post.getText());
        assertArrayEquals(newImageBytes, post.getImageData());
        Set<String> expectedTags = new HashSet<>(Arrays.asList("tag3", "tag4"));
        assertEquals(expectedTags, post.getTags());
    }

    @Test
    void addComment_shouldAddCommentToPost() {
        Post post = Post.builder().id(1L).comments(new ArrayList<>()).build();
        Comment comment = Comment.builder().id(1L).postId(1L).text("текст").build();

        when(repository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        postService.addComment(1L, "текст");

        assertEquals(1, post.getComments().size());
        assertEquals("текст", post.getComments().getFirst().getText());
    }

    @Test
    void updateComment_shouldUpdateComment() {
        Comment comment = Comment.builder().id(1L).postId(1L).text("текст").build();

        when(commentRepository.findById(1L)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        postService.updateComment(1L, 1L, "новый коммент");

        assertEquals("новый коммент", comment.getText());
    }

    @Test
    void deleteComment_shouldDeleteComment() {
        Comment comment = Comment.builder().id(1L).postId(1L).build();

        when(commentRepository.findById(1L)).thenReturn(comment);

        postService.deleteComment(1L, 1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deletePost_shouldDeletePost() {
        postService.deletePost(1L);
        verify(repository).deleteById(1L);
    }
}
