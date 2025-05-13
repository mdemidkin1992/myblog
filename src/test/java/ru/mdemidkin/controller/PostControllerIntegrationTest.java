package ru.mdemidkin.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void redirectToMainPage_shouldRedirectToPosts() {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    @SneakyThrows
    void getPosts_shouldReturnHtmlWithPosts() {
        mockMvc.perform(get("/posts")
                        .param("search", "")
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(xpath("//table/tr").nodeCount(3))
                .andExpect(xpath("//table/tr[3]/td/h2").string("Первый пост"))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void showAddPostForm_shouldReturnAddPostView() {
        mockMvc.perform(get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    @SneakyThrows
    void getPostsById_shouldReturnFirstPost() {
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//h2").string("Первый пост"))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void savePost_shouldCreateThirdPost() {
        mockMvc.perform(multipart("/posts")
                        .file(getMockFile())
                        .param("title", "Новый пост")
                        .param("text", "Новый текст")
                        .param("tags", "tag1,tag2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/3"));
    }

    @Test
    @SneakyThrows
    void getImage_shouldReturnImageResource() {
        mockMvc.perform(get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @SneakyThrows
    void showEditPostForm_shouldReturnAddPostViewWithPost() {
        mockMvc.perform(get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    @SneakyThrows
    void updatePost_shouldUpdatePostAndRedirect() {
        mockMvc.perform(multipart("/posts/1")
                        .file(getMockFile())
                        .param("title", "Updated Title")
                        .param("text", "Updated Text")
                        .param("tags", "tag1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    @SneakyThrows
    void addComment_shouldAddCommentAndRedirect() {
        mockMvc.perform(post("/posts/1/comments")
                        .param("text", "New comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    @SneakyThrows
    void updateComment_shouldUpdateCommentAndRedirect() {
        mockMvc.perform(post("/posts/1/comments/1")
                        .param("text", "Updated comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    @SneakyThrows
    void deleteComment_shouldDeleteCommentAndRedirect() {
        mockMvc.perform(post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    @SneakyThrows
    void deletePost_shouldDeletePostAndRedirect() {
        mockMvc.perform(post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    private MockMultipartFile getMockFile() {
        return new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }
}
