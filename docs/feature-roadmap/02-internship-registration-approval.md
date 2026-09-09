# Internship Registration And Approval

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "workflow approval state machine react spring boot"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ workflow/state-machine, approval flow hoặc frontend flow testing.

## Mục tiêu

Hiện tại Admin tạo phân công thực tập trực tiếp. Trong quy trình thực tế, sinh viên thường đăng ký hoặc khai báo thông tin thực tập trước, sau đó Admin/Coordinator kiểm tra và phê duyệt. Tính năng này biến hệ thống từ CRUD assignment thành workflow có trạng thái rõ ràng.

## Luồng nghiệp vụ đề xuất

1. Student tạo đơn đăng ký thực tập.
2. Student nhập công ty, vị trí, người hướng dẫn tại công ty, đề tài, thời gian dự kiến.
3. Student submit đơn.
4. Admin xem danh sách đơn chờ duyệt.
5. Admin approve hoặc reject.
6. Khi approve, hệ thống tạo hoặc liên kết `InternshipAssignment`.
7. Mentor được phân công nhận thấy sinh viên trong dashboard.

## Database đề xuất

Tạo bảng `internship_applications`.

```sql
CREATE TABLE internship_applications (
    application_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    phase_id INTEGER NOT NULL,
    company_id INTEGER NULL,
    proposed_company_name VARCHAR(150),
    position_title VARCHAR(150),
    company_mentor_name VARCHAR(100),
    company_mentor_email VARCHAR(100),
    company_mentor_phone VARCHAR(20),
    project_topic VARCHAR(255),
    start_date DATE,
    end_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    rejection_reason TEXT,
    submitted_at TIMESTAMP NULL,
    reviewed_by INTEGER NULL,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_application_phase FOREIGN KEY (phase_id) REFERENCES internship_phases(phase_id),
    CONSTRAINT fk_application_company FOREIGN KEY (company_id) REFERENCES companies(company_id),
    CONSTRAINT fk_application_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);
```

Enum status:

```java
public enum InternshipApplicationStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    CANCELLED
}
```

## Business rules

- Student chỉ được tạo đơn cho chính mình.
- Student chỉ được sửa đơn khi status là `DRAFT` hoặc `REJECTED`.
- Student submit đơn khi đủ thông tin bắt buộc.
- Admin chỉ approve đơn `SUBMITTED`.
- Admin reject phải nhập lý do.
- Một student chỉ nên có một đơn active trong cùng một phase.
- Khi application được approve, không cho sửa nội dung application nữa.
- Nếu đã có assignment cùng `student_id + phase_id`, approve phải báo lỗi duplicate hoặc liên kết vào assignment hiện có.

## API đề xuất

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/api/internship_applications` | Admin, Mentor, Student | Danh sách đơn theo quyền |
| GET | `/api/internship_applications/{id}` | Admin, Mentor, Student | Chi tiết đơn |
| POST | `/api/internship_applications` | Student | Tạo đơn draft |
| PUT | `/api/internship_applications/{id}` | Student | Cập nhật draft |
| POST | `/api/internship_applications/{id}/submit` | Student | Gửi đơn |
| POST | `/api/internship_applications/{id}/approve` | Admin | Duyệt đơn |
| POST | `/api/internship_applications/{id}/reject` | Admin | Từ chối đơn |
| POST | `/api/internship_applications/{id}/cancel` | Student | Hủy đơn nếu chưa duyệt |

## DTO request

`InternshipApplicationCreateRequest`:

```java
@NotNull
private Integer phaseId;

private Integer companyId;

@Size(max = 150)
private String proposedCompanyName;

@Size(max = 150)
private String positionTitle;

@Size(max = 255)
private String projectTopic;
```

`InternshipApplicationReviewRequest`:

```java
private Integer mentorId;

private Integer companyId;

private String rejectionReason;
```

Khi approve, Admin có thể chọn mentor phụ trách. Nếu hệ thống đã có mentor từ form, vẫn nên cho Admin xác nhận lại.

## Backend service

Service nên tách rõ state transition:

```java
InternshipApplicationResponse createDraft(CreateRequest request, UserPrincipal currentUser);
InternshipApplicationResponse updateDraft(Integer id, UpdateRequest request, UserPrincipal currentUser);
InternshipApplicationResponse submit(Integer id, UserPrincipal currentUser);
InternshipApplicationResponse approve(Integer id, ApproveRequest request, UserPrincipal currentUser);
InternshipApplicationResponse reject(Integer id, RejectRequest request, UserPrincipal currentUser);
```

Không nên để controller tự check quá nhiều logic trạng thái. Controller chỉ nhận request, lấy user hiện tại, gọi service.

## Frontend cần làm

Tạo `InternshipApplicationsView.tsx`.

Admin view:

- Tabs: `Chờ duyệt`, `Đã duyệt`, `Từ chối`, `Tất cả`.
- Table: sinh viên, phase, công ty, vị trí, trạng thái, ngày gửi.
- Detail drawer/modal: xem đủ thông tin, chọn mentor, approve/reject.
- Action approve/reject chỉ hiển thị với Admin.

Student view:

- Card trạng thái đơn hiện tại.
- Button `Tạo đăng ký thực tập`.
- Form nhập công ty, vị trí, mentor doanh nghiệp, đề tài, thời gian.
- Button `Lưu nháp`, `Gửi duyệt`.
- Nếu bị reject, hiển thị lý do và cho sửa gửi lại.

Mentor view:

- Có thể không cần menu riêng.
- Nếu có, chỉ xem các application đã approve liên quan tới mình.

## Role menu đề xuất

- Admin: thấy `Applications`.
- Student: thấy `My Registration`.
- Mentor: không nhất thiết thấy, hoặc thấy read-only nếu cần.

## Tích hợp với assignment

Khi approve:

1. Kiểm tra application status là `SUBMITTED`.
2. Kiểm tra student/phase chưa có assignment.
3. Tạo assignment với `studentId`, `mentorId`, `phaseId`, `companyId`.
4. Set assignment status `PENDING` hoặc `IN_PROGRESS`.
5. Set application status `APPROVED`.
6. Ghi `reviewedBy`, `reviewedAt`.

## Test cần có

- Student tạo draft thành công.
- Student không tạo đơn cho student khác.
- Student submit thiếu thông tin bị `INVALID_INPUT_DATA`.
- Admin approve đơn và tạo assignment.
- Admin reject thiếu lý do bị lỗi.
- Mentor không approve được.
- Student không sửa đơn đã approved.

## Rủi ro

- Nếu tạo assignment ngay khi student submit, dữ liệu sẽ bị rác khi đơn bị từ chối.
- Nếu không có state transition rõ, dễ phát sinh trạng thái sai như approve đơn đã cancelled.
- Nếu không lock unique `student_id + phase_id`, một sinh viên có thể có nhiều đơn cùng phase.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `02-internship-registration-approval`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-registration-approval-da-sua>
git commit -m "feat: complete internship registration approval"
git push origin <branch-name>
```

Không gom thay đổi của task `03-weekly-progress-report` hoặc các task khác vào commit/push này.
