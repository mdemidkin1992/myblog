package ru.mdemidkin.service.api;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import ru.mdemidkin.model.Paging;
import ru.mdemidkin.model.Post;

import java.util.List;

public interface PostService {

    Post getPostById(Long id);

    Post createPost(String title, String text, MultipartFile image, String tags);

    Post createPost(Post post);

    List<Post> getPosts(String search, int pageSize, int pageNumber);

    Paging getPaging(String search, int pageSize, int pageNumber);

    ByteArrayResource getPostImage(Long id);

    void updateLikes(Long id, boolean like);

    void updatePost(Long id, String title, String text, MultipartFile image, String tags);

    void addComment(Long postId, String text);

    void updateComment(Long postId, Long commentId, String text);

    void deleteComment(Long postId, Long commentId);

    void deletePost(Long id);
}
