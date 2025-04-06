DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;

CREATE TABLE IF NOT EXISTS posts
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    text        VARCHAR(255),
    image_data  BLOB,
    likes_count INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    text    VARCHAR(255),
    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS post_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_post_tag_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_post_tag_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);


INSERT INTO posts (title, text, image_data, likes_count)
VALUES ('Первый пост', 'Это текст первого поста', x'89504E470D0A1A0A', 5);
INSERT INTO posts (title, text, image_data, likes_count)
VALUES ('Второй пост', 'Это текст второго поста', x'FFD8FFE000104A464946', 10);

INSERT INTO comments (post_id, text)
VALUES (1, 'Отличный пост!');
INSERT INTO comments (post_id, text)
VALUES (1, 'Спасибо за информацию.');
INSERT INTO comments (post_id, text)
VALUES (2, 'Интересное мнение.');

INSERT INTO tags (name)
VALUES ('Java');
INSERT INTO tags (name)
VALUES ('Spring');
INSERT INTO tags (name)
VALUES ('Tutorial');
INSERT INTO tags (name)
VALUES ('H2');
INSERT INTO tags (name)
VALUES ('Database');

INSERT INTO post_tags (post_id, tag_id)
VALUES (1, 1);
INSERT INTO post_tags (post_id, tag_id)
VALUES (1, 2);

INSERT INTO post_tags (post_id, tag_id)
VALUES (2, 3);
INSERT INTO post_tags (post_id, tag_id)
VALUES (2, 4);
INSERT INTO post_tags (post_id, tag_id)
VALUES (2, 5);