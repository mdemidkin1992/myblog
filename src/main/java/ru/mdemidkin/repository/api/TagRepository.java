package ru.mdemidkin.repository.api;

import java.util.Set;

public interface TagRepository {

    Set<String> findByPostId(Long postId);

    Long findOrCreateTag(String name);

    void linkTagToPost(Long postId, Long tagId);

    void deleteByPostId(Long postId);
}
