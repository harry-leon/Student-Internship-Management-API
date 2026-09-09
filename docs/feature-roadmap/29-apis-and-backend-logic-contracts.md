# Task 29 - APIs And Backend Logic Contracts

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "spring boot api design dto projection pagination validation error response"
npx skills find "rest api backend logic service boundary query optimization"
```

- Uu tien skill:
  - `spring-boot-crud-patterns`
  - `springboot-security`
  - `spring-boot-test-patterns`

## Muc tieu

Chuan hoa lop API va backend business logic de:

1. Khong expose entity truc tiep.
2. DTO va projection dung scope.
3. Pagination/filter/sort/aggregation ro rang.
4. API idempotent va on dinh khi scale.

## Pham vi

### 1. API contract

- versioned endpoints neu can
- consistent request/response
- error schema thong nhat
- 400/401/403/404/409/422 dung nghia

### 2. Backend logic

- service boundary ro rang
- transaction rules ro rang
- idempotency where needed
- async job hay event khi nghiep vu can scale

### 3. Read model optimization

- summary DTO cho dashboard
- list DTO cho tables
- detail DTO cho screen detail
- projection/query model cho report

### 4. Bulk and pagination

- cursor pagination neu du lieu lon
- server-side filter/sort/search
- bulk endpoints neu can

## Test

- DTO contract test
- pagination/filter/sort test
- conflict and invalid state test
- no entity leak test

## Acceptance criteria

- API contract chuan va on dinh.
- Backend logic co boundary ro rang.
- Read model phuc vu scale thay vi load entity day du.

