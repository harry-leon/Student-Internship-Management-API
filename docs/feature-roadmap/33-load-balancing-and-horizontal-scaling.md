# Task 33 - Load Balancing And Horizontal Scaling

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "horizontal scaling stateless spring boot load balancer health checks"
npx skills find "distributed session state queue workers autoscaling backend"
```

- Uu tien skill:
  - `springboot-security`
  - `spring-boot-crud-patterns`

## Muc tieu

He thong scale lon phai chay duoc nhieu instance ma khong bi phu thuoc session local hoac memory state.

## Pham vi

### 1. Stateless app design

- no in-memory business state
- session externalized neu can
- shared cache/DB instead of local node state

### 2. Load balancing readiness

- health checks
- readiness/liveness
- graceful shutdown
- sticky session chi khi bat buoc

### 3. Horizontal scale support

- background job separate from request path
- queue/worker cho tac vu nang
- read replicas neu co
- async processing cho report/export

### 4. Bottleneck reduction

- remove singleton mutable state
- avoid N+1
- avoid heavy synchronous work on request thread

## Test

- app chay nhieu instance conceptually khong mat state
- health check pass
- request state khong bi local node lock-in

## Acceptance criteria

- He thong san sang cho horizontal scaling.
- Request path khong ton tai state local nguy hiem.

