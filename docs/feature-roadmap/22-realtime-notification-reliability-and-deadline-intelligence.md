# Task 22 - Realtime Notification Reliability And Deadline Intelligence

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/frontend-ui-configuration-rule.md`
- Truoc khi code:

```powershell
npx skills find "spring boot websocket notification realtime unread count deadline scheduler react"
npx skills find "notification center polling websocket sse react query"
```

- Uu tien skill:
  - `spring-boot-crud-patterns`
  - `spring-boot-test-patterns`
  - `database-schema-designer`

## Muc tieu

Notification hien tai can duoc nang cap thanh channel nghiep vu that, khong chi la badge demo.

Can dat cac yeu cau:

1. Cap nhat realtime hoac near-realtime.
2. Unread count dung theo user hien tai.
3. Co mark read, mark all read, archive/delete neu can.
4. Co deep link toi dung ngu canh.
5. Co deadline reminder that, khong chung chung.
6. Khong spam notification chat thuong, chi push cho su kien quan trong.

## Su kien can tao notification

- task moi duoc giao
- task sap den han hoac qua han
- submission moi / resubmit
- review/score/feedback duoc publish
- mention @username hoac @all neu co quyen
- announcement moi trong group
- member duoc them / kick / promote / demote
- permission thay doi
- system warning / maintenance / error quan trong

## Pham vi

### 1. Notification API

Can co:

```http
GET    /api/me/notifications?status=&type=&page=&size=
GET    /api/me/notifications/unread-count
PATCH  /api/me/notifications/{notificationId}/read
PATCH  /api/me/notifications/read-all
DELETE /api/me/notifications/{notificationId}
```

Neu realtime:

```text
/topic/users/{userId}/notifications
/topic/users/{userId}/notification-count
```

Neu chua co websocket/sse, dung polling that nhe va co cache/refetch hop ly.

### 2. Deadline intelligence

Thong bao deadline phai:

- nhan biet task sap het han
- nhan biet task qua han
- uu tien theo role va scope
- co thoi diem gui ro rang

Khong dung scheduler chung chung khong co context.

### 3. Notification center

UI nen co:

- dropdown unread first
- trang center neu danh sach dai
- filter theo type/status/date/group
- mark read nhanh
- deep link
- empty/loading/error state

### 4. Data model

Uu tien tach event goc va recipient state:

- `notifications`
- `notification_recipients`

Khong luu read state vao event goc neu mot notification gui cho nhieu user.

## Test

- unread count cap nhat sau mutation
- read/read all cap nhat ngay
- deep link khong vuot scope permission
- deadline reminder gui dung event
- khong spam notification chat thuong

## Acceptance criteria

- Notification badge/center cap nhat realtime hoac near-realtime.
- Deadline reminder theo du lieu that.
- Notification state dung scope role va user.

