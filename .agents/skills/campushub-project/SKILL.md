---
name: campushub-project
description: CampusHub 校园互助服务平台课程项目的 AI 协同全栈开发总览。Use when planning requirements, architecture, milestones, MVP scope, AI collaboration logs, engineering deliverables, or coordinating frontend/backend/database work for the CampusHub software engineering course project.
---

# CampusHub Project

## Project Context

CampusHub 是软件工程与计算 II 的 10 周课程项目：面向校园学生的互助服务平台，解决快递代取、学习辅导、二手交易、活动组队等需求分散、匹配低效、缺乏信任机制的问题。

主要角色：
- 需求方：发布互助需求的学生。
- 服务方：接单并提供服务的学生。
- 管理员：负责用户管理、内容审核、数据统计。

## Recommended Stack

Use a conservative frontend/backend separation that a 4-person student team can finish in 240 person-hours:
- Frontend: Vue 3, TypeScript, Vite, Vue Router, Pinia, Element Plus, Axios, ECharts, Vitest.
- Backend: Java 17, Spring Boot 3, Spring Security, JWT, MyBatis-Plus, Bean Validation, springdoc-openapi, JUnit 5.
- Database: MySQL 8, Flyway migrations, Redis optional for notification/session/cache use cases.

Use the matching specialized skills when implementation begins:
- Use `$campushub-frontend` for UI pages, routing, state, API clients, forms, admin dashboard, and frontend tests.
- Use `$campushub-backend` for REST APIs, auth, business services, order state machine, notifications, admin APIs, and backend tests.
- Use `$campushub-database` for ER modeling, schema migration, indexes, constraints, seed data, and SQL review.

## MVP Scope

Prioritize P0 before adding optional features:
- P0: user registration/login/profile.
- P0: request publish/list/detail/filter.
- P0: complete order flow from accepting to completion.
- P1: rating and credit score.
- P1: in-site notifications.
- P2: basic recommendation, admin dashboard, one-to-one messaging.

When time is tight, keep P2 features as documented extension points instead of half-finished production code.

## Domain Model

Treat these as first-class modules:
- User and profile: account, role switching, avatar, nickname, college, contact.
- Help request: title, description, category, location, expected time, reward/exchange condition, status.
- Order: requester, provider, request, status, timestamps, confirmation fields.
- Review and credit: mutual rating, text review, credit score and level.
- Notification/message: order status changes, accepted orders, reviews, optional chat.
- Admin: user disable/enable, content audit, platform metrics.

Keep the order state machine explicit: `accepted -> confirmed -> in_progress -> completed -> reviewed`. Model cancellation or rejection only after the P0 path works.

## AI Collaboration Workflow

For every major AI-assisted task, ask the agent to produce:
- What it changed or decided.
- What assumptions it made.
- What risks or missing context remain.
- How to verify the result.

For course reflection logs, record the three required questions:
- AI helped where?
- AI misled or missed what?
- What prompt or process will be improved next stage?

Keep prompt logs attached to concrete artifacts: requirement text, API diff, schema diff, test output, screenshots, or review notes.

## Engineering Rules

Preserve traceability from requirement to implementation:
- Each P0 feature should have a user story, API endpoint, database entity/table, frontend view, and at least one verification path.
- Prefer small vertical slices: build login, request publishing, and order flow end to end before broadening feature count.
- Use ADR-style notes for major choices: stack, auth strategy, order state machine, database migration, deployment shape.
- Keep security visible: password hashing, authorization checks, input validation, SQL injection protection, XSS-safe rendering.
- Keep core operations within three user steps where possible.

## Delivery Checklist

Before considering a phase complete, verify:
- Requirements and implemented behavior match.
- Key modules can run locally.
- P0 flows have tests or scripted manual verification.
- API contracts are documented.
- Database migrations are repeatable.
- AI collaboration evidence is saved for the corresponding phase.
