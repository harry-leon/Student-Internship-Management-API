# Task 34 - Availability And Recovery

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "disaster recovery backup restore circuit breaker graceful degradation spring boot"
npx skills find "availability recovery rollback incident response system design"
```

- Uu tien skill:
  - `database-schema-designer`
  - `spring-boot-test-patterns`

## Muc tieu

Availability va recovery phai di truoc hosting neu he thong scale lon:

1. backup
2. restore
3. failover
4. graceful degradation
5. incident readiness

## Pham vi

### 1. Backup and restore

- DB backup plan
- file storage backup plan
- restore drill
- retention policy

### 2. Resilience

- circuit breaker where needed
- retries with backoff
- dead letter / compensation for async jobs
- maintenance mode

### 3. Disaster recovery

- RPO/RTO target
- incident runbook
- recovery validation

### 4. Rollback strategy

- rollback for migration
- rollback for release
- feature flag kill switch

## Test

- restore test documented
- failure path degrades gracefully
- rollback plan exists for dangerous change

## Acceptance criteria

- He thong co phuong an back up va recover ro rang.
- Availability khong phu thuoc vao may chay lien tuc may man.

