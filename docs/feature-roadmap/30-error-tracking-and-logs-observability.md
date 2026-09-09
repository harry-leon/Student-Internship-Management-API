# Task 30 - Error Tracking And Logs Observability

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "spring boot logging metrics tracing error tracking observability"
npx skills find "structured logging correlation id monitoring alerts dashboards"
```

- Uu tien skill:
  - `springboot-security`
  - `spring-boot-test-patterns`

## Muc tieu

Scale lon ma khong co observability thi khong biet nghen o dau. Task nay can co:

1. structured logs
2. correlation/request id
3. error tracking
4. metrics
5. alerting hooks

## Pham vi

### 1. Logging

- JSON or structured logging
- request id / trace id
- role/user context an toan
- khong log secrets, tokens, PII

### 2. Error tracking

- backend exception mapping dung rule
- frontend error boundary / error toast hoac screen
- log business error khac system error

### 3. Metrics

- latency
- error rate
- throughput
- slow query signal
- queue/job status neu co async

### 4. Alert readiness

- high error burst
- auth failure spike
- upload failure spike
- notification delivery failure

## Test

- log format khong leak secret
- error response dung schema
- correlation id co mat tren request chain

## Acceptance criteria

- Co the debug production flow nhanh hon.
- Error tracking va logs la lop dau tien de tim bottleneck.

