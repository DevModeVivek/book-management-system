# Book Management System

A Spring Boot application for managing books with Google Books API integration, role-based security, and interface-based dependency injection.

## Features

- Complete CRUD operations for books
- External Google Books API integration
- Interface-based architecture for clean dependency injection
- Role-based security (Admin/User)
- Search functionality
- H2 in-memory database
- Swagger API documentation
- Comprehensive testing

## Architecture

The application uses interface-based dependency injection for clean separation of concerns:

- **Business Services**: `IBookService`, `IGoogleBooksService`
- **External Services**: `IHttpClientService`, `IJsonProcessingService`

This design provides loose coupling, easy testing, and flexible implementation swapping.

## Tech Stack

- Java 17
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA
- H2 Database
- Swagger/OpenAPI 3
- Gradle

## Getting Started

1. **Clone and build**:
   ```bash
   git clone <repository-url>
   cd book-management-system
   ./gradlew clean build
   ```

2. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access the application**:
   - Application: http://localhost:8080/api
   - Health Check: http://localhost:8080/api/actuator/health
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - H2 Console: http://localhost:8080/api/h2-console

## Authentication

### Admin User (Full Access)
- Username: `admin`
- Password: `admin123`
- Can manage all books

### Regular User (Read-Only)
- Username: `user` 
- Password: `user123`
- Can only search external books

## Database Access

**H2 Console**: http://localhost:8080/api/h2-console
- JDBC URL: `jdbc:h2:mem:bookdb`
- Username: `sa`
- Password: `password`

## API Examples

### Create a Book (Admin only)
```bash
curl -X POST http://localhost:8080/api/books \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert Martin",
    "isbn": "9780132350884",
    "publishedDate": "2008-08-01"
  }'
```

### Search External Books (Any user)
```bash
curl -u user:user123 \
  "http://localhost:8080/api/books/external/search?query=spring boot"
```

### Get All Books (Admin only)
```bash
curl -u admin:admin123 http://localhost:8080/api/books
```

## Testing

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## Troubleshooting

**Port 8080 in use**: `lsof -ti:8080 | xargs kill -9`

**Database issues**: Check H2 console with exact JDBC URL above

**Auth issues**: Verify Basic Auth credentials

## Project Structure

```
src/main/java/com/vivek/bookms/
├── config/          # Spring configurations
├── controller/      # REST controllers  
├── service/         # Business logic (interfaces + implementations)
├── repository/      # Data access
├── entity/          # JPA entities
├── dto/            # Data transfer objects
├── exception/      # Error handling
└── util/           # Utilities
```

---

Made with ❤️ by Vivek
