# Task 39 - Workflow UI State And Action Guards

## Muc tieu

Dong bo giao dien voi state machine cua backend. UI chi hien thi va cho phep thao tac khi role co quyen va trang thai entity cho phep.

## Pham vi

- Kiem tra cac action cua group task, weekly report, application, submission va assignment.
- Bo sung state-aware guard cho button, menu, bulk action, modal va deep link.
- Disable action dang xu ly, hien thi ly do khi action bi chan.
- Xu ly 409/422 tu backend bang thong bao nghiep vu ro rang va refresh du lieu lien quan.
- Khong tin vao FE guard; backend van la lop bao mat cuoi cung.

## Tieu chi nghiem thu

- Khong con button thao tac sai state tren cac trang lien quan.
- User khong the bypass guard bang URL, devtools hoac request truc tiep.
- Co test cho transition hop le, transition bi chan, 409 va 422.
- Khong dung mock/fallback data de lam action co ve thanh cong.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Doc docs/rule/frontend-ui-configuration-rule.md
- Chay skill discovery phu hop voi task (kiem tra skill co san)
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code

## Kiem thu

- Chay backend test/build lien quan den state transition
- Chay frontend lint/typecheck/test/build
- Verify role, permission, empty/loading/error state
- Test transition hop le, transition bi chan, 409 va 422
- Khong dung mock/fallback data de lam action co ve thanh cong

## Git

- Chi add file thuoc task hien tai (BE/src/..., FE/src/...)
- Commit rieng cho task: `fix(task-39): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope (config IDE, JDBC, docs khac)
