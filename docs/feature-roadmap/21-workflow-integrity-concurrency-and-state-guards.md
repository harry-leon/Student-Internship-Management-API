# Task 21 - Workflow Integrity, Concurrency And State Guards

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "workflow state machine spring boot concurrency optimistic locking testing"
npx skills find "react state guard permission based actions workflow"
```

- Uu tien skill:
  - `spring-boot-crud-patterns`
  - `spring-boot-test-patterns`
  - `database-schema-designer`

## Muc tieu

He thong dang co nhieu flow CRUD va approve/review/update trang thai, nhung chua co guard ro rang cho:

- duplicate action
- invalid state transition
- concurrent update
- stale UI action
- cap nhat trang thai sai scope

Task nay chuan hoa tat ca state machine nghiep vu co ban.

## Pham vi

### 1. State machine rules

Ap dung cho:

- internship application
- group task
- task assignee
- submission
- review/score
- notification status
- read state

Phai co ban do transition ro rang:

- draft -> submitted -> approved / rejected
- pending -> in_progress -> completed / cancelled
- submitted -> reviewed -> published -> archived
- unread -> read

Khong cho nhay state bat hop le.

### 2. DB integrity

Can co:

- unique constraint cho du lieu phai dupe
- foreign key day du
- check constraint cho status/type neu co the
- optimistic locking `@Version` cho record de nhieu user khong ghi de nhau

### 3. API error semantics

Can phan biet ro:

- `400` cho request sai format
- `401` cho chua dang nhap
- `403` cho khong co quyen
- `404` cho khong tim thay
- `409` cho conflict/duplicate/concurrent update
- `422` cho invalid business state

### 4. Frontend guard

UI phai:

- disable action neu state khong hop le
- hide action neu role khong co quyen
- show reason ro rang cho user
- khong de user bam spam action khong hop le

### 5. Scope bat buoc

Task 16 da de cap pham vi business, nen task nay phai giai quyet:

- student khong tu sinh flow ngoai scope
- mentor chi thao tac trong group cua minh
- admin co quyen cao nhat nhung van phai qua state guard

## Test

- duplicate submit/approve/update bi chan dung code
- concurrent update tra `409`
- invalid state tra `422`
- UI khong hien action sai state
- backend test bao phu transition, concurrency, duplicate

## Acceptance criteria

- State transition cua flow quan trong duoc chuan hoa.
- Conflict va invalid business state khong con bi map nham.
- FE va BE cung ton tai guard dung quyen va dung trang thai.

