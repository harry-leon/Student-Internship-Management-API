# Task 41 - Detail Routes, Sorting And Drill-down

## Muc tieu

Bo sung kha nang dieu huong va tra cuu chi tiet nhat quan cho company, assessment result va cac danh sach lon.

## Pham vi

- Them route detail co the bookmark/share cho student, mentor, group, company, task, submission va assessment result.
- Them sort server-side co whitelist field, direction mac dinh va validation.
- KPI/report drill-down toi filter/detail tuong ung.
- Bao toan scope theo role trong detail, sort, search va export.
- Giữ modal/drawer cho preview nhanh neu hop ly, khong duplicate logic fetch.

## Tieu chi nghiem thu

- URL detail tai lai truc tiep van hoat dong va khong lo du lieu ngoai scope.
- Sort khong cho phep arbitrary field de tranh SQL injection va query khong hop le.
- Drill-down tu dashboard mo dung bo loc va record.
- Co test route, not found, forbidden, sort va pagination.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Doc docs/rule/frontend-ui-configuration-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code

## Kiem thu

- Chay backend test/build lien quan den routing, sort, pagination
- Chay frontend lint/typecheck/test/build
- Verify role, permission, empty/loading/error state
- Test route, not found, forbidden, sort va pagination

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-41): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
