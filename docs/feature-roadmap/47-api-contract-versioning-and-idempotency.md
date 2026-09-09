# Task 47 - API Contract Versioning And Idempotency

## Muc tieu

On dinh hop dong API cho client va thao tac retry an toan khi scale, mobile/network yeu hoac request bi lap.

## Pham vi

- Chon chien luoc version `/api/v1` hoac header version va ap dung cho API public can bao tri.
- Chuan hoa pagination, sorting, validation, error code, correlation id va deprecation metadata.
- Them idempotency key cho create submission, upload finalize, grading, approval va notification-triggering mutation.
- Luu request fingerprint/result theo TTL, scope actor va endpoint; tu choi reuse key voi payload khac.
- Them cursor pagination cho dataset lon neu offset khong con phu hop.

## Tieu chi nghiem thu

- Client cu va client moi co contract ro rang trong giai doan chuyen tiep.
- Retry cung idempotency key khong tao record/notification trung lap.
- Key khac payload bi tu choi; key het han xu ly theo policy.
- OpenAPI va integration tests phan anh contract that.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (API versioning, idempotency library)

## Kiem thu

- Chay backend test/build lien quan den API contract
- Verify API versioning, idempotency, pagination
- Test idempotency key reuse, expiration, payload mismatch
- Test OpenAPI contract

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-47): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
