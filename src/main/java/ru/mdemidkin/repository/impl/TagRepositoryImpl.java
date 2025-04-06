package ru.mdemidkin.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mdemidkin.repository.api.TagRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<String> findByPostId(Long postId) {
        String sql = "SELECT t.name FROM tags t " +
                "JOIN post_tags pt ON t.id = pt.tag_id " +
                "WHERE pt.post_id = ?";

        List<String> tags = jdbcTemplate.queryForList(sql, String.class, postId);
        return new HashSet<>(tags);
    }

    @Override
    public Long findOrCreateTag(String name) {
        String findSql = "SELECT id FROM tags WHERE name = ?";
        List<Long> ids = jdbcTemplate.queryForList(findSql, Long.class, name);

        if (!ids.isEmpty()) {
            return ids.getFirst();
        }
        return createNewTag(name);
    }

    @Override
    public void linkTagToPost(Long postId, Long tagId) {
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId, tagId
        );
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update("DELETE FROM post_tags WHERE post_id = ?", postId);
    }

    private Long createNewTag(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tags (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
