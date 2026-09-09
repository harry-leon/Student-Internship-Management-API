# Mentor Group Management

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Bat buoc doc cac rule trong `docs/rule` truoc khi code:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code, chay `npx skills find "spring boot group membership invitation code react admin mentor student workflow"`.
- Neu task co thiet ke UI group/join flow, doc/use `frontend-design` neu co san.
- Neu task co them backend CRUD/test, doc/use `spring-boot-crud-patterns` va `spring-boot-test-patterns` neu co san.

## Muc Tieu

Bo sung tinh nang Mentor tao group/lop de quan ly sinh vien. Sinh vien co the duoc them vao group bang 2 cach:

1. Mentor add thu cong bang email hoac username cua student.
2. Student tu join bang ma lop/group code va mat khau join.

Student co the tim group theo ten mentor de xem cac group mentor do dang so huu, sau do nhap ma lop va mat khau de vao group.

## Pham Vi Nghiep Vu

### Mentor

- Tao group moi gan voi mentor hien tai va phase thuc tap.
- Xem danh sach group cua minh.
- Xem detail group, danh sach member, so luong member hien tai.
- Sua thong tin group: ten group, mo ta, active/inactive, max students, join password.
- Add student thu cong bang email hoac username.
- Remove student khoi group minh so huu.
- Dong/mo group de chan hoac cho phep student tu join.

### Student

- Xem group minh dang tham gia.
- Tim group theo ten mentor hoac group code.
- Join group bang group code va join password.
- Roi group neu nghiep vu cho phep; neu group gan voi assignment chinh thuc thi nen khong cho roi truc tiep, chi mentor/admin remove.

### Admin

- Xem tat ca group trong he thong.
- Xem detail group va member.
- Tao/sua/deactivate group neu can ho tro van hanh.
- Remove member trong truong hop can dieu chinh du lieu.

## Database De Xuat

Tao 2 bang moi de khong lam lech nghiep vu assignment hien co.

### mentor_groups

| Field | Type | Note |
| --- | --- | --- |
| group_id | identity/int | Primary key |
| mentor_id | int | FK mentors. Bat buoc |
| phase_id | int | FK internship_phases. Bat buoc neu he thong dang quan ly theo dot |
| group_name | varchar(150) | Ten group hien thi |
| group_code | varchar(30) | Unique, dung de student join |
| join_password_hash | varchar(255) | Hash bang PasswordEncoder, khong luu plain text |
| description | text | Mo ta ngan |
| max_students | int | Gioi han thanh vien, default 30 |
| is_active | boolean | Cho phep tim/join hay khong |
| allow_self_join | boolean | Bat/tat join bang code |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### mentor_group_members

| Field | Type | Note |
| --- | --- | --- |
| member_id | identity/int | Primary key |
| group_id | int | FK mentor_groups |
| student_id | int | FK students |
| join_method | enum/string | MANUAL, CODE |
| status | enum/string | ACTIVE, REMOVED |
| added_by_user_id | int | User add member, null neu student tu join |
| joined_at | timestamp | Thoi diem vao group |
| removed_at | timestamp | Thoi diem bi remove neu co |

Rang buoc nen co:

- Unique `group_code`.
- Unique active membership theo `group_id + student_id`.
- Neu quy dinh mot student chi o mot group trong mot phase, enforce o service va co the them unique partial index/migration sau.

## Quy Tac Nghiep Vu Can Ap Dung

- Password join phai hash bang `PasswordEncoder`.
- API response khong bao gio tra `joinPasswordHash`.
- Student chi join duoc group `isActive = true` va `allowSelfJoin = true`.
- Khi join, neu sai code/password tra loi nghiep vu chung, khong tiet lo password dung hay sai qua chi tiet qua muc.
- Neu group day member, tra `409` hoac `422` theo rule hien co.
- Mentor chi quan ly group do chinh mentor do so huu.
- Admin co quyen xem/quan ly tat ca group.
- Mac dinh nen gioi han student chi co 1 active group trong cung phase neu group dung cho quan ly thuc tap chinh thuc.
- Khong thay the `InternshipAssignment`; group la lop quan ly/bo loc. Assignment van la nguon chinh cho phan cong thuc tap.

## API De Xuat

### Mentor/Admin Group CRUD

`POST /api/mentor-groups`

Body:

```json
{
  "groupName": "SE Internship Group A",
  "phaseId": 1,
  "joinPassword": "abc123",
  "description": "Group quan ly sinh vien ky Spring 2026",
  "maxStudents": 30,
  "allowSelfJoin": true
}
```

`GET /api/mentor-groups/my`: mentor xem group cua minh.

`GET /api/mentor-groups`: admin list all co filter `mentorName`, `phaseId`, `active`, pagination.

`GET /api/mentor-groups/{groupId}`: detail group + summary member.

`PUT /api/mentor-groups/{groupId}`: update thong tin group.

`PATCH /api/mentor-groups/{groupId}/status`: active/inactive group.

`PATCH /api/mentor-groups/{groupId}/join-password`: doi mat khau join.

### Member Management

`POST /api/mentor-groups/{groupId}/members`

Body:

```json
{
  "identifier": "student1@fpt.edu.vn"
}
```

Identifier chap nhan username hoac email.

`GET /api/mentor-groups/{groupId}/members`: list member co pagination/search.

`DELETE /api/mentor-groups/{groupId}/members/{studentId}`: remove/deactivate member.

### Student Search Va Join

`GET /api/mentor-groups/search?mentorName=le%20thi&groupCode=SE-A`

Response chi tra public info:

```json
[
  {
    "groupId": 1,
    "groupName": "SE Internship Group A",
    "groupCode": "SE-A-2026",
    "mentorName": "Dr. Le Thi B",
    "phaseName": "Spring 2026 Batch A",
    "memberCount": 8,
    "maxStudents": 30,
    "allowSelfJoin": true
  }
]
```

`POST /api/mentor-groups/join`

Body:

```json
{
  "groupCode": "SE-A-2026",
  "joinPassword": "abc123"
}
```

`GET /api/mentor-groups/me`: student xem group dang tham gia.

## DTO Va Bao Mat Response

Uu tien reuse DTO phong cach hien co, nhung khong expose entity truc tiep.

DTO can co toi thieu:

- `MentorGroupResponse`: thong tin group an toan, khong co password hash.
- `MentorGroupDetailResponse`: group + member summary.
- `MentorGroupCreateRequest`.
- `MentorGroupUpdateRequest`.
- `AddGroupMemberRequest`.
- `JoinGroupRequest`.
- `GroupMemberResponse`.

Khong tao DTO rieng cho tung role neu cung response an toan co the reuse. Neu student search group, response phai cat bot field noi bo.

## Repository Query Can Co

- Tim group theo `groupCode`.
- Search group theo mentor fullName va optional groupName/groupCode.
- Count member active theo group.
- Check duplicate active membership.
- List group by mentor.
- List active group cua student theo phase.

Nen dung projection cho list/search neu can hien count member de tranh N+1.

## Frontend Can Lam

### Mentor UI

- Them menu/page `Groups` cho mentor.
- List group compact: ten group, code, phase, member count, status, self join on/off.
- Create/edit modal hoac page.
- Detail group co table member.
- Add member form nhap email/username.
- Remove member co confirm.
- Button copy group code.
- Change join password flow rieng, khong hien password cu.

### Student UI

- Page tim/join group.
- Search box cho mentor name hoac group code.
- Ket qua search compact, hien mentor, phase, member count.
- Join modal nhap password.
- Trang `My Group` xem group dang tham gia va mentor phu trach.

### Admin UI

- List tat ca group co filter mentor/phase/status.
- Detail group va member.
- Action deactivate/remove member neu can.

UI phai theo task 09: compact, nhieu cot tren desktop, khong lam card qua lon, co loading/empty/error state.

## Test Can Co

Backend:

- Mentor tao group thanh cong.
- Mentor khong sua/remove member group cua mentor khac.
- Admin quan ly duoc group bat ky.
- Mentor add student bang username/email thanh cong.
- Add duplicate student tra conflict/business error dung rule.
- Student join dung code/password thanh cong.
- Student join sai password tra loi an toan.
- Group inactive hoac self join off thi student khong join duoc.
- Group full thi khong join duoc.
- Response khong chua `joinPasswordHash`.

Frontend:

- `npm run lint` pass.
- `npm run build` pass.
- Mentor tao group, add/remove member UI goi dung API.
- Student search group va join co loading/error/empty state.
- Admin thay list/detail group neu role admin.

## Thu Tu Thuc Hien

1. Doc rule va chay skill discovery.
2. Audit entity/repository/service hien co: Mentor, Student, InternshipPhase, Assignment.
3. Thiet ke migration/entity cho `MentorGroup` va `MentorGroupMember`.
4. Implement repository projection/query.
5. Implement service voi transaction va business validation.
6. Implement controller + `@PreAuthorize`.
7. Implement FE service types.
8. Implement Mentor group pages.
9. Implement Student search/join pages.
10. Implement Admin group list/detail neu navigation admin yeu cau.
11. Them seed data group demo neu can smoke test.
12. Chay backend test, FE lint/build, smoke test login Mentor/Student/Admin.

## Acceptance Criteria

- Mentor tao/sua/deactivate group cua minh duoc.
- Mentor add student bang email hoac username duoc.
- Student tim group theo ten mentor hoac group code duoc.
- Student join group bang group code + password duoc.
- Password join duoc hash, khong tra ve API response.
- Duplicate membership va group full co error dung rule.
- Phan quyen dung: mentor khong thao tac group nguoi khac, student khong xem du lieu noi bo, admin quan ly duoc tat ca.
- UI co loading, empty, error state va layout compact theo task 09.
- Khong lam cham dashboard/list: list/search dung projection hoac query co count hop ly, tranh N+1.
- Backend test pass.
- Frontend lint/build pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `12-mentor-group-management`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-task-12-da-sua>
git commit -m "feat: add mentor group management"
git push origin <branch-name>
```

Khong gom thay doi cua task khac neu cac task do chua duoc thuc hien/push rieng.
