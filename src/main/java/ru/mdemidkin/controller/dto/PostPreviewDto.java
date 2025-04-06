package ru.mdemidkin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mdemidkin.model.Comment;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPreviewDto {
    private Long id;
    private String title;
    private String textPreview;
    private byte[] imageData;
    private int likesCount;
    private Set<String> tags;
    private List<Comment> comments;

    public String getTagsAsText() {
        return tags != null ? String.join(", ", tags) : "";
    }

}
