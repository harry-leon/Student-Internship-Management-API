# Mentor Group Collaboration Room

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Bat buoc doc cac rule trong `docs/rule` truoc khi code:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/google-oauth-client-config-rule.md` neu co upload file/cloud storage lien quan.
- Truoc khi code, chay `npx skills find "spring boot websocket chat group task management file submission react realtime"`.
- Neu task co realtime chat/notification, uu tien tinh nang chuan cua Spring WebSocket/SSE hoac polling truoc khi them dependency lon.
- Neu task co UI phong chat/task board, doc/use `frontend-design` neu co san.
- Neu task co upload bai nop nhom, doc task 14 va reuse storage abstraction thay vi code upload rieng.
- Neu task co RBAC/permission, doc task 13 va enforce menu/route/action guard.

## Muc Tieu

Nang cap Mentor Group thanh phong lam viec nhom cho internship team. Group khong chi la danh sach sinh vien, ma la mot collaboration room gom:

- Chat noi bo giua mentor va thanh vien group.
- Announcement/thong bao cua mentor hoac leader.
- Task/kanban cong viec cua group co deadline, assignee, priority, status.
- Noi nop bai theo group thay vi tung student nop rieng le.
- Quan ly thanh vien va vai tro noi bo: owner/mentor, leader, member.
- Cau hinh room: ai duoc nhan tin, ai duoc tao task, ai duoc nop bai, ai duoc sua/xoa message/task.
- Admin co quyen cao nhat: xem tat ca room, tat ca message/task/submission/audit chi tiet hon mentor.

## Phan Tich Huong Di Tot Nhat

Khong nen tach chat, task va submission thanh cac module roi rac. Nen lay `MentorGroup` cua task 12 lam aggregate/root va bo sung cac sub-feature:

```text
MentorGroup
  - Members
  - Messages
  - Announcements
  - Tasks
  - Task Comments
  - Group Submissions
  - Room Settings
  - Audit Logs
```

Nop bai nen chuyen theo nhom:

- Group/leader nop file zip hoac GitHub link mot lan cho ca team.
- Submission gan voi `groupId`, `taskId` hoac `assignmentId/phaseId` tuy nghiep vu.
- Van luu duoc contributor/member list de mentor cham diem ca nhom va co the dieu chinh diem ca nhan neu can.

Chat realtime co the lam theo 2 giai doan:

1. MVP: REST + polling moi 5-10 giay cho message/task updates, de nhanh va it rui ro.
2. Realtime: Spring WebSocket/STOMP hoac Server-Sent Events sau khi workflow on dinh.

Khuyen nghi MVP nen lam REST + polling truoc, vi project dang can hoan thien CRUD, RBAC, upload va scope. WebSocket chi them khi da co auth/token va data scope chac chan.

## Role Trong Room

### System Roles

- ADMIN: quyen cao nhat tren toan he thong.
- MENTOR: owner/manager cua group minh tao hoac duoc admin gan.
- STUDENT: thanh vien group.

### Group Member Roles

- OWNER: mentor so huu group, toan quyen trong room.
- CO_MENTOR: mentor phu/assistant neu sau nay can.
- LEADER: truong nhom sinh vien, co quyen dieu phoi task va nop bai nhom neu mentor cho phep.
- MEMBER: thanh vien binh thuong.
- OBSERVER: chi xem, khong chat/nop bai/task neu can.

Quyen trong room khong thay the system permission. Dieu kien dung la:

```text
System permission + Group membership/role + Room setting + Data scope
```

## Tinh Nang Room Nen Co

### Chat

- Gui text message trong room.
- Reply/thread vao message.
- Pin message quan trong.
- Edit/delete message theo rule.
- Reaction emoji don gian neu can.
- Mark as read/read receipt theo user.
- Mention thanh vien bang `@name`.
- File attachment neu task 14 da co storage.
- Message moderation: mentor/admin co the xoa/an message vi pham.
- Slow mode hoac only-leader/only-mentor chat mode.

### Announcement

- Mentor/leader tao announcement.
- Announcement co title, content, priority, deadline/reminder optional.
- Pin announcement len dau room.
- Member mark as read.
- Admin xem audit ai da doc/chua doc neu can.

### Task Management

- Tao task cho group.
- Assign task cho 1 hoac nhieu member.
- Deadline, priority, status.
- Checklist/subtasks.
- Comment trong task.
- Attachment lien quan task.
- Status de xuat: TODO, IN_PROGRESS, REVIEW, DONE, BLOCKED, CANCELLED.
- Priority: LOW, MEDIUM, HIGH, URGENT.
- Overdue badge va reminder.
- Mentor/leader co the lock task khong cho member sua.
- History/audit khi doi status, assignee, deadline.

### Group Submission

- Nop bai theo group thay vi tung student rieng.
- Submission co the la GitHub link hoac file zip.
- Gan submission voi group task hoac assessment round.
- Leader nop bai neu mentor cho phep; mentor/admin co the nop thay trong truong hop can.
- Cho phep resubmit neu round/cong nop bai con mo.
- Luu version submission: lan nop 1, lan nop 2.
- Mentor cham/nhan xet submission cua group.
- Co the co diem group va diem ca nhan override.
- Admin xem duoc toan bo lich su nop bai, nguoi nop, file/link, IP/user agent neu can audit.

### Member Management

- Mentor add/remove/kick member.
- Mentor promote/demote leader/member/observer.
- Mentor khoa chat cua member rieng le neu can.
- Mentor chuyen leader.
- Mentor cau hinh ai duoc invite/add member.
- Leave group neu business cho phep; neu group gan internship chinh thuc thi khong cho student tu roi.
- Audit thanh vien: ai add, ai remove, ly do, thoi diem.

### Room Settings

- Chat mode:
  - ALL_MEMBERS: moi thanh vien duoc chat.
  - LEADER_ONLY: chi mentor/leader duoc chat, member chi xem.
  - MENTOR_ONLY: chi mentor/admin duoc thong bao.
  - MUTED: khoa chat tam thoi.
- Submission mode:
  - LEADER_ONLY.
  - ANY_MEMBER.
  - MENTOR_ONLY.
- Task create mode:
  - MENTOR_ONLY.
  - MENTOR_AND_LEADER.
- Message edit/delete window: vi du cho sua trong 15 phut.
- File attachment enabled/disabled.
- Max attachment size theo task 14.
- Auto reminder truoc deadline.

### Admin Oversight

Admin la quyen cao nhat, nen co view sau hon mentor:

- Xem tat ca group rooms.
- Xem detail room: member, role, messages, announcements, tasks, submissions.
- Xem audit log day du.
- Search/filter message/task/submission theo mentor, group, student, phase, status, overdue.
- Export room activity report neu can.
- Force close/archive room.
- Remove message/task/submission vi pham policy.
- Reassign group sang mentor khac neu can.
- Khong bi gioi han boi room setting cua mentor, nhung moi action admin phai audit.

## Database De Xuat

Reuse bang `mentor_groups` va `mentor_group_members` tu task 12, bo sung truong neu can.

### mentor_group_members bo sung

| Field | Type | Note |
| --- | --- | --- |
| group_role | varchar(30) | OWNER, CO_MENTOR, LEADER, MEMBER, OBSERVER |
| muted_until | timestamp/null | Khoa chat tam thoi |
| last_read_message_id | int/null | Read tracking nhanh |

### group_room_settings

| Field | Type | Note |
| --- | --- | --- |
| group_id | int | PK/FK mentor_groups |
| chat_mode | varchar(30) | ALL_MEMBERS, LEADER_ONLY, MENTOR_ONLY, MUTED |
| submission_mode | varchar(30) | LEADER_ONLY, ANY_MEMBER, MENTOR_ONLY |
| task_create_mode | varchar(30) | MENTOR_ONLY, MENTOR_AND_LEADER |
| allow_attachments | boolean | Default true |
| allow_member_invite | boolean | Default false |
| message_edit_window_minutes | int | Default 15 |
| auto_reminder_enabled | boolean | Default true |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### group_messages

| Field | Type | Note |
| --- | --- | --- |
| message_id | identity/int | PK |
| group_id | int | FK |
| sender_user_id | int | FK users |
| parent_message_id | int/null | Reply/thread |
| message_type | varchar(30) | TEXT, ANNOUNCEMENT, SYSTEM |
| content | text | Text content |
| pinned | boolean | Pin message |
| edited | boolean | Edited flag |
| deleted | boolean | Soft delete |
| deleted_by_user_id | int/null | Moderation |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### group_message_attachments

| Field | Type | Note |
| --- | --- | --- |
| id | identity/int | PK |
| message_id | int | FK group_messages |
| file_id | int/uuid | FK stored_files task 14 |

### group_announcements

Co the dung `group_messages.message_type=ANNOUNCEMENT` cho MVP. Neu can CRUD rieng:

| Field | Type | Note |
| --- | --- | --- |
| announcement_id | identity/int | PK |
| group_id | int | FK |
| author_user_id | int | FK users |
| title | varchar(200) | Required |
| content | text | Required |
| priority | varchar(20) | NORMAL, IMPORTANT, URGENT |
| pinned | boolean | Pin top |
| deadline_at | timestamp/null | Optional |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### group_tasks

| Field | Type | Note |
| --- | --- | --- |
| task_id | identity/int | PK |
| group_id | int | FK |
| creator_user_id | int | FK users |
| title | varchar(200) | Required |
| description | text | Optional |
| status | varchar(30) | TODO, IN_PROGRESS, REVIEW, DONE, BLOCKED, CANCELLED |
| priority | varchar(20) | LOW, MEDIUM, HIGH, URGENT |
| deadline_at | timestamp/null | Deadline |
| locked | boolean | Mentor lock |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### group_task_assignees

| Field | Type | Note |
| --- | --- | --- |
| task_id | int | FK group_tasks |
| student_id | int | FK students |

### group_task_comments

| Field | Type | Note |
| --- | --- | --- |
| comment_id | identity/int | PK |
| task_id | int | FK |
| author_user_id | int | FK users |
| content | text | Required |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### group_submissions

| Field | Type | Note |
| --- | --- | --- |
| submission_id | identity/int | PK |
| group_id | int | FK |
| task_id | int/null | FK group_tasks neu nop theo task |
| assessment_round_id | int/null | FK neu nop theo round |
| submitted_by_user_id | int | Leader/member/mentor nop |
| submission_type | varchar(20) | GITHUB_LINK, ZIP_FILE |
| github_url | varchar(500)/null | Neu nop GitHub |
| file_id | int/uuid/null | FK stored_files neu nop ZIP |
| version_number | int | Version nop bai |
| note | text/null | Ghi chu |
| status | varchar(30) | SUBMITTED, REVIEWED, NEEDS_CHANGES, ACCEPTED, REJECTED |
| submitted_at | timestamp | Audit |

### group_submission_reviews

| Field | Type | Note |
| --- | --- | --- |
| review_id | identity/int | PK |
| submission_id | int | FK group_submissions |
| reviewer_user_id | int | Mentor/Admin |
| score | decimal/null | Diem group |
| comment | text/null | Nhan xet |
| status | varchar(30) | DRAFT, SUBMITTED, PUBLISHED |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

### group_audit_logs

| Field | Type | Note |
| --- | --- | --- |
| audit_id | identity/int | PK |
| group_id | int | FK |
| actor_user_id | int | User thuc hien |
| action | varchar(100) | MEMBER_KICKED, TASK_CREATED, SUBMISSION_REVIEWED... |
| target_type | varchar(50) | MESSAGE, TASK, MEMBER, SUBMISSION |
| target_id | int/null | ID target |
| metadata_json | text/jsonb | Chi tiet thay doi |
| created_at | timestamp | Audit |

## Backend API De Xuat

### Room Detail

- `GET /api/mentor-groups/{groupId}/room`
- Tra summary: group, current user room role, settings, unread count, active tasks, upcoming deadlines, latest submission.

### Messages

- `GET /api/mentor-groups/{groupId}/messages?page=&size=&before=`
- `POST /api/mentor-groups/{groupId}/messages`
- `PUT /api/mentor-groups/{groupId}/messages/{messageId}`
- `DELETE /api/mentor-groups/{groupId}/messages/{messageId}` soft delete.
- `PATCH /api/mentor-groups/{groupId}/messages/{messageId}/pin`
- `POST /api/mentor-groups/{groupId}/messages/{messageId}/read`

### Announcements

- `GET /api/mentor-groups/{groupId}/announcements`
- `POST /api/mentor-groups/{groupId}/announcements`
- `PUT /api/mentor-groups/{groupId}/announcements/{announcementId}`
- `DELETE /api/mentor-groups/{groupId}/announcements/{announcementId}`
- `POST /api/mentor-groups/{groupId}/announcements/{announcementId}/read`

### Tasks

- `GET /api/mentor-groups/{groupId}/tasks?status=&assigneeId=&overdue=`
- `POST /api/mentor-groups/{groupId}/tasks`
- `GET /api/mentor-groups/{groupId}/tasks/{taskId}`
- `PUT /api/mentor-groups/{groupId}/tasks/{taskId}`
- `PATCH /api/mentor-groups/{groupId}/tasks/{taskId}/status`
- `DELETE /api/mentor-groups/{groupId}/tasks/{taskId}` soft delete/cancel.
- `POST /api/mentor-groups/{groupId}/tasks/{taskId}/comments`

### Group Submissions

- `GET /api/mentor-groups/{groupId}/submissions?taskId=&roundId=`
- `POST /api/mentor-groups/{groupId}/submissions/github`
- `POST /api/mentor-groups/{groupId}/submissions/zip` multipart, reuse task 14 storage.
- `GET /api/mentor-groups/{groupId}/submissions/{submissionId}`
- `GET /api/mentor-groups/{groupId}/submissions/{submissionId}/download` neu ZIP.
- `POST /api/mentor-groups/{groupId}/submissions/{submissionId}/reviews` mentor/admin review.
- `PATCH /api/mentor-groups/{groupId}/submissions/{submissionId}/status`.

### Member And Room Settings

- `PATCH /api/mentor-groups/{groupId}/members/{studentId}/role` promote/demote.
- `DELETE /api/mentor-groups/{groupId}/members/{studentId}` kick/remove.
- `PATCH /api/mentor-groups/{groupId}/members/{studentId}/mute` mute/unmute.
- `GET /api/mentor-groups/{groupId}/settings`
- `PUT /api/mentor-groups/{groupId}/settings`

### Admin Oversight

- `GET /api/admin/group-rooms` list all rooms.
- `GET /api/admin/group-rooms/{groupId}` detail deep view.
- `GET /api/admin/group-rooms/{groupId}/audit-logs`.
- `PATCH /api/admin/group-rooms/{groupId}/archive`.
- `POST /api/admin/group-rooms/{groupId}/reassign-mentor`.

## Authorization Va Data Scope

### Mentor

- Mentor co full CRUD trong room cua minh neu system permission/feature cho phep.
- Mentor co the manage member, task, settings, message moderation, submission review.
- Mentor khong duoc xem room cua mentor khac tru khi la co-mentor/admin.

### Student Leader

- Leader co quyen tao/cap nhat task neu room setting cho phep.
- Leader co quyen submit bai nhom neu submission mode la LEADER_ONLY hoac ANY_MEMBER.
- Leader co the pin announcement/task neu mentor cho phep.
- Leader khong duoc kick mentor/admin.

### Student Member

- Member chat neu chat mode cho phep.
- Member xem task/submission cua group minh.
- Member submit neu submission mode ANY_MEMBER.
- Member khong duoc cham diem, publish review, kick member, doi settings.

### Admin

- Admin co quyen cao nhat va xem duoc moi room/detail/audit.
- Admin action phai audit day du.
- Admin khong bi gioi han boi room settings, nhung van can system permission neu task 13 da enforce dynamic permission.

## Permission/Feature Codes De Xuat

- GROUP_ROOM_VIEW
- GROUP_ROOM_MANAGE
- GROUP_MESSAGE_SEND
- GROUP_MESSAGE_MODERATE
- GROUP_ANNOUNCEMENT_MANAGE
- GROUP_TASK_VIEW
- GROUP_TASK_MANAGE
- GROUP_SUBMISSION_VIEW
- GROUP_SUBMISSION_CREATE
- GROUP_SUBMISSION_REVIEW
- GROUP_MEMBER_MANAGE
- GROUP_ROOM_SETTINGS_UPDATE
- ADMIN_GROUP_ROOM_VIEW_ALL
- ADMIN_GROUP_ROOM_AUDIT_VIEW

Feature flags:

- GROUP_CHAT_ENABLED
- GROUP_TASK_ENABLED
- GROUP_SUBMISSION_ENABLED
- GROUP_FILE_ATTACHMENT_ENABLED
- GROUP_REALTIME_ENABLED

## Frontend Huong Implement

### Room Layout

Mentor/Student room nen co layout 3 vung:

```text
Left: Group/member list + role/status
Center: Chat/messages/announcement feed
Right: Active tasks, deadlines, submissions, room settings/action panel
```

Tren mobile, dung tabs:

- Chat
- Tasks
- Submissions
- Members

### Mentor UI

- Room dashboard: unread, overdue tasks, pending submissions.
- Chat moderation: pin/delete/mute.
- Task CRUD: create/edit/assign/status/deadline.
- Submission review: open GitHub/download ZIP, comment, score/status.
- Member management: kick, promote leader/member, mute, add member.
- Room settings: chat mode, submission mode, task create mode, attachments.

### Student UI

- Chat room theo quyen.
- Xem announcement pinned.
- Xem task cua group va task duoc assign.
- Update status task neu duoc assign va setting cho phep.
- Submit group work neu la leader/member co quyen.
- Khong thay chuc nang review/cham diem/admin oversight.

### Admin UI

- Danh sach tat ca rooms voi filter mentor, phase, status, overdue, pending submission.
- Deep detail room: members, messages, tasks, submissions, reviews, audit logs.
- Action archive, reassign mentor, moderate content.
- Admin UI nen ro rang la view giam sat, khong giong student/mentor room binh thuong.

## Notification/Reminder

- Khi mentor tao task/deadline, tao notification cho assignees/group.
- Reminder truoc deadline 24h va khi overdue.
- Khi co submission moi, notify mentor.
- Khi mentor review, notify group members.
- Khi promote/kick/mute, notify member lien quan.

## MVP De Xuat

De tranh task qua lon, implement theo thu tu:

1. Room settings + group member roles.
2. REST chat messages + polling.
3. Announcement CRUD.
4. Group task CRUD + assignees + deadline.
5. Group submission GitHub/ZIP reuse task 14.
6. Mentor review group submission.
7. Admin oversight + audit logs.
8. WebSocket/SSE realtime neu can sau khi MVP on dinh.

## Error Handling

- User khong trong group: 403.
- Role trong group khong du quyen: 403.
- Room setting khong cho action: 403 hoac 422 theo rule.
- Task/submission/message khong ton tai: 404.
- Deadline da qua va khong cho submit: 422.
- Duplicate active leader neu chi cho 1 leader: 409.
- File upload loi: theo task 14.

## Test Can Co

Backend:

- Mentor tao/update room settings thanh cong.
- Student ngoai group khong xem room duoc.
- Member bi mute khong gui message duoc.
- Chat mode LEADER_ONLY chan member gui message.
- Mentor promote member thanh leader thanh cong.
- Mentor kick member thanh cong va member khong truy cap room duoc.
- Leader tao task/submit bai khi setting cho phep.
- Member khong review/cham submission duoc.
- Mentor review group submission thanh cong.
- Admin xem room cua moi mentor va audit logs duoc.
- Admin action duoc ghi audit.

Frontend:

- Mentor room hien member/task/submission/settings/action day du.
- Student room an cac action review/cham diem/settings/kick neu khong co quyen.
- Leader thay action duoc cap quyen, member thuong khong thay.
- Admin oversight page hien detail sau hon mentor.
- Loading/empty/error state day du.
- `npm run lint` pass.
- `npm run build` pass.

## Acceptance Criteria

- Mentor group da tro thanh collaboration room co chat, announcement, task, deadline va group submission.
- Mentor co full CRUD trong room minh quan ly: message moderation, announcement, task, submission review, member role, room settings.
- Student nop bai theo group thay vi chi nop rieng tung ca nhan trong flow group.
- Leader/member permission trong room hoat dong dung theo settings.
- Role khong co quyen thi menu/action bi an va API/route bi chan theo task 13.
- Upload/download ZIP/attachment reuse task 14 storage, khong code rieng.
- Admin co deep oversight view va quyen cao nhat tren moi room.
- Moi action quan trong co audit log.
- Backend test pass.
- Frontend lint/build pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `15-mentor-group-collaboration-room`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-task-15-da-sua>
git commit -m "feat: add mentor group collaboration rooms"
git push origin <branch-name>
```

Khong gom thay doi cua task khac neu cac task do chua duoc thuc hien/push rieng.
