# Task 24 - Critical Business Flow Test Suite

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "spring boot test patterns junit mockito mockmvc testcontainers"
npx skills find "frontend testing react testing library playwright business flow"
```

- Uu tien skill:
  - `spring-boot-test-patterns`
  - `spring-boot-crud-patterns`
  - `frontend-design`

## Muc tieu

He thong dang co nhieu flow nghiep vu va phan quyen. Task nay tao bo test co tinh bao ve de khong bi regress khi task 20-23 thay doi data flow.

## Pham vi test

### 1. Backend tests

Can co:

- unit test service
- repository slice test
- controller/MockMvc test
- integration test cho flow quan trong
- container test neu can

### 2. Nhom flow can bao ve

- dashboard summary
- workflow state transition
- notification create/read
- group task create/assign/submit/review
- permission deny/allow
- detail/list query/filter/pagination
- file upload validation neu dung task 14

### 3. Frontend tests

Can co:

- render state
- loading/empty/error state
- role-based menu visibility
- action guard
- notification badge update
- dashboard refresh after mutation

### 4. Test matrix

It nhat phai cover:

- admin
- mentor
- student
- unauthorized user
- duplicate/conflict
- invalid state
- not found

## Acceptance criteria

- Moi flow chinh co test tuong ung.
- Test khong phu thuoc demo data.
- Test phat hien duoc regress do role/state/data refresh.

