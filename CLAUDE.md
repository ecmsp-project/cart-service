# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Spring Boot microservice for managing shopping carts in an e-commerce platform. The service handles adding, removing, and updating products in user carts, and integrates with other services through Kafka messaging and gRPC for product reservations.

## Technology Stack
- Java 21
- Spring Boot 3.5.3
- Spring Data JPA with PostgreSQL
- Spring Cloud Contract for testing
- Kafka for messaging
- gRPC for service communication
- Lombok for code generation
- Maven for dependency management

## Common Development Commands

### Database
```bash
# Start PostgreSQL database
docker-compose -f docker/db/docker-compose.yml up -d

# Stop database
docker-compose -f docker/db/docker-compose.yml down
```

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Package the application
./mvnw clean package

# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=ClassName#methodName
```

## Architecture

### Package Structure
- `controller/` - REST API endpoints (`/api/carts`)
- `service/` - Business logic layer
- `repository/` - Data access layer (JPA repositories)
- `domain/` - Entity classes and value objects
- `dto/` - Data Transfer Objects for API communication
- `kafka/` - Kafka message producers
- `grpc/` - gRPC client communication

### Key Components
- **CartController**: REST API for cart operations
- **CartService**: Core business logic for cart management
- **ReservationService**: Handles product reservations via gRPC
- **OrderKafkaProducer**: Publishes order events to Kafka
- **CartRepository & CartProductRepository**: JPA data access

### Database Configuration
- PostgreSQL database `shopdb` on localhost:5432
- Default credentials: root/root
- JPA with Hibernate, DDL auto-generation disabled
- SQL logging enabled for development

### Current Limitations
- User authentication via JWT is not implemented (hardcoded UserId(1))
- PathVariable mapping issue in CartController:34 (`@PathVariable("id")` without corresponding path)

## Development Notes
- The service uses domain-driven design with value objects like `UserId`
- DTOs are built with Lombok builders
- Transactional service methods ensure data consistency
- Spring Cloud Contract is configured for contract testing with JUnit 5