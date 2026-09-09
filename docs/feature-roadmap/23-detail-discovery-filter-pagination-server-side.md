# Task 23 - Detail, Discovery, Filter And Server Side Pagination

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/frontend-ui-configuration-rule.md`
- Truoc khi code:

```powershell
npx skills find "react detail view server side pagination filter search table layout"
npx skills find "spring boot pageable specification detail dto query optimization"
```

- Uu tien skill:
  - `spring-boot-crud-patterns`
  - `database-schema-designer`
  - `frontend-design`

## Muc tieu

He thong dang co nhieu list page, nhung chua du detail view va tim kiem loc phu hop cho man hinh quan tri.

Task nay bo sung:

- detail page / drawer / modal cho entity quan trong
- search va filter dung du lieu that
- server-side pagination cho list lon
- sort va column density hop ly
- reuse response DTO thay vi query entity thua

## Pham vi

### 1. Entity can co detail view

Toi thieu:

- student
- mentor
- company
- group
- group task
- submission
- assessment result
- application neu van ton tai trong scope noi bo

### 2. List API

Can co:

- pagination
- search theo nhieu truong
- filter theo status/role/phase/group/company
- sort theo field chinh

API nen tra ve tong hop:

- items
- total
- page info
- available filters neu can

### 3. Detail data

Detail view phai co:

- thong tin co ban
- trang thai
- audit/history neu co
- relation chinh
- action hop le theo role

Khong can load qua nhieu field khong can thiet o list view.

### 4. UI density

Yeu cau giao dien:

- desktop co nhieu cot hon
- table/summary card gon
- detail drawer khong lam vo layout
- filter bar dong bo theo config
- khong phai render card qua to

## Backend

Neu chua co, can them:

```http
GET /api/students
GET /api/students/{id}
GET /api/mentors
GET /api/mentors/{id}
GET /api/groups/{id}
GET /api/groups/{id}/tasks
GET /api/submissions
GET /api/submissions/{id}
GET /api/assessment-results
GET /api/assessment-results/{id}
```

Query phai dung projection/DTO va index hop ly.

## Test

- pagination dung page/size/total
- search/filter/sort dung ket qua
- detail view khong leak data ngoai scope
- list khong N+1

## Acceptance criteria

- Cac trang list co filter/search/pagination that.
- Detail view du thong tin can thiet.
- Khong con layout list co mat do thong tin thap va phai lenh benh.

