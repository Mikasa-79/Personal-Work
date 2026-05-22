---
name: campushub-frontend
description: CampusHub 校园互助服务平台的 Vue 3 前端开发 skill。Use when building or modifying frontend pages, components, routing, state management, API calls, forms, dashboards, validation, responsive UI, or frontend tests for the CampusHub course project.
---

# CampusHub Frontend

## Stack

Use this frontend stack unless the repository already chose another one:
- Vue 3 + TypeScript + Vite.
- Vue Router for pages.
- Pinia for session, user profile, request filters, and notification state.
- Element Plus for forms, tables, dialogs, menus, pagination, upload, and admin UI.
- Axios with a typed API client layer.
- ECharts for admin statistics.
- Vitest + Vue Test Utils for focused component and store tests.

## Product Surfaces

Build the actual app screens, not a marketing landing page:
- Auth: login/register, token persistence, logout.
- User: profile view/edit, role switch between requester and provider.
- Requests: publish form, list with category/time/location filters, detail page.
- Orders: accept, confirm, progress, complete, review flow.
- Notifications: in-site notification list and unread state.
- Admin: user table, request audit table, metrics dashboard.

Keep P0 screens complete before adding P2 polish.

## Architecture

Prefer this shape:
- `src/router/` for route definitions and guards.
- `src/api/` for typed API wrappers; avoid raw Axios calls inside views.
- `src/stores/` for auth, user, request filters, notifications.
- `src/views/` for route-level screens.
- `src/components/` for reusable forms, list items, status tags, dialogs.
- `src/types/` for DTOs shared across API calls.

Guard authenticated routes and admin routes. Redirect unauthenticated users to login while preserving the intended destination.

## UI Rules

CampusHub is an operational student service app. Use a clean, dense, task-focused UI:
- Make request publishing, browsing, accepting, and completing orders fast to scan.
- Use status tags for request/order state.
- Use tabs or segmented controls for "我发布的 / 我接单的" order views.
- Use forms with clear validation messages for title, category, location, time, and reward.
- Use empty, loading, error, disabled, and success states for every async screen.
- Keep core flows within three steps where practical.

Do not use decorative hero layouts for the main app. First screen after login should be a useful request list or dashboard.

## API Integration

Centralize API behavior:
- Attach JWT in an Axios interceptor.
- Normalize backend errors into user-facing messages.
- Keep request/response DTO names aligned with backend contracts.
- Avoid silently swallowing failed operations; show a toast or inline message.
- Use optimistic UI only for low-risk interactions, then reconcile from the server.

Typical modules:
- `authApi`: register, login, current user.
- `requestApi`: create, list, detail, update/audit.
- `orderApi`: accept, confirm, start, complete, review, history.
- `notificationApi`: list, mark read.
- `adminApi`: users, audits, metrics.

## Verification

For each feature, verify:
- Desktop and mobile layout do not overlap.
- Auth guard behavior works.
- Form validation blocks invalid submissions.
- API loading/error states are visible.
- P0 flows can be completed manually against backend mocks or real APIs.

Add tests for stores, API mappers, and components with branching behavior. Keep visual-only components light unless they encode important state logic.
