# Task 43 - Admin Analytics, Reporting And Export

## Muc tieu

Hoan thien dashboard van hanh cho Admin bang du lieu that, bo loc thoi gian, xu huong, drill-down va export.

## Pham vi

- Them date range va timezone ro rang cho KPI/report.
- Them trend/time-series cho student, mentor, group, task, submission, report va grading.
- Implement export CSV/XLSX tai backend theo query/filter hien tai, stream khi dataset lon.
- Them audit cho export nhay cam va gioi han quyen export.
- Them empty/loading/error state va khong hardcode fallback.

## Tieu chi nghiem thu

- So lieu report khop database va scope Admin.
- Export dung filter, encoding, column va khong expose thong tin khong can thiet.
- Drill-down tu moi KPI den danh sach/detail tuong ung.
- Co test date boundary, timezone, empty result va export authorization.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Doc docs/rule/frontend-ui-configuration-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (Excel/CSV export library)

## Kiem thu

- Chay backend test/build lien quan den analytics, export
- Chay frontend lint/typecheck/test/build
- Verify role, permission, empty/loading/error state
- Test date boundary, timezone, empty result va export authorization

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-43): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
