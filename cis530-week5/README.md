# University Student Services

REST API for managing students and their required profiles. The application uses a bidirectional one-to-one JPA relationship with separate `students` and `student_profiles` tables. The student entity owns the relationship and configures `CascadeType.ALL` and `orphanRemoval = true`.

## Run the application

Requires Java 25 or later. The project includes the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

By default the application connects to the MySQL `cis530_week5` schema on `localhost:3306`. Create the schema first and set `DB_USERNAME` and `DB_PASSWORD` to match your MySQL account. To connect to a different MySQL server or schema, set `DB_URL` as well:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
.\mvnw.cmd spring-boot:run
```

Tests use an isolated in-memory H2 database. To run the application against H2 manually, set `DB_URL` to `jdbc:h2:mem:cis530_week5;DB_CLOSE_DELAY=-1`, set `DB_USERNAME=sa` and `DB_PASSWORD` to an empty string, and enable the console with `H2_CONSOLE_ENABLED=true`. The H2 console is at `http://localhost:8080/h2-console`.

## API

| Method | Path | Result |
| --- | --- | --- |
| `POST` | `/api/students` | Create student and profile (`201`) |
| `GET` | `/api/students` | List students |
| `GET` | `/api/students/{id}` | Fetch a student (`404` if missing) |
| `PUT` | `/api/students/{id}` | Update student and profile (`404` if missing) |
| `DELETE` | `/api/students/{id}` | Delete student and cascading profile (`204`) |
| `GET` | `/api/students/{id}/profile` | Fetch the student's profile |
| `GET` | `/api/students/major/{major}` | Find students by major |
| `GET` | `/api/students/year/{year}` | Find students by enrollment year |

Create and update accept a `Student` JSON object with the required profile nested in the same body. The controller validates the entity and cascades persistence to its profile:

```json
{
  "firstName": "Taylor",
  "lastName": "Morgan",
  "email": "taylor.morgan@example.edu",
  "major": "Computer Science",
  "gpa": 3.75,
  "enrollmentYear": 2025,
  "profile": {
    "phone": "555-0100",
    "address": "1 Campus Way",
    "city": "Omaha",
    "state": "NE",
    "zip": "68123",
    "emergencyContactName": "Morgan Taylor",
    "emergencyContactPhone": "555-0101"
  }
}
```

The `students` table maps first name, last name, email, major, GPA, enrollment year, and profile foreign key to the assignment's required column names. The `student_profiles` table stores phone, address, city, two-character state, five-digit ZIP code, and emergency contact details. Names must contain 2-50 characters, email must be valid, GPA must be between 0.0 and 4.0, and enrollment year must be from 2000 through the current calendar year. Profile phone numbers must contain 7-15 digits and use standard phone punctuation. Validation errors return `400` JSON with per-field names and messages. The Postman collection includes two invalid-request examples. Missing students return `404`; a duplicate email returns `409` with a specific message, while other database-constraint conflicts return a general `409`. Since MySQL data persists across restarts, use a new email address for each create request or update an existing student. Responses use DTOs so the bidirectional entity association is never serialized recursively.

Import [`postman/Student-Services.postman_collection.json`](postman/Student-Services.postman_collection.json) into Postman. Create a student first, then set the `studentId` collection variable to the ID in the response to try get, update, and delete.

## Test

```powershell
.\mvnw.cmd test
```
