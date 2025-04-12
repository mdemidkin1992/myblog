package ru.mdemidkin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.mdemidkin.config.DataSourceConfiguration;
import ru.mdemidkin.repository.api.TagRepository;
import ru.mdemidkin.repository.impl.TagRepositoryImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, TagRepositoryImpl.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TagRepositoryIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    void testFindOrCreateTag_shouldReturnExistingOrCreateNew() {
        Long id1 = tagRepository.findOrCreateTag("hibernate");
        assertNotNull(id1);

        Long id2 = tagRepository.findOrCreateTag("hibernate");
        assertEquals(id1, id2);
    }

    @Test
    void testLinkTagToPost_shouldAssociateTagWithPost() {
        Long tagId = tagRepository.findOrCreateTag("database");

        tagRepository.linkTagToPost(2L, tagId);

        Set<String> tags = tagRepository.findByPostId(2L);
        assertEquals(4, tags.size());
        assertTrue(tags.contains("database"));
    }

    @Test
    void testDeleteByPostId_shouldRemoveAllTagLinksForPost() {
        Long tagId1 = tagRepository.findOrCreateTag("tag1");
        Long tagId2 = tagRepository.findOrCreateTag("tag2");
        tagRepository.linkTagToPost(2L, tagId1);
        tagRepository.linkTagToPost(2L, tagId2);

        Set<String> tagsBefore = tagRepository.findByPostId(2L);
        assertEquals(5, tagsBefore.size());

        tagRepository.deleteByPostId(2L);

        Set<String> tagsAfter = tagRepository.findByPostId(2L);
        assertTrue(tagsAfter.isEmpty());
    }
}
