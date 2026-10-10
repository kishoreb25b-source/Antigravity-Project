# Phase 1: Initial Project Scaffolding and Architecture Setup

## Purpose
Establish the foundational architecture for a strict "framework-free" Java web application. Set up the multi-tier structure (frontend, backend, database), implement the core HTTP server using standard Java libraries, and create the initial data models and repositories for user registration.

## Starting State
- Empty workspace in `C:\Users\Lenovo\OneDrive\Documents\PROJECT2.0`.
- Constraints: No frameworks (no Spring, no ORM, no JWT), Windows 11, Java 21, Gradle 8.12.

## Final Outcome
- **Project Structure**: Organized into `frontend/`, `backend/`, and `database/` directories.
- **Frontend**: Created `index.html`, `style.css`, and `script.js` with client-side validation for user registration.
- **Backend (HTTP)**: Configured a raw `com.sun.net.httpserver.HttpServer` in `Server.java` bound to port 8080, with a `RegisterHandler` to parse incoming JSON payloads and handle CORS headers.
- **Backend (Service)**: Created `RegistrationService.java` to handle business logic and validation.
- **Backend (Security)**: Implemented `PasswordHasher.java` originally using `PBKDF2WithHmacSHA512` for secure password hashing and salting.
- **Backend (Data)**: Implemented `UserRepository.java` using plain JDBC `PreparedStatement` to interact with MySQL, and `DatabaseConfig.java` to load credentials from `application.properties`.
- **Database**: Designed `schema.sql` defining the `users` table with appropriate constraints.
- **Build System**: Configured `build.gradle` to pull in `mysql-connector-j` and JUnit 5 for testing.

## Implementation Journey
1. **Directory Setup**: Created the overarching structure for separation of concerns.
2. **Database Layer**: Designed the MySQL schema (`database/schema.sql`). Implemented plain JDBC repository (`UserRepository.java`) and configuration logic (`DatabaseConfig.java`).
3. **Security Layer**: Implemented `PBKDF2WithHmacSHA512` hashing via `PasswordHasher.java` with unit tests ensuring salt uniqueness and constant-time comparison.
4. **Service Layer**: Implemented `RegistrationService.java` to handle validation, business logic, and coordination between the repository and security layers.
5. **HTTP Layer**: Implemented `Server.java` (using `com.sun.net.httpserver.HttpServer`) and `RegisterHandler.java` to route `POST /api/register` requests.
6. **Frontend Layer**: Implemented the registration UI (`index.html`, `style.css`) and JS controller (`script.js`) with client-side validation and `fetch()` logic.
7. **Build Fixes**: Discovered that OneDrive file-locking caused random `compileJava` failures (`classes` directory locked). 

## Repository Changes
- Created `frontend/index.html` (Registration form UI).
- Created `frontend/css/style.css` (Premium, dynamic styling).
- Created `frontend/js/script.js` (Client-side validation and fetch logic).
- Created `backend/build.gradle` (Java application plugin, MySQL connector, JUnit 5).
- Created `backend/src/main/resources/application.properties` (Database config properties).
- Created `backend/src/main/java/com/example/Server.java` (HTTP Server entry point).
- Created `backend/src/main/java/com/example/api/RegisterHandler.java` (JSON parsing and CORS).
- Created `backend/src/main/java/com/example/service/RegistrationService.java` (Business logic).
- Created `backend/src/main/java/com/example/model/User.java` (Data model).
- Created `backend/src/main/java/com/example/repository/UserRepository.java` (JDBC data access).
- Created `backend/src/main/java/com/example/config/DatabaseConfig.java` (Properties loader).
- Created `backend/src/main/java/com/example/security/PasswordHasher.java` (PBKDF2 implementation).
- Created `database/schema.sql` (MySQL schema definition).

## Important Code and Configuration
- **Build Directory Workaround**: Set `layout.buildDirectory.set(file("${System.getProperty('user.home')}/.gradle-builds/project2.0/backend"))` to bypass OneDrive locking issues.
- **JSON Parsing**: Implemented custom regex-based `extractJsonField` to avoid external JSON dependencies (e.g., Jackson/Gson).

## Commands and Operations
- Scaffolding file creation.
- Initial Gradle builds: `gradle :backend:compileJava`
- Testing security logic: `gradle :backend:test --tests "com.example.security.*"`

## Verification
- **Automated Tests**: Initial unit tests for `PasswordHasherTest.java` and `UserRepositoryTest.java` passed successfully in the terminal.
- **Build**: `gradle :backend:compileJava` successfully compiled all classes.

## Failures and Fixes
- **Failure**: Gradle `compileJava` randomly failed with `Task :backend:compileJava FAILED` due to `.class` files being locked.
- **Root Cause**: OneDrive sync engine locks compiled `.class` files immediately after they are generated.
- **Fix**: Redirected the Gradle build output directory outside of the OneDrive folder to `~/.gradle-builds/`.

## Decisions and Rationale
- **Strict Framework-Free**: Relied exclusively on standard Java libraries (`com.sun.net.httpserver`, `java.sql`, `java.security`) to adhere to the core constraint.
- **Regex JSON Parsing**: Avoided adding Jackson/Gson to remain as close to standard library boundaries as possible, though acknowledging it is brittle.
- **PBKDF2 Hashing**: Selected PBKDF2 as the initial hashing algorithm because it is built into the JDK (`SecretKeyFactory`), avoiding external dependencies initially.

## Final Technical State
- The complete multi-tier architecture is established on disk.
- The backend compiles successfully.
- The unit tests pass.
- (Integration and end-to-end functionality were deferred to Phase 2).

## Deferred / Out of Scope
- Actually spinning up the database server and verifying the end-to-end data flow (Completed in Phase 2).
- Migrating to BCrypt (Completed in Phase 2).

## Known Risks or Open Questions
- None.

## Commit Linkage
- Not applicable (Git is not installed/configured).

## Resume From Here
- Proceed to Phase 2: End-to-end integration, database initialization, and BCrypt migration.
