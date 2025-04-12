package ru.mdemidkin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private byte[] imageData;
    @Builder.Default
    private int likesCount = 0;
    @Builder.Default
    private Set<String> tags = new HashSet<>();
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
