# Task 40 - Notification Center, Realtime Delivery And Entity Deep Links

## Muc tieu

Hoan thien he thong thong bao tu dropdown thanh notification center day du, cap nhat realtime va mo dung entity thay vi chi mo trang danh sach.

## Pham vi

- Chon co che realtime phu hop voi kien truc hien tai, uu tien SSE/WebSocket neu backend va ha tang dap ung; co polling fallback co backoff.
- Them trang `/notifications` voi filter, unread, type, pagination, mark read, mark all, delete va retry.
- Deep link bang `targetType` + `targetId` toi detail route dung quyen.
- Bao dam dedupe, ordering, reconnect, tab visibility va khong tao notification trung lap.
- Bo sung event cho submit, approve/reject, review, grading, deadline va group activity.

## Tieu chi nghiem thu

- Notification moi hien thi ma khong can reload trong dieu kien realtime hoat dong.
- Mat ket noi tu dong reconnect va fallback an toan, khong spam request.
- Nhan notification mo dung ban ghi cu the va tu choi truy cap neu khong co quyen.
- Co test backend reliability va frontend notification behavior.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Doc docs/rule/frontend-ui-configuration-rule.md
- Chay skill discovery phu hop voi task (kiem tra skill co san)
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (uu tien reuse thu vien realtime/notification da co)

## Kiem thu

- Chay backend test/build lien quan den notification, websocket/SSE
- Chay frontend lint/typecheck/test/build
- Kiem tra multi-tab, reconnect, unread count va dedupe
- Verify role, permission, empty/loading/error state
- Test realtime fallback, reconnect va notification behavior

## Git

- Chi add file thuoc task hien tai (BE/src/..., FE/src/...)
- Commit rieng cho task: `feat(task-40): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
