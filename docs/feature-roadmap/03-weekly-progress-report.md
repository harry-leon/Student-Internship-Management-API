# Weekly Progress Report

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "weekly report file upload workflow react testing"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ file upload, report workflow hoặc frontend testing.

## Mục tiêu

Báo cáo tuần là phần rất quan trọng trong quản lý thực tập. Nó giúp Mentor biết sinh viên đang làm gì trước khi đến vòng chấm điểm cuối. Nếu chỉ có assignment và assessment result, hệ thống sẽ thiếu dữ liệu tiến độ, thiếu căn cứ nhận xét và khó phát hiện sinh viên đang gặp vấn đề.

## Luồng nghiệp vụ

1. Mỗi assignment active có thể tạo nhiều weekly report.
2. Student nộp báo cáo theo tuần.
3. Mentor xem báo cáo của sinh viên mình phụ trách.
4. Mentor nhận xét và đánh dấu trạng thái review.
5. Admin xem tổng hợp tỷ lệ nộp báo cáo theo phase.

## Database đề xuất

Tạo bảng `weekly_progress_reports`.

```sql
CREATE TABLE weekly_progress_reports (
    report_id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    week_number INTEGER NOT NULL,
    report_title VARCHAR(150),
    completed_tasks TEXT NOT NULL,
    difficulties TEXT,
    next_plan TEXT,
    working_hours DECIMAL(5,2),
    attachment_url VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP NULL,
    reviewed_by INTEGER NULL,
    reviewed_at TIMESTAMP NULL,
    mentor_comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_assignment FOREIGN KEY (assignment_id) REFERENCES internship_assignments(assignment_id),
    CONSTRAINT fk_report_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    CONSTRAINT uq_assignment_week UNIQUE (assignment_id, week_number)
);
```

Enum:

```java
public enum WeeklyReportStatus {
    DRAFT,
    SUBMITTED,
    REVIEWED,
    NEEDS_REVISION,
    LATE
}
```

## Business rules

- Student chỉ tạo báo cáo cho assignment của mình.
- Student chỉ sửa report khi `DRAFT` hoặc `NEEDS_REVISION`.
- Student không sửa report đã `REVIEWED`.
- Mentor chỉ review report của sinh viên được phân công cho mình.
- Admin có quyền xem toàn bộ report.
- Mỗi assignment chỉ có một report cho mỗi `weekNumber`.
- `weekNumber` phải lớn hơn 0.
- `workingHours` không âm.

## API đề xuất

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/api/weekly_reports` | Admin, Mentor, Student | Danh sách report theo quyền |
| GET | `/api/weekly_reports/{report_id}` | Admin, Mentor, Student | Chi tiết report |
| POST | `/api/weekly_reports` | Student | Tạo draft report |
| PUT | `/api/weekly_reports/{report_id}` | Student | Cập nhật draft |
| POST | `/api/weekly_reports/{report_id}/submit` | Student | Nộp báo cáo |
| POST | `/api/weekly_reports/{report_id}/review` | Mentor | Mentor nhận xét |
| DELETE | `/api/weekly_reports/{report_id}` | Student, Admin | Xóa draft |

Query params nên hỗ trợ:

```text
assignmentId
phaseId
studentId
mentorId
status
weekNumber
page
size
sortBy
sortDirection
```

## DTO request

`WeeklyReportCreateRequest`:

```java
@NotNull
private Integer assignmentId;

@NotNull
@Min(1)
private Integer weekNumber;

@NotBlank
private String completedTasks;

private String difficulties;

private String nextPlan;

@DecimalMin("0.0")
private BigDecimal workingHours;
```

`WeeklyReportReviewRequest`:

```java
@NotBlank
private String mentorComment;

@NotNull
private WeeklyReportStatus status; // REVIEWED hoặc NEEDS_REVISION
```

## Backend service

Các method chính:

```java
Page<WeeklyReportResponse> getReports(WeeklyReportQuery query, Pageable pageable, UserPrincipal currentUser);
WeeklyReportResponse createReport(WeeklyReportCreateRequest request, UserPrincipal currentUser);
WeeklyReportResponse updateReport(Integer reportId, WeeklyReportUpdateRequest request, UserPrincipal currentUser);
WeeklyReportResponse submitReport(Integer reportId, UserPrincipal currentUser);
WeeklyReportResponse reviewReport(Integer reportId, WeeklyReportReviewRequest request, UserPrincipal currentUser);
```

Phần khó nhất là filter dữ liệu theo role:

- Admin: không filter theo user hiện tại.
- Mentor: chỉ report của assignment có `mentor_id = currentUser.userId`.
- Student: chỉ report của assignment có `student_id = currentUser.userId`.

## Frontend cần làm

Tạo `WeeklyReportsView.tsx`.

Student:

- Xem danh sách report theo tuần.
- Button `Tạo báo cáo tuần`.
- Form nộp báo cáo.
- Badge trạng thái: Draft, Submitted, Reviewed, Needs Revision.
- Hiển thị comment của Mentor.

Mentor:

- Xem report chờ review.
- Filter theo sinh viên, tuần, trạng thái.
- Mở detail report.
- Nhập nhận xét.
- Chọn `Reviewed` hoặc `Needs Revision`.

Admin:

- Xem tổng hợp report toàn phase.
- Filter theo phase, mentor, status.
- Xem ai chưa nộp.

## UI gợi ý

Không nên làm report thành bảng quá rộng. Nên dùng layout:

- Bên trái: list report theo tuần.
- Bên phải hoặc modal: chi tiết report.
- Trên đầu: KPI nhỏ `Đã nộp`, `Chờ review`, `Cần sửa`, `Trễ hạn`.

## Notification liên quan

Nên tạo notification khi:

- Student submit report.
- Mentor review report.
- Report bị `NEEDS_REVISION`.
- Deadline tuần sắp hết.

## Test cần có

- Student tạo report cho assignment của mình thành công.
- Student tạo report cho assignment người khác bị `403`.
- Mentor review report sinh viên mình phụ trách thành công.
- Mentor review report sinh viên không thuộc mình bị `403`.
- Tạo trùng `assignmentId + weekNumber` bị `DUPLICATE_RESOURCE`.
- Student submit report rồi không sửa được nếu đã reviewed.

## Rủi ro

- Nếu không có weekly report, Mentor chỉ chấm điểm cuối kỳ dựa trên cảm tính.
- Nếu không enforce unique tuần, sinh viên có thể nộp nhiều report cho cùng một tuần.
- Nếu không phân quyền theo assignment, Mentor có thể xem report của sinh viên khác.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `03-weekly-progress-report`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-weekly-progress-report-da-sua>
git commit -m "feat: complete weekly progress report"
git push origin <branch-name>
```

Không gom thay đổi của task `04-rubric-grading-workflow` hoặc các task khác vào commit/push này.
