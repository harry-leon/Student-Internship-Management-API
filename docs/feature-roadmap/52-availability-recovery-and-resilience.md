# Task 52 - Availability, Recovery And Resilience

## Muc tieu

Co kha nang phuc hoi co kiem soat khi DB, storage, cache, queue hoac service ngoai bi loi.

## Pham vi

- Viet backup/restore runbook cho PostgreSQL va object storage, kem RPO/RTO.
- Chon Resilience4j cho timeout, retry exponential backoff, circuit breaker, bulkhead va fallback phu hop.
- Khong retry mutation khong idempotent neu thieu idempotency key.
- Them maintenance mode/feature flag cho thao tac bao tri va thong diep user-friendly.
- Test backup restore, dependency outage, retry storm, circuit open/close va migration rollback.

## Tieu chi nghiem thu

- Restore duoc moi truong test tu backup va do duoc RTO/RPO.
- Dependency loi khong lam treo thread pool hoac tao retry storm.
- 503/429/5xx tra dung ErrorResponse va UI thong bao phu hop.
- Runbook co owner, trigger, rollback va verification sau recovery.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (Resilience4j, backup/restore tool)

## Kiem thu

- Chay backend test/build lien quan den resilience, recovery
- Verify backup restore, circuit breaker, retry, fallback
- Test dependency outage, retry storm, circuit open/close
- Test migration rollback

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-52): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
