# Task 48 - Observability, Structured Logging And Error Tracking

## Muc tieu

Khac phuc task 30 de co the truy vet loi va do suc khoe he thong trong production.

## Pham vi

- Them `X-Request-ID`/correlation id qua filter va propagate qua log, audit, notification va response.
- Cau hinh structured JSON logging voi level, timestamp, service, environment, request id, actor id, route, status, latency.
- Them Spring Actuator/Micrometer health, metrics va readiness/liveness; khong expose endpoint nhay cam.
- Chon error tracking tuong thich qua plugin/ha tang; FE ErrorBoundary va unhandled rejection gui event da redact.
- Dat log retention, PII redaction, sampling va alert threshold.

## Tieu chi nghiem thu

- Mot request co the tim tu FE den backend qua correlation id.
- 5xx co stack trace o server log nhung response khong lo chi tiet noi bo.
- Health endpoint phan biet liveness/readiness va DB/storage dependency.
- Co dashboard/alert huong dan va test redact secret/token/PII.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (logging library, metrics library)

## Kiem thu

- Chay backend test/build lien quan den logging, metrics
- Chay frontend lint/typecheck/test/build (FE error tracking)
- Verify correlation id, structured logging, PII redaction
- Test health endpoint, error tracking

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-48): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
