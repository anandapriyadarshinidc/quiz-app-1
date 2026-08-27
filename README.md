# Quiz Application Backend

A complete, production-structured **Spring Boot + MySQL** backend for a quiz
application. It exposes clean REST/JSON APIs meant to be consumed by a
separately-built frontend (e.g. an **Appzillion** app) — this repository
contains **backend code only**, no UI.

---

## 1. Description

The backend supports two sides of the application:

- **Admin side** — create/read/update/delete quiz **categories**, and
  create/read/update/delete **questions** under any category. There is
  **no fixed limit** on how many questions a category can hold — if you add
  5 questions it has 5, if you add 500 it has 500.
- **User side** — browse categories, fetch every question in a category
  (with the correct answer always hidden), submit answers, and get back an
  instantly computed score (correct/wrong/unanswered/percentage). Every quiz
  attempt is persisted so users can view their history later.

The number of questions a user is quizzed on for a category is **always**
however many questions currently exist in that category — this is never
hardcoded anywhere in the code.

---

## 2. Technologies

- Java 17
- Spring Boot 3.3.x
- Spring Web (REST controllers)
- Spring Data JPA (Hibernate)
- MySQL 8
- Maven
- Lombok
- Jakarta Bean Validation (`spring-boot-starter-validation`)

---

## 3. Project Structure

```
src/main/java/com/example/quizapp/
├── QuizApplication.java
├── controller
│   ├── admin
│   │   ├── AdminCategoryController.java
│   │   └── AdminQuestionController.java
│   ├── CategoryController.java
│   ├── QuizController.java
│   └── UserController.java
├── service
│   ├── CategoryService.java
│   ├── QuestionService.java
│   ├── QuizService.java
│   └── UserService.java
├── repository
│   ├── CategoryRepository.java
│   ├── QuestionRepository.java
│   ├── QuizAttemptRepository.java
│   └── UserRepository.java
├── entity
│   ├── Category.java
│   ├── Question.java
│   ├── QuizAttempt.java
│   └── User.java
├── dto
│   ├── category/   (CategoryRequest, CategoryResponse)
│   ├── question/   (QuestionRequest, AdminQuestionResponse, UserQuestionResponse)
│   ├── quiz/       (AnswerRequest, QuizSubmissionRequest, QuizResultResponse, QuizAttemptResponse)
│   └── user/       (UserRequest, UserResponse)
├── exception
│   ├── ResourceNotFoundException.java
│   ├── InvalidQuizSubmissionException.java
│   ├── DuplicateResourceException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
└── config
    ├── CorsConfig.java
    └── DataSeeder.java   (sample data for local testing)
```

---

## 4. Database Setup

Create the database (or let Hibernate/the app do it automatically — the
JDBC URL below includes `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE quiz_app;
```

Tables are auto-created/updated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
on first run — you don't need to write any DDL by hand.

Entity relationships:

```
Category (1) ────< Question (many)
User     (1) ────< QuizAttempt (many)
Category (1) ────< QuizAttempt (many)
```

---

## 5. Configuration

Set these environment variables before running (defaults to `root`/`root` if unset):

| Variable      | Description               |
|---------------|----------------------------|
| `DB_USERNAME` | MySQL username             |
| `DB_PASSWORD` | MySQL password             |

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quiz_app?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080
```

---

## 6. How To Run

```bash
# 1. Set your DB credentials (Linux/Mac)
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

# Windows (PowerShell)
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"

# 2. Build
mvn clean install

# 3. Run
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

On first startup, `DataSeeder` automatically inserts 5 sample categories
(Animals, Java, Python, DBMS, Operating Systems) with 5 questions each, plus
one ADMIN user (`admin@quizapp.com` / `admin123`) and one USER user
(`user@quizapp.com` / `user123`). This only happens once — if the
`categories` table already has rows, seeding is skipped.

---

## 7. Base URL

```
http://localhost:8080/api
```

---

## 8. Authentication Note

To keep v1 simple and unblock frontend integration quickly, this backend
**does not implement full Spring Security / JWT authentication**. Instead:

- Admin and user APIs are cleanly separated by URL path: `/api/admin/**`
  vs `/api/**`.
- Passwords are never returned in any API response (`@JsonIgnore` on the
  entity field).
- All authentication logic lives behind `UserService`/`UserController`, so
  real authentication (Spring Security, JWT, BCrypt password hashing, role
  guards) can be added later **without changing any other layer** of the
  application.

---

## 9. API Documentation

All responses are JSON. All error responses follow this shape:

```json
{
  "status": 404,
  "message": "Category not found with id: 99",
  "timestamp": "2026-08-26T12:30:00"
}
```

### 9.1 Admin Category APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|--------------|---------------|-----------|
| POST | `/api/admin/categories` | Create a category | `{ "name": "Animals", "description": "..." }` | `201` CategoryResponse |
| GET | `/api/admin/categories` | List all categories | — | `200` List<CategoryResponse> |
| GET | `/api/admin/categories/{id}` | Get one category | — | `200` CategoryResponse / `404` |
| PUT | `/api/admin/categories/{id}` | Update a category | `{ "name": "...", "description": "..." }` | `200` CategoryResponse / `404` |
| DELETE | `/api/admin/categories/{id}` | Delete a category (and its questions) | — | `204` / `404` |

### 9.2 Admin Question APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|--------------|---------------|-----------|
| POST | `/api/admin/questions` | Create a question | See below | `201` AdminQuestionResponse |
| GET | `/api/admin/questions` | List all questions (all categories) | — | `200` List<AdminQuestionResponse> |
| GET | `/api/admin/questions/{id}` | Get one question | — | `200` AdminQuestionResponse / `404` |
| GET | `/api/admin/questions/category/{categoryId}` | Get **all** questions in a category (includes correctAnswer) | — | `200` List<AdminQuestionResponse> |
| PUT | `/api/admin/questions/{id}` | Update a question | See below | `200` AdminQuestionResponse / `404` |
| DELETE | `/api/admin/questions/{id}` | Delete a question | — | `204` / `404` |

Create/Update question body:
```json
{
  "questionText": "Which is the largest land animal?",
  "optionA": "Lion",
  "optionB": "Elephant",
  "optionC": "Tiger",
  "optionD": "Giraffe",
  "correctAnswer": "B",
  "categoryId": 1
}
```

### 9.3 User Category APIs

| Method | Endpoint | Description | Response |
|--------|----------|--------------|-----------|
| GET | `/api/categories` | List all categories | `200` List<CategoryResponse> |
| GET | `/api/categories/{id}` | Get one category | `200` CategoryResponse / `404` |

### 9.4 User Question API

| Method | Endpoint | Description | Response |
|--------|----------|--------------|-----------|
| GET | `/api/categories/{categoryId}/questions` | Get **every** question currently in the category. `correctAnswer` is never included. | `200` List<UserQuestionResponse> / `404` |

### 9.5 Quiz APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|--------------|---------------|-----------|
| POST | `/api/quizzes/{categoryId}/submit` | Submit answers, get graded instantly | See below | `200` QuizResultResponse |
| GET | `/api/quiz-attempts/{attemptId}` | Get one past attempt's full result | — | `200` QuizResultResponse / `404` |

Submit body:
```json
{
  "userId": 2,
  "answers": [
    { "questionId": 1, "selectedAnswer": "B" },
    { "questionId": 2, "selectedAnswer": "A" }
  ]
}
```

### 9.6 User APIs

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|--------------|---------------|-----------|
| POST | `/api/users` | Register a user | `{ "name", "email", "password", "role"? }` | `201` UserResponse |
| GET | `/api/users` | List all users | — | `200` List<UserResponse> |
| GET | `/api/users/{id}` | Get one user | — | `200` UserResponse / `404` |

### 9.7 Quiz Attempt (History) APIs

| Method | Endpoint | Description | Response |
|--------|----------|--------------|-----------|
| GET | `/api/users/{userId}/quiz-attempts` | Full quiz history for a user | `200` List<QuizAttemptResponse> |

---

## 10. Example Quiz Flow

```
1. POST /api/admin/categories          → create "Animals"
2. POST /api/admin/questions   (x N)   → add as many questions as you like to Animals
3. GET  /api/categories                → user browses categories
4. GET  /api/categories/1/questions    → user fetches ALL Animals questions (no answers)
5. POST /api/quizzes/1/submit          → user submits selected answers
6. Backend grades against ALL Animals questions, computes score/percentage
7. GET  /api/users/2/quiz-attempts     → user reviews past attempts
```

---

## 11. Score Calculation

```
score       = number of correct answers   (1 mark each)
wrongAnswers = totalQuestions - correctAnswers
unanswered   = questions the user left blank / did not submit
percentage   = (correctAnswers / totalQuestions) * 100   // floating-point division
```

**Unanswered questions are counted as wrong** (documented behavior — see
`QuizService` javadoc). `totalQuestions` always equals however many
questions currently exist in the category, regardless of how many the user
actually answered.

Example: category has 25 questions, user answers 20 correctly, leaves 2
blank, gets 3 wrong:

```
totalQuestions = 25
correctAnswers = 20
wrongAnswers   = 5      (3 actually wrong + 2 unanswered)
unanswered     = 2
score          = 20
percentage     = 80.0
```

---

## 12. Validation Rules

- **Category**: `name` required, non-blank; `description` optional.
- **Question**: `questionText`, `optionA`–`optionD`, `correctAnswer` required;
  `correctAnswer` must be one of `A`/`B`/`C`/`D`; `categoryId` required and
  must reference an existing category.
- **User**: `name` required; `email` required and must be a valid email;
  `password` required (min 4 chars).
- **Quiz submission**: category must exist; every submitted `questionId`
  must exist **and** belong to the submitted category (otherwise `400`);
  `selectedAnswer`, when present, must be one of `A`/`B`/`C`/`D`.

Validation failures return **HTTP 400** with a field-level error map.
Missing resources return **HTTP 404**.

---

## 13. Error Responses

| Status | Meaning |
|--------|---------|
| 200 | Success |
| 201 | Resource created |
| 204 | Deleted successfully, no content |
| 400 | Validation error / invalid quiz submission |
| 404 | Resource (category/question/user/attempt) not found |
| 409 | Duplicate resource (e.g. category name or email already exists) |
| 500 | Unexpected server error |

---

## 14. Postman Testing

A ready-to-import collection is included at
`postman/QuizApplication.postman_collection.json`.

**Import it:** Postman → Import → select the file → the collection
`Quiz Application Backend` appears with a `baseUrl` variable pre-set to
`http://localhost:8080/api`.

**Recommended run order** (matches the folders in the collection):

1. Create User → `POST /api/users`
2. Create Category → `POST /api/admin/categories`
3. Create Question → `POST /api/admin/questions`
4. Create More Questions → repeat step 3 with different bodies
5. Get Categories → `GET /api/categories`
6. Get Questions For Category → `GET /api/categories/{id}/questions`
7. Submit Quiz → `POST /api/quizzes/{categoryId}/submit`
8. Get Quiz Result → `GET /api/quiz-attempts/{attemptId}`
9. Get User Quiz History → `GET /api/users/{userId}/quiz-attempts`
10. Update Question → `PUT /api/admin/questions/{id}`
11. Delete Question → `DELETE /api/admin/questions/{id}`
12. Delete Category → `DELETE /api/admin/categories/{id}`

After step 1–4, note the actual generated `id` values from the responses
and update the collection variables (`categoryId`, `questionId`, `userId`,
`attemptId`) accordingly, or just paste IDs directly into each request URL.

---

## 15. Integrating With an Appzillion Frontend

- All endpoints are plain REST/JSON — no server-side rendering, no session
  cookies required.
- CORS is enabled for all origins on `/api/**` (`CorsConfig.java`) so the
  Appzillion app can call this backend directly from any host during
  development. Restrict `allowedOrigins` before going to production.
- `UserQuestionResponse` never contains `correctAnswer` — safe to render
  directly in a quiz-taking UI.
- `QuizResultResponse` gives everything needed for a results screen in one
  call: `totalQuestions`, `correctAnswers`, `wrongAnswers`, `unanswered`,
  `score`, `percentage`.
