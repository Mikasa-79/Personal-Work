---
name: campushub-database
description: CampusHub 校园互助服务平台的 MySQL 数据库建模 skill。Use when designing or modifying ER models, SQL migrations, tables, indexes, constraints, seed data, query plans, Redis usage, or database tests for the CampusHub course project.
---

# CampusHub Database

## Stack

Use this data stack unless the repository already chose another one:
- MySQL 8 as the primary relational database.
- Flyway for repeatable schema migrations.
- Redis optional for unread notification counters, rate limiting, or short-lived cache.
- MyBatis-Plus entity mapping on the backend.

Keep database design simple enough for a 10-week student project: strong relational constraints for core flows, lightweight optional tables for P2 features.

## Core Tables

Start from these tables:
- `users`: account, password hash, role/admin flag, status, created time.
- `user_profiles`: nickname, avatar URL, college, contact, credit score.
- `help_requests`: publisher, category, title, description, location, expected time, reward, status, audit status.
- `orders`: request, requester, provider, status, transition timestamps.
- `reviews`: order, reviewer, reviewee, rating, comment.
- `notifications`: receiver, type, title, content, read flag, related entity.
- `messages`: optional P2 one-to-one chat messages.
- `audit_logs`: optional admin audit trail.

Use `created_at`, `updated_at`, and soft-delete or status fields where the UI needs history.

## Relationships

Preserve these relationships:
- One user has one profile.
- One user publishes many help requests.
- One help request can have at most one active order in the MVP.
- One order connects requester, provider, and request.
- One order can have up to two reviews, one per party.
- Notifications belong to one receiver and may reference an order/request/review.

Prefer foreign keys for P0 entities unless the chosen framework/test setup makes them unusually costly. Document any decision to skip them.

## Status Fields

Use explicit enums mapped to strings or small integers:
- Request status: `OPEN`, `LOCKED`, `DONE`, `CANCELLED`.
- Audit status: `PENDING`, `APPROVED`, `REJECTED`.
- Order status: `ACCEPTED`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `REVIEWED`, `CANCELLED`.
- User status: `ACTIVE`, `DISABLED`.

Keep enum values synchronized with backend DTOs and frontend status tags.

## Indexing

Add indexes for expected query paths:
- `users.student_no` or login identifier unique index.
- `help_requests.category`, `status`, `audit_status`, `expected_time`, `created_at`.
- Composite index for request browsing such as `(audit_status, status, category, created_at)`.
- `orders.requester_id`, `orders.provider_id`, `orders.status`.
- `notifications.receiver_id`, `read_flag`, `created_at`.

Avoid premature indexes for P2 recommendation until real query patterns exist.

## Migration Rules

Use Flyway migration files such as `V1__init_schema.sql`:
- Make migrations deterministic and re-runnable from an empty database.
- Do not edit an applied migration in shared branches; add a new migration.
- Keep seed data separate from schema when possible.
- Include minimal demo data for login, request list, order flow, and admin review.

## Verification

For every schema change, check:
- Backend entity fields match column names and nullability.
- Required P0 flows can be represented without nullable workarounds.
- Invalid order/review duplicates are blocked by constraints or service checks.
- Common list queries have indexes.
- Test database can migrate from empty state.
