# Restful Booker API Automation

Portfolio-grade API automation framework using Java, Rest Assured, TestNG and Maven.

Base API: https://restful-booker.herokuapp.com

## Coverage

The framework covers the documented Restful Booker API surface:

- GET /ping
- POST /auth
- GET /booking
- GET /booking/{id}
- POST /booking
- PUT /booking/{id}
- PATCH /booking/{id}
- DELETE /booking/{id}

It also demonstrates:

- positive and negative testing
- authentication and authorization
- dynamic token and booking ID extraction
- CRUD state transitions
- PUT vs PATCH behavior
- query-parameter filtering
- content negotiation (JSON/XML)
- reusable RequestSpecification
- API client layer
- POJO serialization
- TestNG dependencies
- Maven/Surefire execution

## Run

mvn clean test

The hosted API resets its sample data periodically, so tests create their own booking data where state is required.

## Architecture

BaseTest -> ApiClient -> endpoint-specific clients -> tests
                         |
                         +-> payload/model objects

Tests own assertions. Client classes own HTTP interaction.
