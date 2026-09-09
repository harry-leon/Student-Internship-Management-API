# Notification And Deadline Reminder

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "spring scheduler notification reminder react notification bell"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ scheduling/background job, notification workflow hoặc frontend notification UI.

## Mục tiêu

Hệ thống thực tập có nhiều deadline: đăng ký thực tập, duyệt đơn, nộp báo cáo tuần, review của Mentor, chấm điểm round, công bố kết quả. Nếu không có notification, người dùng phải tự nhớ hoặc Admin phải nhắc thủ công. Tính năng Notification giúp hệ thống vận hành chủ động hơn.

## Loại notification cần có

| Sự kiện | Người nhận |
| --- | --- |
| Student submit application | Admin |
| Admin approve/reject application | Student |
| Admin tạo assignment | Student, Mentor |
| Student submit weekly report | Mentor |
| Mentor review weekly report | Student |
| Weekly report sắp đến hạn | Student |
| Weekly report quá hạn | Student, Mentor, Admin |
| Assessment round bắt đầu | Mentor |
| Mentor submit score | Admin |
| Admin publish result | Student |

## Database đề xuất

Tạo bảng `notifications`.

```sql
CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    recipient_id INTEGER NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    target_type VARCHAR(50),
    target_id INTEGER,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES users(user_id)
);
```

Nên index:

```sql
CREATE INDEX idx_notifications_recipient_read
ON notifications(recipient_id, is_read);

CREATE INDEX idx_notifications_created_at
ON notifications(created_at);
```

## Enum type đề xuất

```java
public enum NotificationType {
    APPLICATION_SUBMITTED,
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    ASSIGNMENT_CREATED,
    WEEKLY_REPORT_SUBMITTED,
    WEEKLY_REPORT_REVIEWED,
    WEEKLY_REPORT_DUE_SOON,
    WEEKLY_REPORT_OVERDUE,
    ASSESSMENT_ROUND_STARTED,
    ASSESSMENT_SCORE_SUBMITTED,
    ASSESSMENT_RESULT_PUBLISHED
}
```

## API đề xuất

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/api/notifications` | Admin, Mentor, Student | Lấy notification của user hiện tại |
| GET | `/api/notifications/unread-count` | Admin, Mentor, Student | Đếm chưa đọc |
| PUT | `/api/notifications/{id}/read` | Admin, Mentor, Student | Đánh dấu đã đọc |
| PUT | `/api/notifications/read-all` | Admin, Mentor, Student | Đánh dấu tất cả đã đọc |
| DELETE | `/api/notifications/{id}` | Admin, Mentor, Student | Xóa notification của mình |

Không nên cho user đọc notification của người khác.

## Notification service

Tạo service:

```java
void notifyUser(Integer recipientId, NotificationType type, String title, String message, String targetType, Integer targetId);
void notifyUsers(Collection<Integer> recipientIds, NotificationType type, String title, String message, String targetType, Integer targetId);
Page<NotificationResponse> getMyNotifications(UserPrincipal currentUser, Pageable pageable);
long countUnread(UserPrincipal currentUser);
void markAsRead(Integer notificationId, UserPrincipal currentUser);
void markAllAsRead(UserPrincipal currentUser);
```

Các service nghiệp vụ khác sẽ gọi `NotificationService` sau khi transaction chính thành công.

Ví dụ:

- `InternshipApplicationService.submit()` gọi notify Admin.
- `WeeklyReportService.submit()` gọi notify Mentor.
- `AssessmentGradingService.publish()` gọi notify Student.

## Deadline reminder

Cần scheduled job để tạo reminder.

Trong Spring Boot:

```java
@EnableScheduling
@Configuration
public class SchedulingConfig {
}
```

Job:

```java
@Scheduled(cron = "0 0 8 * * *")
public void sendDailyDeadlineReminders() {
    // kiểm tra report sắp hết hạn, round sắp hết hạn
}
```

Logic reminder:

- Chạy mỗi ngày lúc 08:00.
- Tìm weekly report chưa submit cho tuần hiện tại.
- Nếu còn 1 ngày đến hạn: tạo notification `DUE_SOON`.
- Nếu quá hạn: tạo notification `OVERDUE`.
- Tránh tạo trùng notification cùng target trong cùng ngày.

Để tránh trùng, có thể thêm field:

```sql
ALTER TABLE notifications
ADD COLUMN dedupe_key VARCHAR(150);

CREATE UNIQUE INDEX uq_notification_dedupe
ON notifications(recipient_id, dedupe_key)
WHERE dedupe_key IS NOT NULL;
```

## Frontend cần làm

Component:

```text
src/components/NotificationBell.tsx
src/components/NotificationDropdown.tsx
src/api/notificationService.ts
```

Header:

- Thêm icon chuông cạnh search.
- Badge số unread.
- Click mở dropdown.
- Dropdown hiển thị 5-10 notification mới nhất.
- Có button `Đánh dấu tất cả đã đọc`.

Notification item:

- Title
- Message ngắn
- Thời gian
- Badge unread
- Click điều hướng tới target nếu có.

Ví dụ target mapping:

```ts
const targetRoutes = {
  APPLICATION: (id: number) => `/admin/applications/${id}`,
  ASSIGNMENT: (id: number) => `/admin/assignments?assignmentId=${id}`,
  WEEKLY_REPORT: (id: number) => `/admin/weekly-reports/${id}`,
  ASSESSMENT_RESULT: (id: number) => `/admin/assessment-results?resultId=${id}`,
};
```

## Real-time hay polling?

Giai đoạn đầu nên dùng polling đơn giản:

- Khi app load: gọi unread count.
- Mỗi 60 giây gọi lại unread count.
- Khi mở dropdown: gọi danh sách notification.

Sau này nếu cần realtime, dùng WebSocket hoặc Server-Sent Events.

## Test cần có

- User chỉ lấy notification của mình.
- Mark read notification của người khác bị `403`.
- Submit weekly report tạo notification cho Mentor.
- Review report tạo notification cho Student.
- Scheduled job không tạo duplicate reminder.
- Unread count giảm sau mark read.

## Rủi ro

- Nếu notification tạo trong transaction thất bại, có thể làm fail nghiệp vụ chính. Nên cân nhắc bắt lỗi notification hoặc dùng event async sau này.
- Nếu polling quá dày, hệ thống có thể bị nhiều request thừa.
- Nếu không có dedupe, reminder sẽ bị spam mỗi ngày hoặc mỗi lần job chạy.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `06-notification-deadline-reminder`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-notification-deadline-reminder-da-sua>
git commit -m "feat: complete notification deadline reminder"
git push origin <branch-name>
```

Không gom thay đổi của task khác vào commit/push này.
