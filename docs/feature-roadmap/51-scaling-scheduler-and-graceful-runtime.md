# Task 51 - Horizontal Scaling, Scheduler Coordination And Graceful Runtime

## Muc tieu

Chuan bi backend cho nhieu instance ma khong duplicate scheduler, mat request dang xu ly hoac phu thuoc state local.

## Pham vi

- Them Actuator readiness/liveness va graceful shutdown.
- Chon distributed lock cho scheduler (ShedLock hoac giai phap tuong duong) va idempotent job.
- Kiem tra stateless JWT, upload workflow va websocket/SSE session khi scale.
- Cau hinh connection pool, timeout, thread pool, max request size va backpressure.
- Viet load test baseline cho login, list, submission, notification va chat.

## Tieu chi nghiem thu

- Hai instance khong chay cung mot scheduled job trung lap trong cung window.
- Instance draining khong nhan request moi va hoan tat request dang xu ly trong timeout.
- Health/readiness dung de load balancer routing.
- Co baseline latency/error/throughput va nguong canh bao.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (ShedLock, distributed lock library)

## Kiem thu

- Chay backend test/build lien quan den scaling, scheduler
- Verify graceful shutdown, distributed lock, scheduler coordination
- Test load baseline (login, list, submission, notification)
- Test health/readiness endpoint

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-51): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
