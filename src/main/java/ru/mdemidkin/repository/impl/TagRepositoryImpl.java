package ru.mdemidkin.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mdemidkin.repository.BaseRepository;
import ru.mdemidkin.repository.api.TagRepository;
import ru.mdemidkin.utils.SqlUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class TagRepositoryImpl extends BaseRepository implements TagRepository {

    public TagRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public Set<String> findByPostId(Long postId) {
        List<String> tags = jdbcTemplate.queryForList(SqlUtils.FIND_TAG_BY_POST_ID, String.class, postId);
        return new HashSet<>(tags);
    }

    @Override
    public Long findOrCreateTag(String name) {
        String findSql = SqlUtils.FIND_TAGS_BY_NAME;
        List<Long> ids = jdbcTemplate.queryForList(findSql, Long.class, name);

        if (!ids.isEmpty()) {
            return ids.getFirst();
        }
        return createNewTag(name);
    }

    @Override
    public void linkTagToPost(Long postId, Long tagId) {
        jdbcTemplate.update(
                SqlUtils.LINK_POST_TO_TAG,
                postId, tagId
        );
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update(SqlUtils.DELETE_TAG_BY_POST_ID, postId);
    }

    private Long createNewTag(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    SqlUtils.INSERT_TAG,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
