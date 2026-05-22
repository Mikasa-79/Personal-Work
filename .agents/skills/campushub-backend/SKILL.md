---
name: campushub-backend
description: CampusHub 校园互助服务平台的 Spring Boot 后端开发 skill。Use when building or modifying REST APIs, authentication, authorization, service logic, order workflow, notifications, admin endpoints, API docs, or backend tests for the CampusHub course project.
---

# CampusHub Backend

## Stack

Use this backend stack unless the repository already chose another one:
- Java 17 + Spring Boot 3.
- Spring Web for REST APIs.
- Spring Security + JWT for authentication and authorization.
- MyBatis-Plus for CRUD and query wrappers.
- Bean Validation for request DTO validation.
- springdoc-openapi for API documentation.
- JUnit 5 + Mockito + Spring Boot Test for tests.

Keep the backend stateless where possible. Use Redis only for clearly justified cases such as notification unread counters, rate limiting, or short-lived verification/session data.

## Module Boundaries

Prefer package boundaries like:
- `auth`: registration, login, JWT, password hashing.
- `user`: profile, role switch, public user summary.
- `request`: help request publishing, browsing, filtering, detail, audit status.
- `order`: accept/confirm/start/complete/review state machine.
- `review`: rating, text review, credit score update.
- `notification`: in-site notifications for order and review events.
- `admin`: user disable/enable, request audit, metrics.

Use controller DTOs instead of exposing persistence entities directly.

## API Style

Design REST endpoints around user tasks:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/me`
- `PUT /api/users/me`
- `POST /api/requests`
- `GET /api/requests`
- `GET /api/requests/{id}`
- `POST /api/orders/{requestId}/accept`
- `POST /api/orders/{id}/confirm`
- `POST /api/orders/{id}/start`
- `POST /api/orders/{id}/complete`
- `POST /api/orders/{id}/reviews`
- `GET /api/notifications`
- `POST /api/notifications/{id}/read`

Return consistent response shapes and HTTP statuses. Validate all input DTOs at the controller boundary.

## Security Rules

Apply these rules by default:
- Hash passwords with BCrypt.
- Never return password hashes or sensitive tokens in user DTOs.
- Enforce ownership checks in service methods, not only in the UI.
- Use role checks for admin endpoints.
- Reject invalid order transitions.
- Use parameterized queries/MyBatis-Plus wrappers; do not concatenate SQL.
- Sanitize or escape user-generated text on display, and avoid storing executable HTML.

## Order Workflow

Keep the P0 state machine explicit:
- `ACCEPTED`: provider accepts the request.
- `CONFIRMED`: requester confirms the provider.
- `IN_PROGRESS`: service is being performed.
- `COMPLETED`: both sides complete/confirm the order.
- `REVIEWED`: review and credit update finished.

Each transition must check:
- Current status.
- Current user permission.
- Whether the required actor is requester, provider, or admin.
- Whether related request status should change.

## Testing

Prioritize tests around business risk:
- Auth registration/login and password hashing.
- Request publish/list/detail filters.
- Order transition success and failure paths.
- Authorization and ownership denial.
- Review credit score calculation.
- Admin audit effects.

Use integration tests for controller/service/database paths when behavior crosses module boundaries. Keep unit tests for pure calculation and state transition helpers.
