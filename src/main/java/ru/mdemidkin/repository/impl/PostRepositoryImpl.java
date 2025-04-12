package ru.mdemidkin.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.repository.BaseRepository;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.repository.api.PostRepository;
import ru.mdemidkin.repository.api.TagRepository;
import ru.mdemidkin.utils.SqlUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static ru.mdemidkin.mapper.RawMapper.postRowMapper;

@Repository
public class PostRepositoryImpl extends BaseRepository implements PostRepository {

    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    public PostRepositoryImpl(JdbcTemplate jdbcTemplate,
                              CommentRepository commentRepository,
                              TagRepository tagRepository) {
        super(jdbcTemplate);
        this.commentRepository = commentRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Post> findById(Long id) {
        List<Post> posts = jdbcTemplate.query(SqlUtils.FIND_POST_BY_ID, postRowMapper, id);

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
            sql = SqlUtils.FIND_POSTS_BY_SEARCH;
            params = new Object[]{search, pageSize, offset};
        } else {
            sql = SqlUtils.FIND_POSTS;
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
            sql = SqlUtils.COUNT_POSTS_BY_SEARCH;
            params = new Object[]{search};
        } else {
            sql = SqlUtils.COUNT_POSTS;
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
        jdbcTemplate.update(SqlUtils.DELETE_POST_BY_ID, id);
    }

    private Post insert(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    SqlUtils.INSERT_POST,
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
                SqlUtils.UPDATE_POST,
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
