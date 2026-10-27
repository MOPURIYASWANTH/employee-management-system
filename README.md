# Employee Management System API

A Java 21 / Spring Boot REST API for user sign-up/login and authenticated employee-record CRUD. It uses BCrypt password hashing, JWT bearer tokens, Spring Data JPA/Hibernate, MySQL, Flyway migrations, validation, consistent API errors, pagination/filtering, and Swagger UI.

## Requirements
- JDK 21
- Maven 3.9+ (or use `./mvnw` after adding the Maven Wrapper)
- MySQL 8.x

## 1. Create the database and configure credentials
The Flyway migration creates the tables automatically. Create the database:

```sql
CREATE DATABASE employee_management;
```

Set environment variables before starting the application. Do not commit real passwords or production JWT secrets.

**Windows PowerShell**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/employee_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
# Replace with a strong Base64-encoded secret (at least 32 random bytes):
$env:JWT_SECRET="YOUR_BASE64_ENCODED_SECRET"
```

The development defaults in `application.properties` are for local setup only. Override them using environment variables.

## 2. Run
From the project root:

```bash
mvn clean test
mvn spring-boot:run
```

The API runs at `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

## API endpoints

| Method | Endpoint | Auth | Purpose |
|---|---|---|---|
| POST | `/api/auth/signup` | No | Create account (role defaults to EMPLOYEE) |
| POST | `/api/auth/login` | No | Login and receive JWT |
| POST | `/api/employees` | Bearer token | Create an employee record |
| GET | `/api/employees` | Bearer token | List own records; supports `page`, `size`, `department` |
| GET | `/api/employees/{id}` | Bearer token | Read own record |
| PUT | `/api/employees/{id}` | Bearer token | Update own record |
| DELETE | `/api/employees/{id}` | Bearer token | Delete own record |

## Example requests

### Sign up
`POST /api/auth/signup`
```json
{
  "fullName": "Yaswanth Mopuri",
  "email": "yaswanth@example.com",
  "password": "StrongPass123"
}
```

### Login
`POST /api/auth/login`
```json
{ "email": "yaswanth@example.com", "password": "StrongPass123" }
```
Copy `accessToken` from the response. In Postman, choose **Authorization → Bearer Token** and paste the token for employee endpoints.

### Create employee
`POST /api/employees`
```json
{
  "name": "Ananya Rao",
  "email": "ananya@example.com",
  "role": "Software Engineer",
  "department": "Engineering",
  "hireDate": "2026-10-01"
}
```

### Filter and paginate
`GET /api/employees?page=0&size=10&department=Engineering`

## Security and data ownership
- Passwords are stored as BCrypt hashes, never as plain text.
- JWTs are stateless and have a configurable expiry.
- New accounts cannot choose their own ADMIN role; sign-up always assigns EMPLOYEE.
- Employee queries include the authenticated user's ID. A user cannot read, update, or delete another user's employee record by guessing its ID.
- Never use the sample JWT secret or local database defaults in production.

## Database schema / migration
`src/main/resources/db/migration/V1__create_users_and_employees.sql` is the versioned MySQL schema migration. It creates the user and employee tables, foreign key, unique constraints, and indexes. The assignment's core requirements mention MySQL while its submission note mentions a PostgreSQL dump; these are not interchangeable. Confirm the expected database format with the recruiter if possible.

## Submission checklist
1. Run `mvn clean test` and fix all failures.
2. Start the API against a clean local database and test signup, login, and every CRUD operation in Postman.
3. Verify missing/invalid token returns 401, invalid input returns 400, duplicate email returns 409, and another user's employee ID cannot be accessed.
4. Commit the source, migration, and this README to GitHub. Do not commit secrets or `target/`.
5. Invite GitHub account `iks-hiring` to the repository with access sufficient to view it.
6. If sending ZIP, include the project root with `pom.xml`, `src/`, and `README.md`; exclude `.env`, real credentials, IDE folders, and `target/`.
7. Send the repository URL or ZIP to the recruiter using the submission channel specified in the assignment or their email thread. The assignment itself does not provide a recipient email address.

## Current scope
Implemented: sign-up/login, BCrypt, JWT, owner-scoped employee CRUD, validation, consistent error responses, MySQL/Flyway schema, pagination/filtering, and Swagger/OpenAPI. Automated tests should be added and run before submission; do not claim the project is tested until you have run them locally.
