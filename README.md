# 🎓 Platziflix - Online Course Platform

## Project Description

Platziflix is a simple, straightforward online course platform. Each course contains lessons with basic descriptions.
It’s a minimalist implementation focused on the core functionality of delivering educational content.

## Tech Stack

### Backend
- **Java 21** - Primary language
- **Spring Boot 3.5.3** - Framework for building the backend API
- **MySQL** - Relational database
- **Docker** - Containerization for development and deployment

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
- Course name
- Description
- Thumbnail (image)
- URL slug
- Assigned teachers
- Management timestamps

### Lesson
- Unique ID
- PBelongs to a course
- Lesson name
- Description
- URL slug
- Video URL
- Management timestamps

### Teacher (Profesor)
- Unique ID
- Full name
- Contact email
- Management timestamps


The focus is on maintaining simplicity and core functionality without additional complex features.