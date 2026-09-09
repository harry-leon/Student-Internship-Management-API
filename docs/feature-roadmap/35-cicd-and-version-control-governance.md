# Task 35 - CI CD And Version Control Governance

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "ci cd git branching version control release pipeline spring boot frontend"
npx skills find "quality gates testing build deploy rollback"
```

- Uu tien skill:
  - `spring-boot-test-patterns`

## Muc tieu

Khi scale lon, CI/CD va version control phai giam rui ro release thay vi chi la buoc deploy.

## Pham vi

### 1. Version control

- branch strategy
- commit message convention
- release tag/version
- migration review gate

### 2. CI gates

- lint
- unit test
- integration test
- build
- security scan if available
- fail fast on broken migrations

### 3. CD gates

- environment promotion
- config per env
- rollback toggle
- deployment smoke test

### 4. Change management

- PR template
- release notes
- hotfix path

## Test

- pipeline gates documented
- build/test fail stops release
- migration changes reviewable

## Acceptance criteria

- Moi release co guard.
- Version control phuc vu operability thay vi chi luu code.

