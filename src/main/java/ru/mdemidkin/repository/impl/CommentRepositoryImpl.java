package ru.mdemidkin.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.repository.BaseRepository;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.utils.SqlUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static ru.mdemidkin.mapper.RawMapper.commentRowMapper;

@Repository
public class CommentRepositoryImpl extends BaseRepository implements CommentRepository {

    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return jdbcTemplate.query(SqlUtils.FIND_COMMENT_BY_POST_ID, commentRowMapper, postId);
    }

    @Override
    public Comment findById(Long id) {
        List<Comment> comments = jdbcTemplate.query(SqlUtils.FIND_COMMENT_BY_ID, commentRowMapper, id);
        return comments.isEmpty() ? null : comments.getFirst();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            return insert(comment);
        } else {
            return update(comment);
        }
    }

    private Comment insert(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SqlUtils.INSERT_COMMENT, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPostId());
            ps.setString(2, comment.getText());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        comment.setId(id);

        return comment;
    }

    private Comment update(Comment comment) {
        jdbcTemplate.update(
                SqlUtils.UPDATE_COMMENT,
                comment.getText(),
                comment.getId());

        return comment;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(SqlUtils.DELETE_COMMENT_BY_ID, id);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update(SqlUtils.DELETE_COMMENT_BY_POST_ID, postId);
    }

}
