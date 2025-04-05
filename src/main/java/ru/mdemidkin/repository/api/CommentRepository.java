package ru.mdemidkin.repository.api;

import ru.mdemidkin.model.Comment;

import java.util.List;

public interface CommentRepository {

    List<Comment> findByPostId(Long postId);

    Comment save(Comment comment);

    Comment findById(Long id);

    void deleteById(Long id);

    void deleteByPostId(Long postId);
}
