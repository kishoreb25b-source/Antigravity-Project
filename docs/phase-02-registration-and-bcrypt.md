# Phase 2: Registration Flow and BCrypt Integration

## Purpose
Complete the end-to-end user registration flow by connecting the frontend HTML form to the backend Java HttpServer, setting up the MySQL database, and migrating the password hashing algorithm to BCrypt.

## Starting State
- Frontend UI (`index.html`, `script.js`) was built.
- Java Backend `Server.java` and `RegisterHandler.java` existed but the server was not actively running.
- `PasswordHasher.java` used PBKDF2WithHmacSHA512.
- Database credentials in `application.properties` were empty or incorrect.
- MySQL database `project2_db` was not yet created.

## Final Outcome
- The Java backend runs successfully on port 8080.
- The MySQL database `project2_db` is created and actively storing new user registrations.
- The frontend successfully sends POST requests to `/api/register` and displays success banners.
- Passwords are securely hashed using BCrypt.

## Implementation Journey
1. **Server Startup**: Discovered that `gradle :backend:run` was executing `App.java` instead of `Server.java`. Updated `build.gradle` to set `mainClass = 'com.example.Server'`.
2. **Database Connectivity Debugging**: Frontend reported connection errors. Diagnosed that the Java backend was throwing a `SQLException` due to missing database credentials and uncreated tables, causing the request to drop.
3. **MySQL Configuration**: Guided user to verify MySQL. The user set a root password (`kishore@10`).
4. **Schema Initialization**: Executed `database/schema.sql` to create `project2_db` and the `users` table. Updated `application.properties` with the correct root password.
5. **End-to-End Verification**: Confirmed that submitting the frontend form successfully persisted data to MySQL, verified via direct `SELECT * FROM project2_db.users` query.
6. **BCrypt Refactoring**: Swapped PBKDF2 for BCrypt based on verification checklist requirements. Updated `build.gradle` with `at.favre.lib:bcrypt:0.10.2`.
7. **Test Updates**: Modified `PasswordHasherTest.java` to account for BCrypt's internal salting and non-deterministic hashing.
8. **Final Verification**: Ran `gradle :backend:test`, all 4 tests passed successfully.

## Repository Changes
- `backend/build.gradle`: Modified `mainClass` to `com.example.Server`. Added dependency `at.favre.lib:bcrypt:0.10.2`.
- `backend/src/main/resources/application.properties`: Updated `db.password=kishore@10`.
- `backend/src/main/java/com/example/security/PasswordHasher.java`: Replaced PBKDF2 logic with BCrypt hashing (`BCrypt.withDefaults().hashToString`) and verification.
- `backend/src/test/java/com/example/security/PasswordHasherTest.java`: Updated tests to expect non-deterministic BCrypt hashes.

## Important Code and Configuration
- **Backend URL**: `http://localhost:8080/api/register`
- **Database URL**: `jdbc:mysql://localhost:3306/project2_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- **BCrypt Cost Factor**: `12`

## Commands and Operations
- Started backend: `gradle :backend:run`
- Executed schema: `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p"kishore@10" < database/schema.sql`
- Verified data: `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p"kishore@10" -e "SELECT * FROM project2_db.users;"`
- Ran tests: `gradle :backend:test`

## Verification
- **Automated Tests**: Ran `gradle :backend:test`. Result: `BUILD SUCCESSFUL in 11s`. All tests passed.
- **Manual End-to-End**: Verified via the browser UI and direct SQL queries that a user is successfully created in the database with a hashed password.

## Failures and Fixes
- **Failure**: Frontend `fetch` threw a generic connection error.
- **Root Cause**: Backend was throwing unhandled SQL exceptions because MySQL credentials were missing and the DB didn't exist.
- **Fix**: Setup MySQL, created `project2_db`, and updated `application.properties`.
- **Failure**: `Access denied for user 'root'@'localhost'`.
- **Fix**: User had left password empty; corrected by setting the valid password `kishore@10` in properties.

## Decisions and Rationale
- **BCrypt**: Transitioned to BCrypt to meet the strict verification checklist criteria. BCrypt embeds the salt into the hash, simplifying the `PasswordHasher` API.
- **No ORM**: Continued adhering to the strict "framework-free" and "plain JDBC" constraint per the project rules.

## Final Technical State
- Frontend form posts JSON to backend.
- Backend parses JSON without external libraries.
- Backend hashes password using BCrypt.
- Backend persists user to `project2_db.users` via plain JDBC.

## Deferred / Out of Scope
- Login endpoint and session management.
- Database connection pooling (HikariCP).

## Known Risks or Open Questions
- The custom JSON parser (`extractJsonField`) relies on regex and is brittle to formatting changes in the incoming JSON payload.

## Commit Linkage
- Not applicable (Git is not installed/configured in this environment).

## Resume From Here
- **Next Steps**: Begin implementing the Login flow (`login.html`, `LoginHandler.java`, `/api/login`).
- **Dependencies**: Ensure MySQL is running on port 3306 and `gradle :backend:run` is active.
