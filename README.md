# myblog
Приложение-блог с использованием Spring Framework

## Запуск проекта

Для запуска только тестов:

```mvn test```

Для запуска всего проекта выполнить команду:

```mvn spring-boot:run```

Проект станет доступен на 8080 порту:

```http://localhost:8080```

___

## Доступный API функционал

- **GET /** перенаправляет на `/posts`
- **GET /posts** отображает главную страницу со списком постов
- **GET /posts/{id}** отображает конкретный пост с комментариями
- **GET /posts/add** отображает форму для добавления нового поста
- **POST /posts** создает новый пост
- **GET /images/{id}** возвращает изображение для поста
- **POST /posts/{id}/like** ставит лайк или убирает лайк с поста
- **GET /posts/{id}/edit** отображает форму редактирования поста
- **POST /posts/{id}** обновляет пост
- **POST /posts/{id}/comments** добавляет комментарий к посту
- **POST /posts/{id}/comments/{commentId}** обновляет комментарий.
- **POST /posts/{id}/comments/{commentId}/delete** удаляет комментарий
- **POST /posts/{id}/delete** удаляет пост

___

## Схема базы данных

<img width="1152" alt="Image" src="https://github.com/user-attachments/assets/6b6f3497-480f-4fef-b8df-5633f53434dc" />