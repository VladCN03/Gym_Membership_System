# GymMembershipSystem

A full-stack platform for managing gym memberships, members and training activity. Administrators run the gym from a dashboard; members track their workouts, follow their progress and get AI-based training recommendations.

Built with **Spring Boot 3** (Java) on the backend, **Vue 3** on the frontend and **MySQL** for storage, communicating through a REST API secured with JWT.


## Features

### Authentication and roles
- Login with JWT-based authentication between frontend and backend
- Role-based access control with two roles: **Admin** and **Member**
- Protected routes on both the API (Spring Security) and the UI (Vue Router guards)

### Admin
- Manage members, trainers and membership types (create, edit, delete)
- Assign memberships to members and track their validity
- Dashboard with gym statistics and data tables

### Member
- View active membership and its details
- Log and track workouts
- Personalized statistics dashboard showing training progress over time

### AI recommendations
- Suggests personalized workouts and training plans based on the member's data and activity history
- Powered by the **Google Gemini API**: the backend builds a prompt from the member's profile and logged workouts, sends it to Gemini and returns the generated recommendations to the frontend
- The API key stays on the server, so it is never exposed to the browser

## Tech stack

| Layer | Technology |
| --- | --- |
| Frontend | Vue 3, Quasar / Vuetify, Vite, Vue Router, Axios |
| Backend | Java, Spring Boot 3, Spring Security, Spring Data JPA (Hibernate) |
| Database | MySQL |
| Authentication | JWT |
| AI | Google Gemini API |
| Build tools | Maven, Node.js / npm |

## Architecture

```
Vue 3 SPA  ──HTTP + JWT──▶  Spring Boot REST API  ──JPA──▶  MySQL
                                   │
                                   └──▶  Google Gemini API (workout recommendations)
```

The backend is organized in layers: controllers expose the REST endpoints, services hold the business logic, and repositories handle persistence through JPA.

## Getting started

### Prerequisites
- Java 17+
- Maven
- Node.js 18+ and npm
- MySQL 8

### 1. Database
```sql
CREATE DATABASE gym_db;
```

### 2. Backend
Set the database credentials, JWT secret and Gemini API key in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym_db
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
jwt.secret=YOUR_SECRET
gemini.api.key=YOUR_GEMINI_API_KEY
```

You can get a Gemini API key from [Google AI Studio](https://aistudio.google.com/).

Then run:
```bash
cd backend
mvn spring-boot:run
```
The API starts on `http://localhost:8080`.

### 3. Frontend
```bash
cd frontend
npm install
npm run dev
```
The app opens on `http://localhost:5173`.

## Future improvements
- Self-service registration for new members
- Email notifications for expiring memberships
- Advanced reports and exports
- Dark / light theme

## Author

**Căpraru Vlad-Nicolae** — Technical University of Cluj-Napoca

[GitHub](https://github.com/VladCN03) · [LinkedIn](https://www.linkedin.com/in/vlad-capraru-0211l)
