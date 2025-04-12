package ru.mdemidkin.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SqlUtils {

    // COMMENTS
    public static final String FIND_COMMENT_BY_ID = "SELECT * FROM comments WHERE id = ?";
    public static final String FIND_COMMENT_BY_POST_ID = "SELECT * FROM comments WHERE post_id = ? ORDER BY id";
    public static final String INSERT_COMMENT = "INSERT INTO comments (post_id, text) VALUES (?, ?)";
    public static final String UPDATE_COMMENT = "UPDATE comments SET text = ? WHERE id = ?";
    public static final String DELETE_COMMENT_BY_ID = "DELETE FROM comments WHERE id = ?";
    public static final String DELETE_COMMENT_BY_POST_ID = "DELETE FROM comments WHERE post_id = ?";

    // POSTS
    public static final String FIND_POST_BY_ID = "SELECT * FROM posts WHERE id = ?";
    public static final String FIND_POSTS_BY_SEARCH = "SELECT DISTINCT p.* FROM posts p " +
            "JOIN post_tags pt ON p.id = pt.post_id " +
            "JOIN tags t ON pt.tag_id = t.id " +
            "WHERE t.name = ? " +
            "ORDER BY p.id DESC LIMIT ? OFFSET ?";
    public static final String FIND_POSTS = "SELECT * FROM posts ORDER BY id DESC LIMIT ? OFFSET ?";
    public static final String COUNT_POSTS_BY_SEARCH = "SELECT COUNT(DISTINCT p.id) FROM posts p " +
            "JOIN post_tags pt ON p.id = pt.post_id " +
            "JOIN tags t ON pt.tag_id = t.id " +
            "WHERE t.name = ?";
    public static final String COUNT_POSTS = "SELECT COUNT(*) FROM posts";
    public static final String DELETE_POST_BY_ID = "DELETE FROM posts WHERE id = ?";
    public static final String INSERT_POST = "INSERT INTO posts (title, text, image_data, likes_count) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_POST = "UPDATE posts SET title = ?, text = ?, image_data = ?, likes_count = ? WHERE id = ?";

    // TAGS
    public static final String FIND_TAG_BY_POST_ID = "SELECT t.name FROM tags t " +
            "JOIN post_tags pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = ?";
    public static final String FIND_TAGS_BY_NAME = "SELECT id FROM tags WHERE name = ?";
    public static final String LINK_POST_TO_TAG = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";
    public static final String DELETE_TAG_BY_POST_ID = "DELETE FROM post_tags WHERE post_id = ?";
    public static final String INSERT_TAG = "INSERT INTO tags (name) VALUES (?)";
}
