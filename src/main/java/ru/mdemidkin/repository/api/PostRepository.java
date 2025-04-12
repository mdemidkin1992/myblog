package ru.mdemidkin.repository.api;

import ru.mdemidkin.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Optional<Post> findById(Long id);

    List<Post> findPosts(String search, int pageSize, int pageNumber);

    long countPosts(String search);

    Post save(Post post);

    void deleteById(Long id);

}
