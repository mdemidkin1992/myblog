package ru.mdemidkin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.mdemidkin.controller.dto.PostFullDto;
import ru.mdemidkin.controller.dto.PostPreviewDto;
import ru.mdemidkin.mapper.PostMapper;
import ru.mdemidkin.model.Paging;
import ru.mdemidkin.model.Post;
import ru.mdemidkin.service.api.PostService;

import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping("/")
    public String redirectToMainPage() {
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable(name = "id") Long id, Model model) {
        Post postModel = postService.getPostById(id);
        PostFullDto dto = postMapper.mapToPostFullDto(postModel);
        model.addAttribute("post", dto);
        return "post";
    }

    @GetMapping("/posts/add")
    public String showAddPostForm() {
        return "add-post";
    }

    @PostMapping("/posts")
    public String addPost(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("tags") String tags,
            @RequestParam("image") MultipartFile image) {
        Post post = postService.createPost(title, text, image, tags);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts")
    public String getPosts(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            Model model) {

        List<Post> posts = postService.getPosts(search, pageSize, pageNumber);
        List<PostPreviewDto> dtos = postMapper.mapTopPostPreviewDtoList(posts);
        Paging paging = postService.getPaging(search, pageSize, pageNumber);

        model.addAttribute("posts", dtos);
        model.addAttribute("search", search);
        model.addAttribute("paging", paging);

        return "posts";
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable(name = "id") Long id) {
        ByteArrayResource resource = postService.getPostImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable(name = "id") Long id, Model model) {
        Post postModel = postService.getPostById(id);
        PostFullDto dto = postMapper.mapToPostFullDto(postModel);
        model.addAttribute("post", dto);
        return "add-post";
    }

    @PostMapping("/posts/{id}")
    public String updatePost(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "image", required = false) MultipartFile image,
            @RequestParam(name = "tags", required = false, defaultValue = "") String tags) {

        postService.updatePost(id, title, text, image, tags);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/like")
    public String likePost(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "like") boolean like) {

        postService.updateLikes(id, like);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "text") String text) {

        postService.addComment(id, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments/{commentId}")
    public String updateComment(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "commentId") Long commentId,
            @RequestParam(name = "text") String text) {

        postService.updateComment(id, commentId, text);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "commentId") Long commentId) {

        postService.deleteComment(id, commentId);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

}
