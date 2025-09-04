# Product Catalog API

A REST API application for serving a product catalog with automatic discount calculation based on category and SKU rules.

## Features

- ✅ List all products with automatic discount calculation
- ✅ Filter products by category
- ✅ Sort products by SKU, Price, Description, or Category
- ✅ Pagination support
- ✅ OpenAPI/Swagger documentation
- ✅ Comprehensive unit and integration tests

## Discount Rules

The API automatically applies discounts based on the following rules:
- **Electronics**: 15% discount
- **Home & Kitchen**: 25% discount
- **SKUs ending in '5'**: 30% special discount

> **Note**: Only one discount is applied at a time, with priority given to the highest discount percentage.

## Technology Stack

- **Kotlin** 1.9.25
- **Spring Boot** 3.5.5
- **Java SDK** Temurin 24
- **Gradle** Build System
- **JUnit 5** & **MockK** for Testing
- **SpringDoc OpenAPI** for API Documentation

## Getting Started

### Prerequisites

- Java 24 (Eclipse Temurin 24.0.2)
- Gradle 8.x

### Running the Application

```bash
# Build the application
./gradlew build

# Run tests
./gradlew test

# Start the application
./gradlew bootRun
```

The application will start on `http://localhost:8080/api`

### API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/api-docs`

## API Endpoints

### Get Products

```
GET /api/products
```

#### Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| category | string | No | Filter by category | Electronics |
| sortBy | string | No | Sort field (SKU, Price, Description, Category) | Price |
| sortDirection | string | No | Sort direction (ASC, DESC) | DESC |
| page | integer | No | Page number (0-based, default: 0) | 0 |
| size | integer | No | Page size (default: 20, max: 100) | 10 |

#### Example Requests

```bash
# Get all products
curl http://localhost:8080/api/products

# Filter by Electronics category
curl "http://localhost:8080/api/products?category=Electronics"

# Sort by price descending
curl "http://localhost:8080/api/products?sortBy=price&sortDirection=DESC"

# Get paginated results (pagination is always active)
curl "http://localhost:8080/api/products?page=0&size=5"

# Combined filtering, sorting, and pagination
curl "http://localhost:8080/api/products?category=Electronics&sortBy=price&sortDirection=DESC&page=0&size=3"
```

#### Response Format

```json
{
  "content": [
    {
      "sku": "SKU0005",
      "description": "Noise-Cancelling Over-Ear Headphones",
      "category": "Electronics",
      "price": 120.00,
      "discount": 30,
      "finalPrice": 84.00
    }
  ],
  "pageable": {...},
  "totalElements": 30,
  "totalPages": 6,
  "number": 0,
  "size": 5
}
```

> **Note**: The `discount` field is always present in the response. Products without applicable discounts will have `discount: 0`.

## Architectural Decisions

### 1. Layered Architecture

The application follows a clean layered architecture pattern:
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic and discount calculations
- **Repository Layer**: Manages data access (currently in-memory)
- **Domain/DTO Layer**: Defines data models

This separation ensures:
- Clear separation of concerns
- Easy testing of individual components
- Flexibility to change data storage without affecting business logic

### 2. In-Memory Data Storage

For this implementation, products are stored in-memory within the `ProductRepository`. This decision was made because:
- The product catalog is small and static (30 products)
- Simplifies deployment and testing
- No database setup required
- Can easily be replaced with a database implementation by changing the repository layer

### 3. Discount Calculation Strategy

Discounts are calculated at runtime in the service layer rather than being stored:
- Ensures discount rules are always consistently applied
- Easy to modify discount rules without data migration
- Supports complex discount logic (priority-based selection)
- Maintains single source of truth for discount rules

### 4. Response Model Separation

Using separate `Product` (domain) and `ProductResponse` (DTO) models:
- Allows internal representation to differ from API response
- Includes calculated fields (discount, finalPrice) only in response
- Provides flexibility for future API versioning
- Follows API best practices for response formatting

### 5. Pagination Implementation

Database-level pagination using Spring Data JPA:
- Mandatory pagination for all requests (prevents accidental large data transfers)
- Efficient database queries with LIMIT/OFFSET
- Default page size of 20, maximum 100 items per page
- Integrated sorting with pagination for optimal performance

### 6. Testing Strategy

Comprehensive testing approach:
- **Unit Tests**: Test individual components (DiscountService, ProductService)
- **Integration Tests**: Test full API endpoints with Spring context
- **Mocking**: Using MockK for isolating components in unit tests
- Ensures high code coverage and reliability

### 7. API Documentation

OpenAPI/Swagger integration provides:
- Interactive API documentation
- Auto-generated from code annotations
- Always up-to-date with implementation
- Useful for API consumers and testing

### 8. Configuration Externalization

Application properties are externalized:
- Easy configuration changes without code modification
- Environment-specific configurations possible
- Follows Spring Boot best practices

## Production Considerations

For production deployment, consider:

1. **Database Integration**: Replace in-memory storage with a proper database
2. **Caching**: Add caching layer for frequently accessed products
3. **Security**: Implement authentication and authorization
4. **Monitoring**: Add metrics and health checks
5. **Rate Limiting**: Protect API from abuse
6. **Error Handling**: Enhance error responses with proper error codes
7. **Logging**: Implement structured logging for debugging
8. **Performance**: Consider async processing for heavy operations

## Testing

The application includes comprehensive test coverage:

```bash
# Run all tests
./gradlew test

# Run with coverage report
./gradlew test jacocoTestReport
```

Test categories:
- **Unit Tests**: Service layer logic and discount calculations
- **Integration Tests**: Full API endpoint testing with Spring context

## Future Enhancements

Potential improvements for future iterations:
- Add product search functionality
- Implement product CRUD operations
- Support multiple currencies
- Add product images and detailed descriptions
- Implement user-specific discounts
- Add inventory management
- Support bulk operations
- Implement GraphQL API alongside REST

## License

This project is part of a technical assessment for mytheresa.