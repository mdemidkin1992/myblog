package ru.mdemidkin.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.repository.api.CommentRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static ru.mdemidkin.mapper.RawMapper.commentRowMapper;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    @Override
    public Comment findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        List<Comment> comments = jdbcTemplate.query(sql, commentRowMapper, id);
        return comments.isEmpty() ? null : comments.get(0);
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
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO comments (post_id, text) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
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
                "UPDATE comments SET text = ? WHERE id = ?",
                comment.getText(),
                comment.getId()
        );

        return comment;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update("DELETE FROM comments WHERE post_id = ?", postId);
    }

}
