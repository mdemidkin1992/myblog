package ru.mdemidkin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.mdemidkin.config.DataSourceConfiguration;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.repository.api.PostRepository;
import ru.mdemidkin.repository.impl.CommentRepositoryImpl;
import ru.mdemidkin.repository.impl.PostRepositoryImpl;
import ru.mdemidkin.repository.impl.TagRepositoryImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {
        DataSourceConfiguration.class,
        PostRepositoryImpl.class,
        CommentRepositoryImpl.class,
        TagRepositoryImpl.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void testInsertAndFindById() {
        Post post = new Post();
        post.setTitle("Integration Test Post");
        post.setText("Текст для интеграционного теста");
        post.setLikesCount(0);
        post.setImageData("dummy image".getBytes());
        Set<String> tags = new HashSet<>(Arrays.asList("java", "spring"));
        post.setTags(tags);

        Post saved = postRepository.save(post);
        Post found = postRepository.findById(saved.getId()).get();

        assertNotNull(saved.getId());
        assertEquals("Integration Test Post", found.getTitle());
        assertArrayEquals("dummy image".getBytes(), found.getImageData());
        assertNotNull(found.getTags());
        assertEquals(2, found.getTags().size());
        assertTrue(found.getTags().containsAll(tags));
    }

    @Test
    void testUpdatePost() {
        Post post = new Post();
        post.setTitle("Old Title");
        post.setText("Old Text");
        post.setLikesCount(0);
        post.setTags(new HashSet<>(Arrays.asList("oldTag")));
        Post saved = postRepository.save(post);

        saved.setTitle("New Title");
        saved.setText("New Text");
        saved.setImageData("new image".getBytes());
        saved.setLikesCount(5);

        Set<String> newTags = new HashSet<>(Arrays.asList("newTag1", "newTag2"));
        saved.setTags(newTags);

        Post updated = postRepository.save(saved);
        Optional<Post> foundOpt = postRepository.findById(updated.getId());
        assertTrue(foundOpt.isPresent());
        Post found = foundOpt.get();

        assertEquals("New Title", found.getTitle());
        assertEquals("New Text", found.getText());
        assertArrayEquals("new image".getBytes(), found.getImageData());
        assertEquals(5, found.getLikesCount());
        assertEquals(newTags, found.getTags());
    }

    @Test
    void testFindPostsAndCountPosts() {
        Post post1 = new Post();
        post1.setTitle("Post 1");
        post1.setText("Text 1");
        post1.setLikesCount(2);
        post1.setTags(new HashSet<>(Arrays.asList("java")));
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Post 2");
        post2.setText("Text 2");
        post2.setLikesCount(3);
        post2.setTags(new HashSet<>(Arrays.asList("spring")));
        postRepository.save(post2);

        List<Post> posts = postRepository.findPosts("", 10, 1);
        assertTrue(posts.size() >= 2);

        long total = postRepository.countPosts("");
        assertTrue(total >= 2);

        List<Post> javaPosts = postRepository.findPosts("java", 10, 1);
        assertEquals(1, javaPosts.size());
        assertEquals("Post 1", javaPosts.getFirst().getTitle());

        long javaCount = postRepository.countPosts("java");
        assertEquals(1, javaCount);
    }

    @Test
    void testDeletePost() {
        Post post = new Post();
        post.setTitle("To be deleted");
        post.setText("Some text");
        post.setLikesCount(0);
        post.setTags(new HashSet<>(Arrays.asList("temp")));
        Post saved = postRepository.save(post);
        Long id = saved.getId();

        postRepository.deleteById(id);

        Optional<Post> opt = postRepository.findById(id);
        assertFalse(opt.isPresent());
    }

    @Test
    void testCountPostsWithSearch() {
        Post post1 = new Post();
        post1.setTitle("Java Post");
        post1.setText("Text for Java post");
        post1.setLikesCount(1);
        post1.setTags(new HashSet<>(Arrays.asList("java")));
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Spring Post");
        post2.setText("Text for Spring post");
        post2.setLikesCount(1);
        post2.setTags(new HashSet<>(Arrays.asList("spring")));
        postRepository.save(post2);

        long javaCount = postRepository.countPosts("java");
        long springCount = postRepository.countPosts("spring");

        assertEquals(1, javaCount);
        assertEquals(1, springCount);
    }

}
