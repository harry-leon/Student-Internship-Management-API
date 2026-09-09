# Role Based Dashboard

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "react dashboard role based access frontend testing"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ React dashboard, role-based UI hoặc frontend testing.

## Mục tiêu

Dashboard hiện tại đã có nội dung khác nhau theo role, nhưng vẫn nên tách rõ dữ liệu và hành động cho từng vai trò. Một dashboard tốt không chỉ hiển thị số liệu, mà phải trả lời câu hỏi: hôm nay người dùng cần làm gì tiếp theo?

## Nguyên tắc thiết kế dashboard

- Admin cần nhìn tổng quan và rủi ro hệ thống.
- Mentor cần thấy việc cần review/chấm điểm.
- Student cần thấy deadline và trạng thái cá nhân.
- Không hiển thị action mà role không được phép làm.
- Không dùng cùng một KPI cho mọi role nếu ý nghĩa khác nhau.

## Dashboard Admin

### KPI nên có

- Tổng sinh viên trong phase active.
- Số sinh viên chưa phân công.
- Số mentor đang hoạt động.
- Mentor gần quá tải.
- Assignment đang `PENDING`, `IN_PROGRESS`, `COMPLETED`.
- Báo cáo tuần chưa nộp.
- Báo cáo tuần chờ Mentor review.
- Round đang active.
- Điểm chưa công bố.

### Widget nên có

1. `Pending Applications`
   - Danh sách đơn đăng ký đang chờ duyệt.
   - Action: xem chi tiết, duyệt, từ chối.

2. `Mentor Workload`
   - Top mentor gần quá tải.
   - Hiển thị `activeStudents / maxCapacity`.

3. `Missing Weekly Reports`
   - Sinh viên chưa nộp báo cáo tuần hiện tại.

4. `Active Assessment Rounds`
   - Round đang mở, tỷ lệ chấm, deadline.

5. `Recent System Activity`
   - User mới, assignment mới, report mới, điểm mới.

## Dashboard Mentor

### KPI nên có

- Số sinh viên đang phụ trách.
- Report chờ review.
- Round cần chấm.
- Sinh viên có report trễ.
- Assignment đang active.

### Widget nên có

1. `My Students`
   - Danh sách sinh viên thuộc mentor.
   - Link sang assignment detail.

2. `Reports To Review`
   - Báo cáo tuần đã nộp nhưng chưa review.
   - Action: nhận xét.

3. `Grading Queue`
   - Sinh viên cần chấm trong round active.
   - Action: mở form chấm.

4. `Risk Students`
   - Sinh viên trễ report, điểm thấp, chưa có tiến độ.

Mentor không nên thấy:

- Users management.
- Tạo phase.
- Tạo criteria.
- Phân quyền account.
- Quản lý toàn bộ mentor.

## Dashboard Student

### KPI nên có

- Phase hiện tại.
- Mentor phụ trách.
- Assignment status.
- Báo cáo tuần hiện tại.
- Deadline gần nhất.
- Điểm đã công bố.

### Widget nên có

1. `My Internship`
   - Công ty, mentor, phase, trạng thái.

2. `Weekly Report Status`
   - Tuần hiện tại đã nộp chưa.
   - Action: tạo/nộp report.

3. `Upcoming Deadlines`
   - Hạn nộp report, hạn round đánh giá.

4. `My Assessment Results`
   - Điểm đã công bố theo round.

Student không nên thấy:

- Danh sách tất cả users.
- Danh sách tất cả students.
- Tạo assignment.
- Cấu hình phase.
- Tạo criteria.
- Update assignment status.

## API dashboard đề xuất

Thay vì FE gọi nhiều API và tự tính, nên tạo API summary theo role.

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/api/dashboard/admin` | Admin | Summary cho Admin |
| GET | `/api/dashboard/mentor` | Mentor | Summary cho Mentor |
| GET | `/api/dashboard/student` | Student | Summary cho Student |
| GET | `/api/dashboard/me` | Admin, Mentor, Student | Backend tự trả dashboard theo role |

Khuyến nghị dùng `/api/dashboard/me` để FE đơn giản hơn.

## Response mẫu

```json
{
  "role": "MENTOR",
  "kpis": {
    "activeStudents": 12,
    "reportsToReview": 5,
    "gradingQueue": 8,
    "lateReports": 2
  },
  "items": {
    "students": [],
    "reportsToReview": [],
    "activeRounds": []
  }
}
```

## Frontend implementation

Tạo cấu trúc component:

```text
src/views/DashboardView.tsx
src/components/dashboard/AdminDashboard.tsx
src/components/dashboard/MentorDashboard.tsx
src/components/dashboard/StudentDashboard.tsx
src/api/dashboardService.ts
```

Trong `DashboardView`:

```tsx
if (role === 'Admin') return <AdminDashboard />;
if (role === 'Mentor') return <MentorDashboard />;
return <StudentDashboard />;
```

Không nên nhồi toàn bộ điều kiện role vào một file lớn. Dashboard thường phát triển nhanh, tách component sớm sẽ dễ bảo trì.

## UI density

Dashboard nên gọn:

- KPI card nhỏ, cùng chiều cao.
- Widget dùng table/list compact.
- Không dùng hero quá lớn trong dashboard.
- Action chính đặt gần dữ liệu liên quan.
- Tránh để Student thấy các số liệu tổng hệ thống không có ý nghĩa.

## Test cần có

- Admin login thấy Admin dashboard.
- Mentor login không thấy action Admin.
- Student login không thấy users/students management.
- Direct URL tới trang không có quyền phải bị chặn FE và backend.
- Dashboard API `/me` trả đúng role.

## Rủi ro

- Nếu chỉ ẩn bằng FE mà backend không chặn, vẫn có thể gọi API bằng Postman.
- Nếu dashboard dùng API tổng hợp quá nhiều logic, cần cache hoặc query tối ưu.
- Nếu không có role-specific design, người dùng thấy nhiều menu nhưng không biết nên làm gì.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `05-role-based-dashboard`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-role-based-dashboard-da-sua>
git commit -m "feat: complete role based dashboard"
git push origin <branch-name>
```

Không gom thay đổi của task `06-notification-deadline-reminder` hoặc các task khác vào commit/push này.
