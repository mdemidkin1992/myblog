package ru.mdemidkin.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.RowMapper;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.model.Post;

@UtilityClass
public final class RawMapper {

    public static final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setImageData(rs.getBytes("image_data"));
        post.setLikesCount(rs.getInt("likes_count"));
        return post;
    };

    public static final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setText(rs.getString("text"));
        return comment;
    };
}
