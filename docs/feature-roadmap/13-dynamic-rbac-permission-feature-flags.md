# Dynamic RBAC Permission And Feature Flags

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Bat buoc doc cac rule trong `docs/rule` truoc khi code:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code, chay `npx skills find "spring security rbac permissions feature flags react admin access control"`.
- Neu task co thay doi Spring Security, doc/use `springboot-security` neu co san.
- Neu task co CRUD admin role/permission, doc/use `spring-boot-crud-patterns` va `spring-boot-test-patterns` neu co san.
- Neu task co thiet ke UI matrix permission, doc/use `frontend-design` neu co san.

## Muc Tieu

Nang cap he thong tu phan quyen hard-code theo role `ADMIN / MENTOR / STUDENT` sang mo hinh linh hoat:

1. Role: nhom nguoi dung.
2. Permission: quyen thuc hien chuc nang/API/action.
3. Data Scope: pham vi du lieu duoc phep thao tac.
4. Feature Flag: tinh nang dang duoc bat/tat cho he thong hoac tung role.

Admin phai co UI de dieu chinh permission/feature cho tung role ma khong can sua source code, build va deploy lai.

Task nay dong thoi phai sua gap hien tai: cac role dang xem duoc thong tin va tinh nang khong thuoc quyen cua minh. Sau task nay, role khong du quyen phai bi chan o backend, bi chan khi truy cap route/API truc tiep, va cac menu/action tren frontend phai duoc an di.

## Van De Hien Tai Can Sua Ngay

He thong hien tai co dau hieu dang chi an/hien theo role o mot so noi va chua enforce nhat quan. Can audit va sua cac diem sau:

- Role MENTOR/STUDENT khong duoc xem menu/tinh nang chi danh cho ADMIN.
- STUDENT khong duoc xem danh sach tat ca students, mentors, users, assignments, submissions, grading cua nguoi khac.
- MENTOR khong duoc xem hoac thao tac du lieu ngoai scope: student khong duoc assign/group cua mentor do, submission/score/report cua student khac.
- Role khong co permission khong duoc truy cap route bang cach go truc tiep URL tren frontend.
- Role khong co permission khong duoc goi API truc tiep bang token, backend phai tra 403.
- Sidebar, header action, quick action, button create/edit/delete/download/grade phai an neu user khong co permission/feature tuong ung.
- Neu menu bi an nhung user vao URL cu, frontend phai hien trang 403/Access Denied thay vi render data.

Day la acceptance bat buoc cua task 13, khong duoc chi tao bang permission ma bo qua viec chan truy cap hien tai.

## Phan Tich Huong Phu Hop

Khong nen xoa role hien co. Role van la lop nhan dien nguoi dung co nghiep vu khac nhau. Tuy nhien, role khong nen la dieu kien duy nhat trong moi `@PreAuthorize`.

Huong dung:

- Role dung de nhom user: ADMIN, MENTOR, STUDENT.
- Permission dung de quyet dinh co duoc goi chuc nang hay khong: `STUDENT_VIEW`, `ASSESSMENT_SCORE`, `SUBMISSION_DOWNLOAD`.
- Data Scope dung de gioi han du lieu: mentor chi xem student duoc assign/group cua minh; student chi xem/sua profile cua minh.
- Feature Flag dung de bat/tat tinh nang van hanh: cho phep student xem diem, cho phep mentor cham diem, mo cong nop bai.

Vi du:

```text
Mentor cham diem:
1. Co permission ASSESSMENT_SCORE?
2. Feature ASSESSMENT_SCORING dang enabled cho MENTOR?
3. Student co thuoc mentor/group cua mentor nay?
4. Assessment round dang mo?
=> Neu tat ca dung moi cho phep.
```

## Database De Xuat

### roles

| Field | Type | Note |
| --- | --- | --- |
| role_id | identity/int | Primary key |
| role_code | varchar(50) | Unique: ADMIN, MENTOR, STUDENT |
| role_name | varchar(100) | Ten hien thi |
| description | text | Mo ta |
| is_system | boolean | Role he thong khong cho xoa |
| is_active | boolean | Active/inactive |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### permissions

| Field | Type | Note |
| --- | --- | --- |
| permission_id | identity/int | Primary key |
| permission_code | varchar(100) | Unique, vi du STUDENT_VIEW |
| module_code | varchar(50) | USER, STUDENT, MENTOR, ASSESSMENT |
| action_code | varchar(50) | VIEW, CREATE, UPDATE, DELETE, SCORE |
| description | text | Mo ta |
| is_active | boolean | Active/inactive |

### role_permissions

| Field | Type | Note |
| --- | --- | --- |
| role_id | int | FK roles |
| permission_id | int | FK permissions |
| granted | boolean | Cho phep hay khong |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

Unique: `role_id + permission_id`.

### system_features

| Field | Type | Note |
| --- | --- | --- |
| feature_id | identity/int | Primary key |
| feature_code | varchar(100) | Unique, vi du STUDENT_VIEW_RESULT |
| module_code | varchar(50) | Module so huu |
| feature_name | varchar(150) | Ten hien thi |
| description | text | Mo ta |
| enabled | boolean | Default toan he thong |
| is_runtime_configurable | boolean | Admin co duoc doi trong UI khong |

### role_features

| Field | Type | Note |
| --- | --- | --- |
| role_id | int | FK roles |
| feature_id | int | FK system_features |
| enabled | boolean | Override theo role |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

Unique: `role_id + feature_id`.

## Permission Code De Xuat Ban Dau

### User

- USER_VIEW
- USER_CREATE
- USER_UPDATE
- USER_DELETE
- USER_CHANGE_STATUS
- USER_CHANGE_ROLE

### Student

- STUDENT_VIEW
- STUDENT_CREATE
- STUDENT_UPDATE
- STUDENT_DELETE
- STUDENT_VIEW_DETAIL

### Mentor

- MENTOR_VIEW
- MENTOR_CREATE
- MENTOR_UPDATE
- MENTOR_DELETE
- MENTOR_VIEW_DETAIL

### Company

- COMPANY_VIEW
- COMPANY_CREATE
- COMPANY_UPDATE
- COMPANY_DELETE
- COMPANY_CHANGE_STATUS

### Phase

- PHASE_VIEW
- PHASE_CREATE
- PHASE_UPDATE
- PHASE_DELETE
- PHASE_CHANGE_STATUS

### Assignment

- ASSIGNMENT_VIEW
- ASSIGNMENT_CREATE
- ASSIGNMENT_UPDATE
- ASSIGNMENT_DELETE
- ASSIGNMENT_CHANGE_STATUS

### Submission

- SUBMISSION_VIEW
- SUBMISSION_CREATE
- SUBMISSION_UPDATE
- SUBMISSION_DELETE
- SUBMISSION_DOWNLOAD
- SUBMISSION_OPEN_LINK

### Assessment

- ASSESSMENT_VIEW
- ASSESSMENT_CREATE
- ASSESSMENT_UPDATE
- ASSESSMENT_DELETE
- ASSESSMENT_SCORE
- ASSESSMENT_PUBLISH

### Mentor Group

- GROUP_VIEW
- GROUP_CREATE
- GROUP_UPDATE
- GROUP_DELETE
- GROUP_MEMBER_ADD
- GROUP_MEMBER_REMOVE
- GROUP_JOIN

### System Config

- ROLE_PERMISSION_VIEW
- ROLE_PERMISSION_UPDATE
- FEATURE_FLAG_VIEW
- FEATURE_FLAG_UPDATE

## Feature Flag De Xuat Ban Dau

- STUDENT_PROFILE_UPDATE_ENABLED
- STUDENT_SUBMISSION_ENABLED
- STUDENT_VIEW_SCORE_ENABLED
- STUDENT_JOIN_GROUP_ENABLED
- MENTOR_SCORING_ENABLED
- MENTOR_GROUP_ENABLED
- WEEKLY_REPORT_SUBMISSION_ENABLED
- APPLICATION_REGISTRATION_ENABLED
- ASSESSMENT_RESULT_PUBLISHING_ENABLED

## Backend Huong Implement

### 1. Migration/Entity

Tao entity/repository cho:

- `Role`
- `Permission`
- `RolePermission`
- `SystemFeature`
- `RoleFeature`

Can can nhac mapping voi `UserRole` enum hien co. De giam rui ro, giai doan dau co the giu `User.role` enum va map sang `roles.role_code`. Chua can doi `User.role` thanh FK ngay neu dieu do lam lan rong migration.

### 2. Seed Default Permission

Seed permission mac dinh cho 3 role:

- ADMIN: gan tat ca permission va tat ca feature configurable enabled.
- MENTOR: chi gan permission lien quan mentor workflow, assessment scoring, student view theo scope.
- STUDENT: chi gan permission self-service va view du lieu cua minh.

Seed phai idempotent: chay lai khong tao duplicate.

### 3. Load Authority Khi Login

Khi authentication thanh cong, backend phai load permissions cua role va dua vao `GrantedAuthority`.

Huong chuyen doi:

- Giu role authority dang co: `ROLE_ADMIN`, `ROLE_MENTOR`, `ROLE_STUDENT` de tranh break ngay.
- Them permission authority: `STUDENT_VIEW`, `ASSESSMENT_SCORE`, ...
- Sau do tung buoc doi controller tu `hasRole` sang `hasAuthority`.

### 4. Security Check

- Endpoint quan tri role/permission/feature bat buoc can `ROLE_PERMISSION_VIEW`, `ROLE_PERMISSION_UPDATE`, `FEATURE_FLAG_VIEW`, `FEATURE_FLAG_UPDATE`.
- Controller nghiep vu moi nen dung `hasAuthority('<PERMISSION_CODE>')`.
- Khong chi dua vao FE an nut. Backend van la nguon phan quyen chinh.

### 5. Data Scope

Permission khong thay the data scope. Service van phai check:

- Mentor chi xem/sua student thuoc assignment/group cua minh.
- Student chi xem/sua du lieu cua minh.
- Mentor chi cham bai thuoc student minh quan ly.
- Admin co full scope neu co permission tuong ung.

Nen gom logic scope vao helper/service rieng, vi du `AccessScopeService`, de khong lap code trong tung service.

### 6. Feature Flag Service

Tao service:

- `isFeatureEnabled(String featureCode)`
- `isFeatureEnabledForRole(String featureCode, UserRole role)`
- `requireFeatureEnabledForCurrentRole(String featureCode)`

Neu feature disabled, tra `403` hoac `422` theo rule trong `http-error-response-rule.md`. Voi action bi tat boi cau hinh he thong, uu tien `403 FEATURE_DISABLED` neu da co error code, hoac them error code moi neu can.

## API De Xuat

### Role Permission Management

`GET /api/admin/roles`

`GET /api/admin/permissions`

`GET /api/admin/roles/{roleCode}/permissions`

`PUT /api/admin/roles/{roleCode}/permissions`

Body:

```json
{
  "permissions": [
    "STUDENT_VIEW",
    "ASSESSMENT_VIEW",
    "ASSESSMENT_SCORE"
  ]
}
```

### Feature Flag Management

`GET /api/admin/features`

`GET /api/admin/roles/{roleCode}/features`

`PUT /api/admin/roles/{roleCode}/features`

Body:

```json
{
  "features": [
    {
      "featureCode": "STUDENT_VIEW_SCORE_ENABLED",
      "enabled": true
    },
    {
      "featureCode": "MENTOR_SCORING_ENABLED",
      "enabled": false
    }
  ]
}
```

### Current User Capability

`GET /api/auth/me/capabilities`

Response:

```json
{
  "role": "MENTOR",
  "permissions": ["STUDENT_VIEW", "ASSESSMENT_SCORE"],
  "features": ["MENTOR_GROUP_ENABLED", "MENTOR_SCORING_ENABLED"]
}
```

FE dung endpoint nay de an/hien menu, button va route theo capability thay vi hard-code role qua nhieu noi.

## Frontend Huong Implement

### Access Guard Bat Buoc

- Tao capability/auth guard dung chung cho frontend.
- Moi menu item phai khai bao permission/feature can co.
- Moi route quan trong phai khai bao permission/feature can co.
- Moi action button can quyen rieng: create, edit, delete, download, score, publish.
- Neu user khong du quyen: an menu/action; neu truy cap URL truc tiep: render trang 403.
- Khong duoc chi check role trong component rieng le neu da co permission/capability.

### Admin UI

Tao page `Role & Permission` trong admin:

- Tab Permissions: matrix role x permission.
- Tab Feature Flags: matrix role x feature.
- Filter/search theo module: User, Student, Mentor, Assignment, Assessment, Group.
- Checkbox/toggle cho tung permission/feature.
- Save button co loading state.
- Confirm khi thay doi permission quan trong: ROLE_PERMISSION_UPDATE, FEATURE_FLAG_UPDATE, USER_DELETE.
- Hien warning neu admin dang tu tat quyen quan tri cua role admin.

### Capability Store

- Them FE service `capabilityService` goi `/api/auth/me/capabilities`.
- Luu capability vao auth context/state sau login va refresh app.
- Sidebar/menu/button nen check permission/feature thay vi check role truc tiep khi co the.
- Van co role fallback trong giai doan migration de khong break UI cu.

### UI Rule

- Page matrix phai compact, co sticky first column neu bang rong ngang.
- Module grouping ro rang, khong tao card qua lon.
- Desktop hien nhieu cot de admin thao tac nhanh.
- Co loading, empty, error state.

## Audit Bat Buoc Truoc Khi Implement

Truoc khi code, agent phai lap bang audit role access hien tai:

| Page/API/Action | Role dang xem/goi duoc | Role dung ra duoc phep | Gap | Cach sua BE | Cach sua FE |
| --- | --- | --- | --- | --- | --- |

Pham vi audit toi thieu:

- Sidebar/menu theo role.
- Dashboard theo role.
- Users, Students, Mentors, Companies.
- Applications, Weekly Reports, Submissions.
- Internship Phases, Assignments.
- Evaluation Criteria, Assessment Rounds, Assessment Results/Grading.
- Quick actions, export/report, detail/download/grade buttons.

Lenh goi y:

```powershell
rg -n "hasRole|hasAnyRole|hasAuthority|PreAuthorize" BE\src\main\java -g "*.java"
rg -n "role|currentUser|Sidebar|NavPage|can\(|permission|feature|admin|mentor|student" FE\src -g "*.ts" -g "*.tsx"
```

Khong duoc ket luan dua tren UI screenshot. Phai doi chieu controller authorization, service data scope va FE route/menu/action.

## Migration Strategy An Toan

Lam theo 3 phase, khong doi het mot luc:

### Phase 1: Additive

- Them bang roles/permissions/role_permissions/features/role_features.
- Seed du lieu default.
- Login/current user capabilities tra permissions/features.
- Chua doi tat ca `@PreAuthorize` ngay.

### Phase 2: Dual Check

- Gi? role authority hien co.
- Doi dan controller quan trong sang `hasAuthority`.
- Them test de dam bao ADMIN/MENTOR/STUDENT van truy cap dung nhu truoc sau khi seed permission.

### Phase 3: Admin Runtime Config

- Admin UI update role permission/feature.
- FE menu/action dua theo capabilities.
- Audit cac endpoint con hard-code role va lap danh sach can migrate tiep.

## Test Can Co

Backend:

- Seed roles/permissions/features idempotent.
- Login user co role MENTOR nhan duoc permission authority mac dinh.
- Endpoint admin role permission chi user co ROLE_PERMISSION_VIEW/UPDATE moi goi duoc.
- Update role permission thanh cong va lan login/request sau cap nhat authority/capability dung.
- Non-admin/non-permission bi 403.
- Feature disabled thi action bi chan dung rule.
- Response khong expose du lieu noi bo khong can thiet.

Frontend:

- Admin page render matrix permission/feature.
- Toggle permission va save goi dung API.
- Loading/error/empty state day du.
- Menu/button an/hien theo capabilities.
- Role khong du permission khong thay menu/action tuong ung.
- Role khong du permission truy cap route truc tiep thi thay trang 403, khong render data.
- `npm run lint` pass.
- `npm run build` pass.

## Acceptance Criteria

- Co database model cho role, permission, role permission, feature flag, role feature.
- Default seed du permission/feature cho ADMIN, MENTOR, STUDENT.
- Current user capabilities endpoint tra permissions/features an toan.
- Admin co UI dieu chinh permission/feature theo role.
- Backend khong expose password, token, permission internal metadata khong can thiet.
- Cac endpoint quan tri role/permission/feature duoc bao ve bang permission, khong chi bang FE.
- Data scope van duoc enforce trong service.
- Tat ca menu/action khong thuoc role/permission cua user phai bi an tren frontend.
- Tat ca route/page khong thuoc role/permission cua user phai bi chan bang route guard.
- Tat ca API khong thuoc role/permission cua user phai tra 403 khi goi truc tiep.
- Co bang audit role access hien tai va cach sua tung gap.
- Khong break login va role workflow hien co.
- Backend test pass.
- Frontend lint/build pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `13-dynamic-rbac-permission-feature-flags`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-task-13-da-sua>
git commit -m "feat: add dynamic role permissions and feature flags"
git push origin <branch-name>
```

Khong gom thay doi cua task khac neu cac task do chua duoc thuc hien/push rieng.
