# Book Management System

A comprehensive Spring Boot application for managing books with external Google Books API integration, role-based security, and comprehensive CRUD operations.

## 🚀 Features

- **Complete CRUD Operations**: Create, Read, Update, Delete books
- **Google Books API Integration**: Search external books by title or author
- **Role-Based Security**: Admin and User roles with different permissions
- **Search Functionality**: Search books by title or author in local database
- **H2 Database**: In-memory database for development and testing
- **API Documentation**: Swagger/OpenAPI integration
- **Request Logging**: Comprehensive request/response logging
- **Error Handling**: Global exception handling with proper HTTP status codes
- **Input Validation**: Bean validation for data integrity
- **Integration Tests**: Comprehensive test coverage

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 2.7.18**
- **Spring Security** - Authentication and Authorization
- **Spring Data JPA** - Database operations
- **H2 Database** - In-memory database
- **Swagger/OpenAPI 3** - API documentation
- **Jackson** - JSON processing
- **Gradle** - Build tool
- **JUnit 5** - Testing framework
- **SLF4J + Logback** - Logging

## 📋 Prerequisites

- Java 17 or higher
- Gradle 8.x (included via wrapper)
- Internet connection (for Google Books API)

## ⚙️ Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd book-management-system
   ```

2. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

3. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**:
   - **Base Application URL**: http://localhost:8080/api
   - **Health Check**: http://localhost:8080/api/actuator/health ✅ (Working)
   - **H2 Console**: http://localhost:8080/api/h2-console ✅ (Use settings below)
   - **Swagger UI**: http://localhost:8080/api/swagger-ui.html
   - **API Documentation**: http://localhost:8080/api/v3/api-docs
   - **Book API**: http://localhost:8080/api/books ✅ (Requires auth)

## 🔐 Security Configuration

The application uses HTTP Basic Authentication with two predefined users:

### Admin User (Full Access)
- **Username**: `admin`
- **Password**: `admin123`
- **Permissions**: Full CRUD operations on books

### Regular User (Limited Access)
- **Username**: `user`
- **Password**: `user123`
- **Permissions**: Access to external Google Books search only

## 📊 Database Configuration

### H2 Console Access
- **URL**: http://localhost:8080/api/h2-console
- **Setting Name**: Generic H2 (Embedded)
- **Driver Class**: org.h2.Driver
- **JDBC URL**: `jdbc:h2:mem:bookdb`
- **Username**: `sa`
- **Password**: `password`

**⚠️ Important**: Use the exact JDBC URL above. Do NOT use `jdbc:h2:~/test`

## 🌐 API Endpoints

### Book Management (Admin Only)

#### Get All Books
```http
GET http://localhost:8080/api/books
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Get Book by ID
```http
GET http://localhost:8080/api/books/{id}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Create New Book
```http
POST http://localhost:8080/api/books
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "isbn": "9780743273565",
  "publishedDate": "1925-04-10"
}
```

#### Update Book
```http
PUT http://localhost:8080/api/books/{id}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json

{
  "title": "Updated Title",
  "author": "Updated Author",
  "isbn": "9780743273565",
  "publishedDate": "1925-04-10"
}
```

#### Delete Book
```http
DELETE http://localhost:8080/api/books/{id}
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### Search Local Books
```http
GET http://localhost:8080/api/books/search?query=gatsby
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### External Google Books API (User & Admin)

#### Search External Books
```http
GET http://localhost:8080/api/books/external/search?query=java programming
Authorization: Basic dXNlcjp1c2VyMTIz
```

#### Search by Title
```http
GET http://localhost:8080/api/books/external/search/title?title=clean code
Authorization: Basic dXNlcjp1c2VyMTIz
```

#### Search by Author
```http
GET http://localhost:8080/api/books/external/search/author?author=robert martin
Authorization: Basic dXNlcjp1c2VyMTIz
```

## 📝 Sample API Responses

### Success Response Format
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "id": 1,
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "isbn": "9780743273565",
    "publishedDate": "1925-04-10"
  }
}
```

### Error Response Format
```json
{
  "success": false,
  "message": "Book not found with ID: 999",
  "data": null
}
```

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

### Run Integration Tests Only
```bash
./gradlew test --tests "*Integration*"
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/vivek/bookms/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions
│   │   ├── interceptor/     # Request interceptors
│   │   ├── mapper/          # Object mappers
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic
│   │   └── util/            # Utility classes
│   └── resources/
│       ├── application.yaml      # Main configuration
│       ├── application-dev.yaml  # Development profile
│       └── application-prod.yaml # Production profile
└── test/
    ├── java/                # Test classes
    └── resources/
        └── application-test.yaml # Test configuration
```

## 🔧 Configuration

### Application Profiles

- **default**: Uses H2 in-memory database
- **dev**: Development configuration with detailed logging
- **prod**: Production configuration with optimized settings
- **test**: Test configuration for integration tests

### Environment Variables (Optional)

- `GOOGLE_BOOKS_API_KEY`: Google Books API key for enhanced rate limits
- `SPRING_PROFILES_ACTIVE`: Active profile (dev/prod/test)

## 📋 Common Operations

### Adding Sample Data via API

```bash
# Create a sample book (as admin)
curl -X POST http://localhost:8080/api/books \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "publishedDate": "2008-08-01"
  }'
```

### Search External Books

```bash
# Search Google Books API (as user)
curl -X GET "http://localhost:8080/api/books/external/search?query=spring boot" \
  -u user:user123
```

## 🐛 Troubleshooting

### Common Issues

1. **Port 8080 already in use**:
   ```bash
   # Find and kill process using port 8080
   lsof -ti:8080 | xargs kill -9
   ```

2. **Database connection issues**:
   - Ensure H2 console settings match configuration
   - Check logs for detailed error messages

3. **Authentication failures**:
   - Verify credentials (admin/admin123 or user/user123)
   - Check request headers for proper Basic Auth encoding

4. **Google Books API issues**:
   - Check internet connectivity
   - API may have rate limits without API key

### Viewing Logs

```bash
# Real-time log viewing
tail -f logs/book-management-system.log
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, please create an issue in the repository or contact the development team.

---

**Built with ❤️ by Vivek**
