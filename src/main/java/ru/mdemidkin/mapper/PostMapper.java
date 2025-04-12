package ru.mdemidkin.mapper;

import org.springframework.stereotype.Component;
import ru.mdemidkin.controller.dto.PostFullDto;
import ru.mdemidkin.controller.dto.PostPreviewDto;
import ru.mdemidkin.model.Post;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostPreviewDto mapTopPostPreviewDto(Post post) {
        return PostPreviewDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .likesCount(post.getLikesCount())
                .imageData(post.getImageData())
                .comments(post.getComments())
                .textPreview(getTextPreview(post.getText()))
                .tags(post.getTags())
                .build();
    }

    public PostFullDto mapToPostFullDto(Post post) {
        return PostFullDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .text(post.getText())
                .likesCount(post.getLikesCount())
                .imageData(post.getImageData())
                .comments(post.getComments())
                .textParts(getTextParts(post.getText()))
                .tags(post.getTags())
                .build();
    }

    public List<PostPreviewDto> mapTopPostPreviewDtoList(List<Post> posts) {
        return posts.stream()
                .map(this::mapTopPostPreviewDto)
                .toList();
    }

    private String getTextPreview(String text) {
        StringBuilder preview = new StringBuilder();
        if (text != null && !text.isEmpty()) {
            String[] lines = text.split("\\r?\\n");
            int maxLines = Math.min(lines.length, 3);
            for (int i = 0; i < maxLines; i++) {
                preview.append(lines[i]).append("\n");
            }
        }
        return preview.toString().trim();
    }

    private List<String> getTextParts(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }


}
