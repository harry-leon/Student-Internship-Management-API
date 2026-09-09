# Task 26 - Security And RLS Data Scope

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/frontend-ui-configuration-rule.md` neu co UI security state
- Truoc khi code:

```powershell
npx skills find "spring boot security rbac permissions row level security data scope audit"
npx skills find "database design row level security multi tenant access scope"
```

- Uu tien skill:
  - `springboot-security`
  - `database-schema-designer`

## Muc tieu

Xay dung lop bao ve dau tien cho he thong scale lon:

1. Deny by default.
2. Moi request phai co scope ro rang theo user, role, org, group, owner, or admin override.
3. Khong cho menu/UI la lop bao mat chinh.
4. Backend va database phai chan duoc truy cap sai scope.

## Pham vi

### 1. Data scope rules

Ap dung cho:

- users
- students
- mentors
- companies
- groups
- tasks
- submissions
- notifications
- reports

Quy tac:

- Admin xem duoc toan he thong.
- Mentor chi xem du lieu trong pham vi group/company/phase ma minh phu trach.
- Student chi xem du lieu cua minh va group minh thuoc.
- Moi query can scope filter ro rang.

### 2. RLS style enforcement

Neu DB support RLS thi co the dung native RLS.
Neu khong, enforce o app layer bang:

- repository filter
- specification/query guard
- service ownership check
- `@PreAuthorize`

### 3. Security headers and request hygiene

Can chuan hoa:

- CORS
- CSRF posture
- secure headers
- content security policy co ban
- no sensitive logging

### 4. Sensitive action guard

Can bao ve:

- read detail
- export/download
- delete
- approve/reject
- promote/demote
- kick/remove member
- view audit data

## Backend deliverables

- trung tam authz helper / policy service
- scope filter theo role va owner
- guard cho API detail/list/export
- audit cho action nhay cam

## Test

- user ngoai scope bi 403
- admin override van log duoc
- scope filter khong bi bypass qua URL
- detail/list khong leak du lieu

## Acceptance criteria

- Tat ca du lieu co scope ro rang.
- FE khong con la lop bao mat chinh.
- RLS style enforcement duoc ghi ro va test duoc.

