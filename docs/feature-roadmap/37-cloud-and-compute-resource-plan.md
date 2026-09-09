# Task 37 - Cloud And Compute Resource Plan

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "cloud compute managed database object storage queue autoscaling"
npx skills find "cloud architecture cost control infra planning backend frontend"
```

- Uu tien skill:
  - `database-schema-designer`
  - `springboot-security`

## Muc tieu

Chon va mo ta lop cloud/compute phu hop voi he thong scale lon:

1. app compute
2. managed database
3. object storage
4. cache/queue
5. network/IAM/secrets

## Pham vi

### 1. Resource map

Can co ban do cho:

- API compute
- frontend hosting
- DB
- object storage
- cache
- message queue if needed
- logging/monitoring

### 2. Environment plan

- dev
- staging
- production
- cost guardrails

### 3. Security and access

- IAM least privilege
- secret storage
- network access boundaries

### 4. Capacity plan

- baseline
- expected growth
- scaling triggers

## Test

- resource map documented
- env boundaries clear
- secrets not embedded in code

## Acceptance criteria

- Cloud and compute layer have a concrete plan.
- Provider selection can be evaluated against requirements, not guesswork.

