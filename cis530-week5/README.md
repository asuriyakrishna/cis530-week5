# University Student Services

REST API for managing students and their required profiles. The application uses a bidirectional one-to-one JPA relationship with separate `students` and `student_profiles` tables. The student entity owns the relationship and configures `CascadeType.ALL` and `orphanRemoval = true`.

## Run the application

Requires Java 25 or later. The project includes the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

By default the application uses an in-memory H2 database. To use MySQL instead, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` before starting; for example:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/cis530_week5"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
.\mvnw.cmd spring-boot:run
```

The H2 console is available at `http://localhost:8080/h2-console` when using the default H2 database. Use JDBC URL `jdbc:h2:mem:cis530_week5`, user `sa`, and a blank password.

## API

| Method | Path | Result |
| --- | --- | --- |
| `POST` | `/api/students` | Create student and profile (`201`) |
| `GET` | `/api/students` | List students |
| `GET` | `/api/students/{id}` | Fetch a student (`404` if missing) |
| `PUT` | `/api/students/{id}` | Update student and profile (`404` if missing) |
| `DELETE` | `/api/students/{id}` | Delete student and cascading profile (`204`) |

Create and update accept a required nested profile:

```json
{
  "firstName": "Taylor",
  "lastName": "Morgan",
  "email": "taylor.morgan@example.edu",
  "profile": {
    "phoneNumber": "555-0100",
    "address": "1 Campus Way",
    "major": "Computer Science",
    "dateOfBirth": "2003-06-15"
  }
}
```

Names, email, phone number, address, and major are required; email must be valid, and a supplied birth date must be in the past. Validation errors return `400` JSON with per-field details. A missing student returns `404`; conflicts such as a duplicate email return `409`. Responses use DTOs so the bidirectional entity association is never serialized recursively.

Import [`postman/Student-Services.postman_collection.json`](postman/Student-Services.postman_collection.json) into Postman. Create a student first, then set the `studentId` collection variable to the ID in the response to try get, update, and delete.

## Test

```powershell
.\mvnw.cmd test
```
