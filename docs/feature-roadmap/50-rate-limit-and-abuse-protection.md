# Task 50 - Rate Limiting And Abuse Protection

## Muc tieu

Bao ve cac endpoint de bi abuse: login, upload, search, export, notification va chat.

## Pham vi

- Chon Bucket4j, gateway limiter hoac thu vien da duoc workspace/plugin ho tro.
- Cau hinh limit theo endpoint, actor/IP, role va burst; Redis/distributed store khi multi-instance.
- Tra 429 kem `Retry-After` va ErrorResponse dung contract.
- Them upload quota, request body limit, search cost control, export cooldown va anti-spam chat.
- Ghi metric/log cho blocked request, khong log credential/file content.

## Tieu chi nghiem thu

- Login bi gioi han va khong lam lo tai khoan ton tai.
- Limit hoat dong nhat quan tren nhieu instance.
- Request hop le tu dong hoat dong lai sau retry window.
- Co load/negative test cho 429 va khong anh huong endpoint khong bi gioi han.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (Bucket4j, rate limiting library)

## Kiem thu

- Chay backend test/build lien quan den rate limiting
- Verify rate limit per endpoint, actor/IP, role
- Test 429 response, Retry-After, distributed limit
- Test load/negative cases

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-50): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
