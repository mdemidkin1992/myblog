package ru.mdemidkin.repository.api;

import ru.mdemidkin.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    List<Comment> findByPostId(Long postId);

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    void deleteById(Long id);

    void deleteByPostId(Long postId);
}
