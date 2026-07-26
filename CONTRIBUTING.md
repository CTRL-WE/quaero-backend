# Contributing to QUAERO Backend

Thank you for contributing to **QUAERO** — a community-driven media and information literacy platform. This document defines how every backend developer contributes to this repository. It assumes familiarity with the Backend Master Reference; this file governs *process*, not architecture.

**Team:** CTRL+WE

---

## 1. Introduction

QUAERO's backend is a Spring Boot monolith, organized package-by-feature, built by a small team working in parallel across clearly owned modules. This guide exists so that every contribution — regardless of who makes it — follows the same branching, commit, review, and quality process. Consistency here is what lets multiple developers work simultaneously without stepping on each other.

**Tech Stack:** Spring Boot 4.1.0 · Java 17 · Maven · MySQL · JWT · GitHub Flow

---

## 2. Branch Strategy

| Branch | Purpose |
|---|---|
| `main` | Deployed, stable. Never committed to directly. |
| `development` | Shared integration branch. All feature work merges here first. Never committed to directly. |
| `feature/*` | New functionality, e.g. `feature/case`, `feature/investigation`. |
| `bugfix/*` | Non-critical bug fixes, e.g. `bugfix/login-validation`. |
| `hotfix/*` | Urgent fixes applied directly against `main` when production is broken. |

All work branches off `development` (except `hotfix/*`, which branches off `main`) and merges back into it via Pull Request.

---

## 3. Development Workflow

```
git clone <repository-url>
git checkout development
git pull origin development

git checkout -b feature/module-name

# ...develop...

git add .
git commit -m "feat(module): description"
git push origin feature/module-name

# Open a Pull Request into development
# After approval and merge:
git branch -d feature/module-name
```

Every step above is expected for every change, no matter how small — including documentation-only or config-only updates.

---

## 4. Commit Message Convention

Format: `<type>(<scope>): <short description>`

| Type | Use For |
|---|---|
| `feat` | New functionality |
| `fix` | Bug fixes |
| `docs` | Documentation only |
| `refactor` | Code change with no behavior change |
| `test` | Adding or fixing tests |
| `style` | Formatting only, no logic change |
| `chore` | Tooling, dependencies, build config |

**Examples:**
```
feat(auth): implement login endpoint
fix(jwt): validate issuer
docs(api): update swagger
refactor(user): simplify mapper
```

Keep commits small and scoped to a single logical change — a commit should be easy to describe in one line because it does one thing.

---

## 5. Coding Standards

- **Java naming:** PascalCase for classes, camelCase for methods and fields, `SCREAMING_SNAKE_CASE` for constants.
- **Package conventions** (package-by-feature, per module):

  | Package | Contents |
  |---|---|
  | `controller` | REST endpoints only — no business logic |
  | `service` | Interface + implementation, business logic |
  | `repository` | Spring Data JPA repositories |
  | `dto` | Request/response/internal data shapes |
  | `mapper` | Entity ↔ DTO conversion |
  | `validator` | Custom validation logic beyond Bean Validation annotations |
  | `exception` | Module-specific exception types |

- **No business logic inside Controllers.** A Controller validates input shape, calls one Service method, and returns the result.
- **Use `ApiResponse` for every endpoint.** No endpoint returns a bare DTO or raw object — every response is wrapped consistently.
- **Use `GlobalExceptionHandler`.** No module implements its own local exception handling — all exceptions propagate to the shared handler.
- **Use constructor injection.** No field injection (`@Autowired` on a field) anywhere in the codebase.

---

## 6. Pull Request Checklist

Before opening a PR, confirm:

- [ ] Project builds (`mvn clean install`)
- [ ] Tests pass
- [ ] Swagger updated if any API changed
- [ ] No secrets committed
- [ ] Latest `development` merged into your branch (no stale base)
- [ ] Commit history is meaningful and readable

A PR missing any of the above is not ready for review.

---

## 7. Code Review Rules

- No direct push to `development` — every change goes through a PR.
- **One reviewer required** before merge.
- All review comments must be addressed (resolved or discussed) before merging — not merged over.

---

## 8. Git Rules

- **No force push**, on any shared branch.
- **One feature per branch** — don't combine unrelated work.
- **Delete merged branches** once they're no longer needed.
- **Never commit application secrets** — database credentials, JWT signing secret, API keys. These are environment variables, never hardcoded or checked in.

---

## 9. Architecture Rules

- **Do not modify another developer's module.** Each module has one owner; if you need something from a module you don't own, request it from its owner rather than editing it directly.
- **Communicate through services/interfaces.** Cross-module calls go through a Service interface — never through another module's Repository, Entity, or implementation class directly.
- **Follow the Backend Master Reference.** It is the source of truth for architecture, module boundaries, and design decisions. This file governs process; the Master Reference governs design.

---

## 10. Contact

| Role | Name |
|---|---|
| Backend Architect | Vishwa |
| Team | CTRL+WE |
