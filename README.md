# 🎓 Platziflix - Online Course Platform

## Project Description

Platziflix is a simple, straightforward online course platform. Each course contains lessons with basic descriptions.
It’s a minimalist implementation focused on the core functionality of delivering educational content.

## Tech Stack

### Backend
- **Java 21** - Primary programming language
- **Spring Boot 3.5.3** - Framework for building the backend API
- **MySQL** - Relational database
- **Docker** - Containerization tool for development and deployment

### Frontend
- **TypeScript** - Statically typed language
- **CSS Modules** - Modular styling
- **SASS** - CSS preprocessor

### Mobile
- **iOS**: Swift + SwiftUI
- **Android**: Kotlin + Jetpack Compose

## Architecture

```
Frontend (TypeScript)     Mobile Apps (Swift/Kotlin)
        │                           │
        └─────────┬─────────────────┘
                  │
            Backend API (Spring Boot)
                  │
            Database (MySQL)
```

## System Entities

### Course
- Unique ID
- Name
- Description
- Thumbnail (image)
- URL slug
- Lessons
- Assigned teachers
- Enrolled students
- Management timestamps

### Lesson
- Unique ID
- Name
- Description
- URL slug
- Video URL
- Belongs to a course
- Management timestamps

### Teacher
- Unique ID
- Full name
- Contact email
- Username
- Password
- Role (TEACHER)
- Biography
- Specialization
- Courses taught
- Management timestamps

### Student
- Unique ID
- Full name
- Contact email
- Username
- Password
- Role (STUDENT)
- Enrolled courses
- Management timestamps

### Enrollment
- Unique ID
- Enrollment date
- Completion date
- Student
- Course
- Management timestamps


The focus is on maintaining simplicity and core functionality without additional complex features.