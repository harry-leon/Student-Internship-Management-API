# Task 42 - Test Pyramid And Frontend Quality Gates

## Muc tieu

Lap test coverage thuc te cho FE va bo sung integration test cho API quan trong, thay cho chi phu thuoc vao service unit test.

## Pham vi

- Them Vitest + React Testing Library hoac stack da duoc project/plugin ho tro.
- Test render, role menu, permission guard, state guard, form validation, error/empty/loading va notification.
- Them MockMvc integration tests cho auth, RBAC, pagination, error contract va workflow.
- Them Testcontainers PostgreSQL cho nhom test repository/integration can database that.
- Tao script test unit, integration, coverage; dat quality gate phu hop, khong dat coverage ao.

## Tieu chi nghiem thu

- CI chay duoc FE test va backend test khong can thao tac thu cong.
- Co test cho admin/mentor/student/unauthorized va cac loi 400/401/403/404/409/422.
- Test khong phu thuoc thu tu chay va khong dung secret production.
- Bao cao gap coverage va cac phan co risk cao.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (Vitest, Testing Library, Testcontainers)

## Kiem thu

- Chay backend test/build (unit, integration, MockMvc)
- Chay frontend lint/typecheck/test/build
- Verify test khong phu thuoc thu tu va khong dung secret production
- Test coverage gap va risk cao

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-42): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
