# Task 36 - Hosting And Deployment Strategy

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "hosting deployment reverse proxy environment configuration blue green rolling"
npx skills find "spring boot deployment frontend deployment static assets"
```

- Uu tien skill:
  - `springboot-security`
  - `frontend-design` neu co giao dien deploy docs

## Muc tieu

Xac dinh lop hosting va deployment ro rang:

- dev
- staging
- production
- zero downtime if possible

## Pham vi

### 1. Deployment topology

- api
- frontend
- database
- cache
- object storage
- worker/service khac

### 2. Runtime configuration

- env separation
- secrets externalized
- runtime config no hardcode

### 3. Deployment strategy

- rolling or blue/green if supported
- health check before switch
- smoke test after deploy

### 4. Static asset hosting

- frontend asset caching
- CDN friendly headers
- versioned build assets

## Test

- deploy runbook readable
- rollback strategy exists
- env config does not leak secrets

## Acceptance criteria

- Hosting and deployment strategy clear.
- Zero-downtime path documented where possible.

