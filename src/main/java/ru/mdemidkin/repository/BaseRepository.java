package ru.mdemidkin.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public abstract class BaseRepository {

    protected final JdbcTemplate jdbcTemplate;

}
