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