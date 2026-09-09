# Task 53 - CI/CD, Deployment, Cloud Plan And Frontend Last-mile Quality

## Muc tieu

Hoan tat lop delivery va frontend production sau khi security, database, backend va observability da on dinh.

## Pham vi

- Tao GitHub Actions cho lint, typecheck, FE tests, BE tests, build, migration validation va security scan.
- Tao PR template, branch/commit policy, artifact retention va required checks.
- Tao Dockerfile multi-stage cho BE/FE, docker-compose local va production config khong chua secret.
- Viet deploy runbook cho dev/staging/prod, environment matrix, rollback va migration order.
- Tai lieu cloud resource map: compute, DB, object storage, cache, queue, IAM least privilege va capacity baseline.
- FE: React.lazy route code splitting, ESLint, FE tests, virtualization danh sach lon, accessibility va bundle budget.

## Tieu chi nghiem thu

- Pull request khong pass gate thi khong merge/deploy.
- Image build reproducible, non-root neu phu hop, khong chua `.env`, key hay debug log.
- Staging deploy co smoke test va rollback duoc theo runbook.
- FE tai route theo nhu cau, list lon khong lam treo UI, lint/test/build deu pass.

## Luu y

Task nay chi bat dau sau khi tasks 44-52 da co ket qua toi thieu tren staging. Uu tien skill/plugin/thu vien da co truoc khi tu code pipeline hoac UI primitive moi.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Doc docs/rule/frontend-ui-configuration-rule.md
- Chay skill discovery phu hop voi task (CI/CD, Dockerfile, ESLint, testing)
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code

## Kiem thu

- Chay backend test/build (lint, typecheck, tests, security scan)
- Chay frontend lint/typecheck/test/build
- Verify CI/CD pipeline, Docker build, deployment runbook
- Test FE code splitting, virtualization, accessibility, bundle budget
- Verify staging deploy, smoke test, rollback

## Git

- Chi add file thuoc task hien tai (CI/CD config, Dockerfile, runbook)
- Commit rieng cho task: `feat(task-53): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit secret/key/debug log trong Docker image
