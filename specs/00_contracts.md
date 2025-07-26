# MyPlatziFlix

Online Courses Platform. Each course has lessons, descriptions and not much else. That is the beginning.

## Stacks

### Frontend
- Typescript
- CSS modules
- SASS

### Mobile
- iOS:
    - Swift
    - SwiftUI
- Android:
    - Kotlin
    - Jetpack Compose

### Backend
- Java 21
- Spring Boot 3.5.3
- MySQL

## Contracts

### Entities
1. Course
2. Lesson
3. Teacher

### Contracts

- Course
```json
{
    "id": 1,
    "name": "React Course",
    "description": "React Course",
    "thumbnail": "https://via.placeholder.com/150", 
    "slug": "react-course",
    "teachers_id": [1, 2, 3],
    "created_at": "2025-07-25T15:36:23",
    "updated_at": "2025-07-25T15:36:23",
    "deleted_at": "2025-07-25T15:36:23"
}
```

- Lesson:
```json
{
    "id": 1, 
    "course_id": 1, 
    "name": "Lesson 1",
    "description": "Lesson 1",
    "slug": "lesson-1",
    "video_url": "https://youtube.com/watch?v=nAe_a1mdNDY",
    "created_at": "2025-07-25T15:36:23",
    "updated_at": "2025-07-25T15:36:23",
    "deleted_at": "2025-07-25T15:36:23"
}
```

- Teacher
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "created_at": "2025-07-25T15:36:23",
    "updated_at": "2025-07-25T15:36:23",
    "deleted_at": "2025-07-25T15:36:23"
}
```

### Endpoints

- GET /courses -> List all courses
```json
[
    {
        "id": 1,
        "name": "React Course",
        "description": "React Course",
        "thumbnail": "https://via.placeholder.com/150", 
        "slug": "react-course"
    },
    ...
]
```

- GET /courses/{slug} -> Get course by slug
```json
{
    "id": 1,
    "name": "React Course",
    "description": "React Course",
    "thumbnail": "https://via.placeholder.com/150", 
    "slug": "react-course",
    "teachers_id": [1, 2, 3],
    "lessons": [
        {
            "id": 1,
            "name": "Lesson 1",
            "description": "Lesson 1",
            "slug": "lesson-1",
        },
        ...
    ]
}
```

- GET /courses/{course_slug}/lessons/{lesson_id} -> Get lesson by course slug and lesson id
```json
{
    "id": 1,
    "name": "Lesson 1",
    "description": "Lesson 1",
    "slug": "lesson-1",
    "video_url": "https://youtube.com/watch?v=nAe_a1mdNDY",
    "created_at": "2025-07-23 23:59:59",
    "updated_at": "2025-07-23 23:59:59",
    "deleted_at": "2025-07-23 23:59:59"
}
```


