# Task 20 - Role Scopedd Production Dashboard And Data Refresh

## Skill nen dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/frontend-ui-configuration-rule.md`
  - `docs/rule/README.md` neu co ghi chu them ve dashboard/summary config
- Truoc khi code, chay skill discovery:

```powershell
npx skills find "role based dashboard api summary projection react query refresh testing"
npx skills find "spring boot dashboard summary dto aggregation query optimization"
```

- Neu task can summary data cho dashboard, uu tien reuse skill `spring-boot-crud-patterns` va `database-schema-designer`.

## Muc tieu

Task nay tap trung vao phan dashboard dang con hardcode, count sai scope, va chua refresh dung khi data thay doi.

Dashboard phai:

1. Doc du lieu that theo role dang dang nhap.
2. Khong dung fallback demo data khi API loi hoac empty.
3. Khong lay count global neu user chi co scope mentor/group cua minh.
4. Co co che refresh sau mutation va khi quay lai man hinh.
5. Hien thi compaction hop ly cho desktop, khong qua nhieu card to.

## Pham vi

### 1. Dashboard data theo role

API dashboard nen co thanh phan rieng theo scope:

- Admin summary: users, students, mentors, companies, groups, tasks, submissions, pending approvals.
- Mentor summary: groups, active members, assigned tasks, pending reviews, unread notifications.
- Student summary: assigned tasks, submission status, deadline, feedback, unread notifications.

Khong dung mot endpoint global cho tat ca role neu response phai query qua nhieu field khong can thiet.

### 2. DTO va query optimization

Can co:

- summary DTO rieng cho dashboard
- projection/aggregate query thay vi load entity day du
- count query toi uu va co index
- khong N+1 khi lay mentor/group/task summary

Neu backend co san response DTO tuong dong, reuse truoc khi tao moi.

### 3. Frontend refresh

UI can co:

- loading state
- empty state
- error state dung rule HTTP
- refetch sau create/update/delete cua entity lien quan
- auto refresh nhe neu dashboard phu thuoc KPI thay doi lien tuc

Khong render so lieu cu khi mutation thanh cong.

### 4. Layout

Chi nay la trang overview, nen:

- card KPI gon hon
- table/widget co mat do thong tin cao hon
- desktop co the hien 4-6 cot tuy breakpoint
- mobile rut gon thanh 1-2 cot

## Ghi chu backend

Neu chua co endpoint dashboard theo role, can bo sung:

```http
GET /api/dashboard/me
GET /api/dashboard/admin
GET /api/dashboard/mentor
GET /api/dashboard/student
```

Co the dung 1 endpoint chung `GET /api/dashboard/me` va backend tu resolve scope, nhung phai co filter theo role ro rang.

## Test

- Dashboard admin khong lay so global sai scope.
- Dashboard mentor chi hien group/assignee/review cua minh.
- Dashboard student chi hien task/submission cua minh.
- Empty response khong render demo data.
- Sau mutation, dashboard refetch dung.
- `npm run lint` pass.
- `npm run build` pass.

## Acceptance criteria

- Dashboard khong con hardcode KPI chinh.
- So lieu doc dung scope role.
- Khong co fallback demo data che API loi.
- Co refresh sau thao tac va khi quay lai man hinh.

