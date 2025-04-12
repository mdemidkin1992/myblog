package ru.mdemidkin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mdemidkin.exception.EntityNotFoundException;
import ru.mdemidkin.exception.ImageNotFoundException;
import ru.mdemidkin.exception.ImageProcessingException;
import ru.mdemidkin.model.Comment;
import ru.mdemidkin.model.Paging;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.repository.api.CommentRepository;
import ru.mdemidkin.repository.api.PostRepository;
import ru.mdemidkin.service.api.PostService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final CommentRepository commentRepository;

    @Override
    public Post createPost(Post post) {
        return repository.save(post);
    }

    private final PostRepository repository;

    @Override
    public Post getPostById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("не найден пост с id: " + id));
    }

    @Override
    public List<Post> getPosts(String search, int pageSize, int pageNumber) {
        return repository.findPosts(search, pageSize, pageNumber);
    }

    @Override
    public Paging getPaging(String search, int pageSize, int pageNumber) {
        long totalElements = repository.countPosts(search);
        boolean hasNext = pageNumber * pageSize < totalElements;
        boolean hasPrevious = pageNumber > 1;
        return new Paging(pageNumber, pageSize, hasNext, hasPrevious);
    }

    @Override
    public Post createPost(String title, String text, MultipartFile image, String tags) {
        Post post = Post.builder()
                .title(title)
                .text(text)
                .build();

        addImageIfNotEmpty(post, image);
        setTagsIfNotEmpty(post, tags);
        return repository.save(post);
    }

    @Override
    public ByteArrayResource getPostImage(Long id) {
        Post post = getPostById(id);
        if (post.getImageData() == null) {
            throw new ImageNotFoundException("не найдено изображение для поста с id: " + id);
        }
        return new ByteArrayResource(post.getImageData());
    }

    @Override
    public void updateLikes(Long id, boolean like) {
        Post post = getPostById(id);
        int currentLikes = post.getLikesCount();

        if (like) {
            post.setLikesCount(currentLikes + 1);
        } else if (currentLikes > 0) {
            post.setLikesCount(currentLikes - 1);
        }

        repository.save(post);
    }

    @Override
    public void updatePost(Long id, String title, String text, MultipartFile image, String tags) {
        Post post = getPostById(id);
        if (title != null && !title.isEmpty()) {
            post.setTitle(title);
        }
        if (text != null && !text.isEmpty()) {
            post.setText(text);
        }
        addImageIfNotEmpty(post, image);
        setTagsIfNotEmpty(post, tags);
        repository.save(post);
    }

    @Override
    public void addComment(Long postId, String text) {
        Post post = getPostById(postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setText(text);

        Comment savedComment = commentRepository.save(comment);
        post.getComments().add(savedComment);
    }

    @Override
    public void updateComment(Long postId, Long commentId, String text) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent() && optional.get().getPostId().equals(postId)) {
            Comment comment = optional.get();
            comment.setText(text);
            commentRepository.save(comment);
        }
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent() && optional.get().getPostId().equals(postId)) {
            commentRepository.deleteById(commentId);
        }
    }

    @Override
    public void deletePost(Long id) {
        repository.deleteById(id);
    }

    private void addImageIfNotEmpty(Post post, MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                byte[] imageBytes = image.getBytes();
                post.setImageData(imageBytes);
            }
        } catch (IOException ex) {
            throw new ImageProcessingException("ошибка при обработке изображения");
        }
    }

    private void setTagsIfNotEmpty(Post post, String tags) {
        if (tags != null && !tags.isEmpty()) {
            Set<String> tagSet = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toSet());
            post.setTags(tagSet);
        }
    }
}
