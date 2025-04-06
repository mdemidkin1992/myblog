package ru.mdemidkin.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.repository.api.PostRepository;
import ru.mdemidkin.repository.api.TagRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static ru.mdemidkin.mapper.RawMapper.postRowMapper;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, id);

        if (posts.isEmpty()) {
            return Optional.empty();
        }

        Post post = posts.getFirst();

        List<Comment> comments = commentRepository.findByPostId(id);
        post.setComments(comments);

        Set<String> tags = tagRepository.findByPostId(id);
        post.setTags(tags);

        return Optional.of(post);
    }

    @Override
    public List<Post> findPosts(String search, int pageSize, int pageNumber) {
        int offset = (pageNumber - 1) * pageSize;

        String sql;
        Object[] params;

        if (search != null && !search.isEmpty()) {
            sql = "SELECT DISTINCT p.* FROM posts p " +
                    "JOIN post_tags pt ON p.id = pt.post_id " +
                    "JOIN tags t ON pt.tag_id = t.id " +
                    "WHERE t.name = ? " +
                    "ORDER BY p.id DESC LIMIT ? OFFSET ?";
            params = new Object[]{search, pageSize, offset};
        } else {
            sql = "SELECT * FROM posts ORDER BY id DESC LIMIT ? OFFSET ?";
            params = new Object[]{pageSize, offset};
        }

        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, params);

        for (Post post : posts) {
            List<Comment> comments = commentRepository.findByPostId(post.getId());
            post.setComments(comments);

            Set<String> tags = tagRepository.findByPostId(post.getId());
            post.setTags(tags);
        }

        return posts;
    }

    @Override
    public long countPosts(String search) {
        String sql;
        Object[] params;

        if (search != null && !search.isEmpty()) {
            sql = "SELECT COUNT(DISTINCT p.id) FROM posts p " +
                    "JOIN post_tags pt ON p.id = pt.post_id " +
                    "JOIN tags t ON pt.tag_id = t.id " +
                    "WHERE t.name = ?";
            params = new Object[]{search};
        } else {
            sql = "SELECT COUNT(*) FROM posts";
            params = new Object[]{};
        }

        return jdbcTemplate.queryForObject(sql, Long.class, params);
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        } else {
            return update(post);
        }
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteByPostId(id);
        tagRepository.deleteByPostId(id);
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    private Post insert(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO posts (title, text, image_data, likes_count) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setBytes(3, post.getImageData());
            ps.setInt(4, post.getLikesCount());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        post.setId(id);

        saveTags(post);
        return post;
    }

    private Post update(Post post) {
        jdbcTemplate.update(
                "UPDATE posts SET title = ?, text = ?, image_data = ?, likes_count = ? WHERE id = ?",
                post.getTitle(),
                post.getText(),
                post.getImageData(),
                post.getLikesCount(),
                post.getId()
        );

        tagRepository.deleteByPostId(post.getId());
        saveTags(post);
        return post;
    }

    private void saveTags(Post post) {
        Set<String> tags = post.getTags();
        if (tags == null || tags.isEmpty()) {
            return;
        }

        for (String tag : tags) {
            Long tagId = tagRepository.findOrCreateTag(tag);
            tagRepository.linkTagToPost(post.getId(), tagId);
        }
    }

}
