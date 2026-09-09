# Task 19 - Group Workspace UX, Read Receipts and Online Presence

## Skill n?n d?ng

- B?t bu?c d?ng skill local `internship-management-system` trong `SKILL.md` n?u t?n t?i.
- B?t bu?c ??c c?c rule tr??c khi code:

```powershell
Get-Content docs\rule\http-error-response-rule.md
Get-Content docs\rule\skill-query-rule.md
Get-Content docs\rule\library-reuse-rule.md
Get-Content docs\rule\frontend-ui-configuration-rule.md
Get-Content docs\feature-roadmap\15-mentor-group-collaboration-room.md
Get-Content docs\feature-roadmap\16-business-scope-group-task-submission-redesign.md
Get-Content docs\feature-roadmap\17-system-wide-dark-theme-standardization.md
Get-Content docs\feature-roadmap\18-frontend-hardcoded-demo-data-cleanup.md
```

- Tr??c khi code, ch?y skill discovery:

```powershell
npx skills find "spring boot websocket chat read receipts online presence react group workspace"
npx skills find "database design chat read receipts presence indexes"
npx skills find "react chat ui read receipts online status accessibility"
```

- ?p d?ng t? duy c?a c?c skill:
  - `database-schema-designer`: schema chu?n, FK/index/constraint r? cho messages/read receipts/presence.
  - `spring-boot-crud-patterns`: API CRUD/transaction/DTO ??ng boundary.
  - `frontend-design`: UI group workspace ??p, compact, d? d?ng trong h? th?ng qu?n l? th?c t?p.

## Library reuse va realtime decision

Agent phai doc docs/rule/library-reuse-rule.md va chon ro MVP polling hay realtime truoc khi implement.

Khuyen nghi cho task 19:

- MVP uu tien REST API + @tanstack/react-query polling/refetch cho messages, read receipts, presence, tasks va submissions neu task 18 da them TanStack Query.
- Neu can realtime that, backend nen dung spring-boot-starter-websocket + Spring Messaging/STOMP; frontend nen dung @stomp/stompjs. Chi them sockjs-client neu can SockJS fallback.
- Popover/member detail/tooltip khong nen tu code positioning/focus trap neu component hien co chua tot. Danh gia Radix UI primitives hoac Floating UI truoc.
- Reuse motion da co cho animation avatar/read receipt/message transition nho.
- Reuse lucide-react cho icon va component upload/file cua task 14 cho attachment/avatar.
- Chi them react-dropzone neu upload drag-drop duoc dung lap lai o nhieu man hinh va component hien co khong dap ung.

Can kiem tra truoc khi code:

```powershell
Get-Content BE\build.gradle
Get-Content FE\package.json
rg -n "WebSocket|STOMP|SimpMessagingTemplate|EventSource|@tanstack|react-query|setInterval|messages|presence|read" BE FE -S
```

Final note phai ghi ro: da chon polling/SSE/WebSocket, thu vien nao da them hoac reuse, polling interval, cleanup subscription/timer, va cach enforce RBAC o BE/FE.
## B?i c?nh

Trang `/groups/:groupId` hi?n ?ang gi?ng m?t chat room c? b?n, v?ng n?i dung c?n tr?ng, c?c ch?c n?ng task/submission/member/settings ch?a ?? r?. User mu?n t?i ?u trang group th?nh workspace l?m vi?c nh?m th?c s?.

Y?u c?u m?i b? sung:

- Tin nh?n ph?i th? hi?n tr?ng th?i ?? ??c gi?ng Messenger.
- Th?nh vi?n n?o ?? xem tin nh?n s? hi?n th? avatar nh? ? g?c/d??i tin nh?n.
- Khi nh?n v?o avatar ?? xem, m? th?ng tin chi ti?t th?nh vi?n ??.
- Avatar th?nh vi?n online c? ch?m tr?n m?u xanh ? g?c.
- Online status d?a tr?n ??ng nh?p/ho?t ??ng hi?n t?i, kh?ng ???c hardcode.

## M?c ti?u

N?ng c?p group room th?nh `Group Workspace` g?m:

1. Chat c? read receipts theo t?ng message.
2. Online presence cho member trong group.
3. Member detail popover/drawer khi click avatar.
4. Group tabs r? r?ng: Chat, Tasks, Submissions, Members, Announcements, Files, Activity.
5. Mentor c? ?? quy?n qu?n l? group room theo task 15.
6. Admin xem ???c th?ng tin chi ti?t h?n mentor.
7. Student ch? th?y group m?nh tham gia v? ch?c n?ng ???c ph?p.
8. UI ??ng b? v?i theme/config chung, kh?ng t?o layout l? t?.

## Ph?m vi ch?c n?ng

### 1. Group Header

Header c?a group c?n hi?n th? r?:

- t?n group
- m? group
- phase
- mentor owner
- s? member
- s? member online
- s? task ?ang m?
- deadline g?n nh?t
- submission ch? review
- quick actions theo permission

Action g?i ?:

- `T?o task`
- `T?o th?ng b?o`
- `Th?m th?nh vi?n`
- `C?i ??t nh?m`
- `L?m m?i`

Kh?ng hi?n action n?u user kh?ng c? quy?n.

### 2. Group Navigation

Thay c?t icon kh? hi?u b?ng tab/segmented navigation c? label r?:

```text
Chat
Tasks
Submissions
Members
Announcements
Files
Activity
```

C? th? gi? icon nh?ng ph?i c? tooltip/label. Tab ?ang active ph?i r? ? light v? dark theme.

### 3. Chat Messages

Chat c?n h? tr?:

- g?i text message
- hi?n th? avatar/name/role/time
- ph?n bi?t message c?a m?nh v? ng??i kh?c
- pin message quan tr?ng n?u c? quy?n
- delete/hide message n?u mentor/admin c? quy?n
- file attachment n?u task 14 ?? ho?n thi?n
- empty state khi ch?a c? message
- loading state khi ?ang t?i message
- error state ??ng rule HTTP error

Kh?ng render message demo n?u API tr? r?ng.

### 4. Read Receipts

M?i message c?n c? danh s?ch member ?? ??c.

UI y?u c?u:

- D??i ho?c g?c ph?i message hi?n th? t?i ?a 3-5 avatar nh? c?a nh?ng ng??i ?? xem g?n nh?t.
- N?u nhi?u h?n gi?i h?n, hi?n th? `+N`.
- Hover/click v?o v?ng read receipt hi?n danh s?ch ng??i ?? xem.
- Click v?o avatar m? popover/drawer chi ti?t th?nh vi?n.
- Kh?ng hi?n th? avatar c?a ng??i g?i trong read receipt n?u nghi?p v? kh?ng c?n.
- Ch? hi?n th? read receipt trong ph?m vi group, kh?ng l? user ngo?i group.

Th?ng tin trong detail member:

- avatar
- h? t?n
- username/email n?u ???c ph?p
- role h? th?ng
- role trong group: owner/leader/member/observer
- tr?ng th?i online/offline
- l?n ho?t ??ng g?n nh?t
- s? task ???c giao
- s? submission ?? n?p
- action theo quy?n: xem profile, promote/demote, kick, nh?n ri?ng n?u sau n?y c?

### 5. Online Presence

Avatar member ph?i c? ch?m tr?ng th?i:

- Online: ch?m xanh l? ? g?c avatar.
- Away/idle n?u c?: ch?m v?ng ho?c kh?ng b?t bu?c MVP.
- Offline: kh?ng c? ch?m ho?c ch?m x?m nh?.

Ngu?n d? li?u:

- Kh?ng hardcode online state.
- MVP c? th? d?a v?o `lastSeenAt` ho?c heartbeat polling.
- Realtime phase c? th? d?ng WebSocket/SSE.

?? xu?t MVP:

- FE g?i heartbeat m?i 30-60 gi?y khi user ?ang authenticated v? tab visible.
- BE l?u `last_seen_at`, `last_active_group_id` optional.
- User ???c coi l? online n?u `last_seen_at >= now - 2 minutes`.
- Khi tab hidden ho?c logout, gi?m ?? ?u ti?n hi?n th? online sau timeout.

### 6. Members Panel

C?n c? panel danh s?ch member:

- search member
- filter role: owner/leader/member/observer
- filter online/offline
- avatar + online dot
- role badge
- task count/submission count n?u c?
- action theo quy?n: promote leader, demote, kick, reset member password n?u nghi?p v? c?n

Mentor qu?n l? group m?nh. Admin qu?n l? to?n h? th?ng. Student ch? xem member c? b?n n?u ???c ph?p.

### 7. Tasks Panel

Trang group c?n qu?n l? task r?:

- mentor t?o task trong group
- ch?n assignee, m?c ??nh ch?n t?t c? active members
- deadline
- priority
- status
- checklist/subtask n?u c?
- comment trong task
- attachment n?u ?? c? storage
- task detail drawer

Task ph?i g?n v?i group v? member assignees th?t t? database.

### 8. Submissions Panel

Submission n?m trong group/task:

- Student/Leader n?p GitHub URL ho?c ZIP theo task ???c giao.
- N?u group setting l? `LEADER_ONLY`, ch? leader ho?c mentor ???c n?p.
- Mentor/Admin download ZIP ho?c m? GitHub.
- Mentor review/ch?m ?i?m/comment.
- Student xem feedback khi ???c publish.
- L?u l?ch s? version n?p b?i.

### 9. Announcements, Files, Activity

N?n c? ?t nh?t skeleton/empty state ho?c MVP:

- Announcements: mentor/admin t?o th?ng b?o, pin, mark as read.
- Files: t?i li?u y?u c?u, t?i li?u tham kh?o, submission files.
- Activity: log member join/leave, task created, submission uploaded, score updated, setting changed.

Kh?ng d?ng d? li?u demo.

## Database thi?t k? ?? xu?t

N?u BE ch?a c? ??y ??, b? sung schema theo h??ng m? r?ng t? `mentor_groups` hi?n t?i.

C?c b?ng/entity n?n c? ho?c r? so?t:

```text
mentor_groups
group_members
group_messages
group_message_reads
group_member_presence
group_announcements
group_announcement_reads
group_tasks
group_task_assignees
group_task_comments
group_submissions
group_submission_reviews
group_files
group_activity_logs
```

### Read receipt schema

`group_message_reads`:

- `message_read_id` bigint PK
- `message_id` FK -> `group_messages.message_id`
- `group_id` FK -> `mentor_groups.group_id`
- `user_id` FK -> `users.user_id`
- `read_at` timestamp not null
- unique `(message_id, user_id)`
- index `(group_id, user_id, read_at)`
- index `(message_id, read_at)`

L?u `group_id` ?? query theo group nhanh h?n, nh?ng v?n ph?i validate message thu?c group.

### Presence schema

`group_member_presence` ho?c m? r?ng user session/presence table:

- `presence_id` bigint PK
- `user_id` FK -> `users.user_id`
- `group_id` FK nullable -> `mentor_groups.group_id`
- `status` enum: ONLINE, AWAY, OFFLINE
- `last_seen_at` timestamp not null
- `last_heartbeat_at` timestamp
- `updated_at` timestamp
- unique `(user_id)` n?u global presence
- ho?c unique `(group_id, user_id)` n?u presence theo group
- index `(group_id, status, last_seen_at)`
- index `(user_id, last_seen_at)`

Khuy?n ngh? MVP: global user presence ??n gi?n theo `user_id`, sau ?? join v?i group members.

### Constraint v? data integrity

- Message ph?i thu?c group t?n t?i.
- Read receipt ch? ???c t?o cho user l? member c?a group ho?c admin/mentor c? quy?n view.
- Kh?ng t?o duplicate read receipt cho c?ng `message_id + user_id`.
- X?a group kh?ng n?n x?a v?t l? message n?u c?n audit; ?u ti?n soft delete/archived.
- Message delete n?n l? soft delete: `deleted_at`, `deleted_by`, `delete_reason`.

## Backend API ?? xu?t

D?ng resource-based URL, kh?ng ??a role v?o path.

### Group overview

```http
GET /api/groups/{groupId}/workspace
```

Tr? v?:

- group info
- current user group role
- room settings
- member summary
- unread counts
- task/submission KPI
- permission flags n?u c?n FE render nhanh

### Messages

```http
GET    /api/groups/{groupId}/messages?before=&after=&limit=50
POST   /api/groups/{groupId}/messages
PATCH  /api/groups/{groupId}/messages/{messageId}
DELETE /api/groups/{groupId}/messages/{messageId}
POST   /api/groups/{groupId}/messages/{messageId}/read
POST   /api/groups/{groupId}/messages/read-batch
GET    /api/groups/{groupId}/messages/{messageId}/reads
```

Batch read endpoint n?n d?ng khi user m? room ?? mark nhi?u message ?? ??c, tr?nh g?i API t?ng message.

### Presence

```http
POST /api/me/presence/heartbeat
GET  /api/groups/{groupId}/presence
```

Heartbeat request:

```json
{
  "activeGroupId": 1,
  "status": "ONLINE"
}
```

### Member detail

```http
GET /api/groups/{groupId}/members/{memberId}/detail
```

Response kh?ng ???c expose d? li?u nh?y c?m ngo?i quy?n user.

### Tasks/Submissions

Reuse API ?? c? n?u task 15/16 ?? implement. N?u ch?a, tri?n khai theo resource-based URL:

```http
GET  /api/groups/{groupId}/tasks
POST /api/groups/{groupId}/tasks
GET  /api/groups/{groupId}/submissions
POST /api/groups/{groupId}/tasks/{taskId}/submissions
```

## System Notifications realtime

Task 19 phai kiem tra lai toan bo he thong thong bao hien co, vi notification la kenh can realtime cua group workspace va toan he thong.

Muc tieu notification:

1. Thong bao phai cap nhat realtime hoac near-realtime, khong bat user reload browser moi thay badge/thong bao moi.
2. Badge tren header phai hien unread count dung theo user hien tai.
3. Notification phai co read/unread, mark one as read, mark all as read.
4. Notification phai co link dieu huong den dung context: group, task, submission, assessment result, member request, system setting.
5. Student/Mentor/Admin chi thay notification trong scope permission cua minh.
6. Admin co the xem audit/log rong hon va notification he thong quan trong.
7. Khong hardcode notification demo.

Su kien can tao notification:

- Mentor tao/cap nhat/xoa task trong group.
- Task sap den deadline hoac qua deadline.
- Student/leader nop bai moi hoac nop lai version moi.
- Mentor review/cham diem/comment submission.
- Diem/feedback duoc publish cho student.
- Co tin nhan mention user bang @username hoac @all neu user co quyen.
- Co announcement moi trong group.
- User duoc them vao group, bi kick, duoc promote/demote leader/member.
- Yeu cau join group duoc approve/reject.
- File/attachment duoc upload vao group/task.
- Permission/role thay doi anh huong den user dang dang nhap.
- Loi he thong quan trong hoac maintenance message cho admin.

Tinh nang notification can co:

- Notification dropdown trong header: unread first, grouped by time, co icon/type/status.
- Notification center page neu danh sach dai: filter unread/type/date/group, search noi dung, pagination.
- Mark as read khi click vao notification hoac nut rieng tuy loai su kien.
- Mark all as read.
- Delete/archive notification ca nhan neu can.
- Deep link den resource lien quan.
- Toast/in-app banner cho notification moi khi user dang online.
- Quiet handling: khong spam toast voi moi message chat thuong, chi toast cho mention, assignment, deadline, review, announcement quan trong.
- Admin co tab system/audit notifications neu backend co data.

API de xuat:

```http
GET    /api/me/notifications?status=&type=&page=&size=
GET    /api/me/notifications/unread-count
PATCH  /api/me/notifications/{notificationId}/read
PATCH  /api/me/notifications/read-all
DELETE /api/me/notifications/{notificationId}
```

Neu dung realtime:

```text
/topic/users/{userId}/notifications
/topic/users/{userId}/notification-count
```

Neu chua dung WebSocket/SSE, MVP phai polling:

- unread count: 15-30 giay.
- notification list khi dropdown dang mo: 15-30 giay.
- refetch ngay sau mark read/read all.

Database goi y:

```text
notifications
notification_recipients
```

`notifications` luu event goc: type, title, message, actor_id, resource_type, resource_id, group_id nullable, metadata json, created_at.

`notification_recipients` luu theo user: notification_id, user_id, read_at, delivered_at, archived_at, unique(notification_id, user_id), index(user_id, read_at, created_at).

Khong tron read state vao notification goc neu mot notification gui cho nhieu user.
## Realtime strategy

Kh?ng b?t bu?c ph?i l?m WebSocket ngay n?u r?i ro cao.

MVP ch?p nh?n:

- messages polling m?i 5-10 gi?y
- presence polling m?i 30 gi?y
- heartbeat m?i 30-60 gi?y
- optimistic append message sau khi POST th?nh c?ng
- notification unread-count polling 15-30 giay neu chua co WebSocket/SSE

Phase sau:

- Spring WebSocket/STOMP ho?c SSE
- push message m?i
- push read receipt update
- push presence update
- typing indicator
- push notification moi va unread-count update theo user

N?u th?m WebSocket, ph?i x? l? auth token, group authorization v? cleanup subscription ??ng chu?n.

## Permission/RBAC

B?t bu?c d?ng permission code, kh?ng hardcode role trong UI.

Permission ?? xu?t:

```text
GROUP_ROOM_VIEW
GROUP_MESSAGE_VIEW
GROUP_MESSAGE_SEND
GROUP_MESSAGE_EDIT_OWN
GROUP_MESSAGE_DELETE_OWN
GROUP_MESSAGE_MODERATE
GROUP_MESSAGE_READ_RECEIPT_VIEW
GROUP_MEMBER_VIEW
GROUP_MEMBER_DETAIL_VIEW
GROUP_MEMBER_INVITE
GROUP_MEMBER_REMOVE
GROUP_MEMBER_PROMOTE
GROUP_TASK_VIEW
GROUP_TASK_CREATE
GROUP_TASK_UPDATE
GROUP_TASK_DELETE
GROUP_SUBMISSION_VIEW
GROUP_SUBMISSION_CREATE
GROUP_SUBMISSION_REVIEW
GROUP_FILE_VIEW
GROUP_FILE_UPLOAD
GROUP_ACTIVITY_VIEW
ADMIN_GROUP_ROOM_VIEW_ALL
```

Rule:

- Admin c? th? xem to?n b? group, messages, read receipts, activity log va notification audit/system event.
- Mentor owner qu?n l? group m?nh.
- Leader c? quy?n theo room settings.
- Member ch? thao t?c trong quy?n ???c c?p.
- Student kh?ng ph?i member th? kh?ng ???c xem group.
- FE ?n menu/button n?u thi?u quy?n, BE v?n ph?i enforce b?ng @PreAuthorize v? data scope.
- Notification phai duoc filter theo recipient va permission; khong gui/thong bao resource ma user khong duoc xem.

## Frontend y?u c?u

Trang ch?nh:

```text
/groups/:groupId
/groups/:groupId/tasks
```

Component ?? xu?t:

```text
GroupWorkspacePage
GroupWorkspaceHeader
GroupWorkspaceTabs
GroupChatPanel
GroupMessageList
GroupMessageBubble
GroupReadReceipts
GroupPresenceAvatar
GroupMemberDetailPopover
GroupMembersPanel
GroupTasksPanel
GroupSubmissionsPanel
GroupAnnouncementsPanel
GroupFilesPanel
GroupActivityPanel
NotificationDropdown
NotificationCenterPage
```

Ph?i reuse:

- `PageContainer`
- `PageHeader`
- `Card`
- `Button`
- `Badge`
- `Can`
- `PermissionCode`
- `theme.config.ts`
- `ui.config.ts`
- `layout.config.ts`

Kh?ng hardcode style l? t? n?u ?? c? token/component chung.

### UI read receipts

G?i ? layout:

- Message c?a m?nh n?m b?n ph?i, message ng??i kh?c b?n tr?i.
- Read receipts n?m g?c d??i ph?i c?a message cu?i c?ng ?? ??c theo c?m.
- Avatar read receipt k?ch th??c 16-20px.
- Stack avatar ch?ng nh?, c? border theo surface ?? d? nh?n.
- Click avatar m? detail popover g?n avatar; mobile d?ng bottom sheet/drawer.
- Tooltip text: `?? xem b?i Nguy?n Minh Anh l?c 08:47`.

### UI online status

- Avatar online c? dot xanh l? 8-10px ? g?c d??i ph?i.
- Dot c? border c?ng m?u surface ?? kh?ng b? d?nh v?o avatar.
- N?u offline, kh?ng hi?n th? dot ho?c dot x?m nh?.
- Member rail/panel hi?n th? s? online: `3 online`.

## UX c?n c?

- Empty state khi ch?a c? message/task/submission.
- Loading skeleton khi m? group.
- Error state n?u user kh?ng c? quy?n ho?c group kh?ng t?n t?i.
- Disable input n?u room setting kh?ng cho user nh?n.
- Khi user g?i message th?nh c?ng, mark b?n th?n ?? ??c message ??.
- Khi user m? room, mark read batch cho c?c message ?ang nh?n th?y.
- Khi chuy?n tab, kh?ng m?t draft message.
- N?u API read receipt l?i, kh?ng l?m m?t message; ch? log warning ho?c hi?n th? nh?.

## Performance

- Ph?n trang messages theo cursor: `before`, `after`, `limit`.
- Kh?ng load to?n b? l?ch s? chat m?t l?n.
- Read receipts ch? tr? danh s?ch compact cho message m?i/g?n nh?t.
- V?i message c?, ch? tr? count, khi click m?i g?i detail.
- Presence polling kh?ng qu? d?y; 30 gi?y l? ?? cho MVP.
- Tr?nh N+1 query khi l?y messages + sender + read receipts.
- Index ??y ?? theo `group_id`, `message_id`, `user_id`, `created_at`, `read_at`.

## Security v? privacy

- Kh?ng expose email/s? ?i?n tho?i member n?u user kh?ng c? quy?n xem detail.
- Student kh?ng ???c xem th?ng tin ngo?i group c?a m?nh.
- Read receipt ch? th?y trong group m? user c? quy?n ??c message.
- Admin xem ??y ?? h?n nh?ng v?n c?n audit.
- Message/file/submission download ph?i ki?m tra group membership/permission.
- Kh?ng tin d? li?u groupId/memberId t? FE; backend ph?i verify scope.

## Kh?ng ???c l?m

- Kh?ng hardcode online/offline status.
- Kh?ng render read receipts b?ng d? li?u demo.
- Kh?ng ?? Student xem group kh?ng thu?c m?nh.
- Kh?ng ch? ?n button FE m? b? authorization backend.
- Kh?ng load to?n b? message/read receipt kh?ng ph?n trang.
- Kh?ng d?ng role prefix trong URL nh? `/mentor/groups/:id`.
- Kh?ng t?o UI qu? to/landing page; ??y l? workspace l?m vi?c h?ng ng?y.

## Verification b?t bu?c

Backend:

```powershell
cd BE
.\gradlew.bat test
```

Frontend:

```powershell
cd FE
npm run lint
npm run build
```

Ki?m tra th? c?ng:

1. Admin m? ???c m?i group v? xem read receipts/activity ??y ??.
2. Mentor owner m? group m?nh, th?y members online, g?i message, t?o task.
3. Student member m? group m?nh, g?i message n?u setting cho ph?p.
4. Student kh?ng thu?c group truy c?p `/groups/:groupId` ph?i b? ch?n.
5. Message ?? xem hi?n th? avatar nh? ??ng ng??i ??c.
6. Click avatar read receipt m? ??ng member detail.
7. Member online hi?n th? dot xanh; offline kh?ng hi?n th? sai.
8. Refresh page kh?ng m?t d? li?u th?t.
9. Dark theme v?n ??c t?t v? kh?ng c? card tr?ng l?i.
10. Kh?ng c?n d? li?u demo trong group room.
11. Notification badge/dropdown cap nhat realtime hoac polling, khong can reload.
12. Mark read/read all cap nhat unread count ngay.
13. Notification deep link khong cho user vao resource ngoai scope permission.

## K?t qu? c?n b?n giao

Agent ph?i tr? v?:

1. Rule/task ?? ??c.
2. Skill query ?? ch?y v? skill ?? d?ng/c?i n?u c?.
3. Database migration/schema ?? th?m ho?c k? ho?ch n?u ch?a th? migrate.
4. API backend ?? th?m/s?a.
5. Component frontend ?? th?m/s?a.
6. C?ch x? l? read receipts.
7. C?ch x? l? online presence.
8. C?ch x? l? member detail popover/drawer.
9. Permission/RBAC ?? enforce ? FE v? BE.
10. K?t qu? test/build.
12. Commit hash v? tr?ng th?i push GitHub cho task 19.
