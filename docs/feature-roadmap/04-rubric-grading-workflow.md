# Rubric Grading Workflow

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "rubric grading workflow api design react form validation"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ domain workflow, API design, form validation hoặc frontend testing.

## Mục tiêu

Hệ thống hiện có `EvaluationCriteria`, `AssessmentRounds`, `RoundCriteria` và `AssessmentResults`, nhưng trải nghiệm chấm điểm nên được nâng lên thành workflow rõ ràng cho Mentor. Mentor không nên nhập result rời rạc từng record. Thay vào đó, họ cần một form chấm theo round, hiển thị toàn bộ criteria, max score, weight và tự tính tổng điểm.

## Luồng nghiệp vụ

1. Admin tạo criteria chung.
2. Admin tạo assessment round.
3. Admin gắn criteria vào round qua `RoundCriteria` và khai báo weight.
4. Mentor mở round active.
5. Mentor chọn sinh viên/assignment được phân công.
6. Form hiển thị tất cả criteria của round.
7. Mentor nhập score/comment từng criterion.
8. Hệ thống lưu `AssessmentResult`.
9. Hệ thống tính tổng điểm weighted score.
10. Student xem kết quả khi được công bố.

## Database cần kiểm tra

Các bảng hiện có trong SRS đã đủ cơ bản:

- `evaluation_criteria`
- `assessment_rounds`
- `round_criteria`
- `assessment_results`

Nên bổ sung trạng thái công bố điểm ở round hoặc result.

Phương án 1: thêm vào `assessment_rounds`.

```sql
ALTER TABLE assessment_rounds
ADD COLUMN is_published BOOLEAN NOT NULL DEFAULT FALSE;
```

Phương án 2: thêm bảng tổng hợp `assessment_submissions`.

```sql
CREATE TABLE assessment_submissions (
    submission_id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    round_id INTEGER NOT NULL,
    evaluated_by INTEGER NOT NULL,
    total_score DECIMAL(5,2),
    weighted_score DECIMAL(5,2),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP NULL,
    published_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_assignment_round UNIQUE (assignment_id, round_id)
);
```

Nếu muốn nhanh, dùng phương án 1. Nếu muốn nghiệp vụ tốt hơn, dùng phương án 2.

## Enum đề xuất

```java
public enum AssessmentSubmissionStatus {
    DRAFT,
    SUBMITTED,
    PUBLISHED,
    RETURNED
}
```

## API đề xuất

API hiện tại có `/api/assessment_results`. Nên bổ sung endpoint dạng workflow:

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/api/assessment_rounds/{round_id}/criteria` | Admin, Mentor, Student | Lấy criteria của round |
| GET | `/api/assessment_grading/forms` | Mentor | Lấy form chấm theo assignment + round |
| POST | `/api/assessment_grading/draft` | Mentor | Lưu nháp điểm |
| POST | `/api/assessment_grading/submit` | Mentor | Submit điểm |
| POST | `/api/assessment_grading/{submission_id}/publish` | Admin | Công bố điểm |
| GET | `/api/assessment_grading/results` | Admin, Mentor, Student | Xem kết quả theo quyền |

Ví dụ query lấy form:

```text
GET /api/assessment_grading/forms?assignmentId=1&roundId=2
```

Response nên trả:

```json
{
  "assignmentId": 1,
  "roundId": 2,
  "studentName": "Nguyen Van A",
  "mentorName": "Tran Van B",
  "criteria": [
    {
      "criterionId": 1,
      "criterionName": "Technical Skill",
      "description": "Evaluate technical delivery",
      "maxScore": 10,
      "weight": 30,
      "score": 8.5,
      "comment": "Good implementation"
    }
  ],
  "totalScore": 8.5,
  "weightedScore": 2.55,
  "status": "DRAFT"
}
```

## Request submit điểm

```json
{
  "assignmentId": 1,
  "roundId": 2,
  "items": [
    {
      "criterionId": 1,
      "score": 8.5,
      "comments": "Good implementation"
    }
  ]
}
```

Validation:

- `score >= 0`
- `score <= criterion.maxScore`
- mọi criterion bắt buộc của round phải được chấm khi submit
- tổng weight trong round nên bằng 100 hoặc cảnh báo nếu không bằng 100

## Backend service

Nên tạo service riêng:

```java
AssessmentGradingFormResponse getGradingForm(Integer assignmentId, Integer roundId, UserPrincipal currentUser);
AssessmentGradingFormResponse saveDraft(AssessmentGradingRequest request, UserPrincipal currentUser);
AssessmentGradingFormResponse submit(AssessmentGradingRequest request, UserPrincipal currentUser);
AssessmentResultSummaryResponse getResultSummary(Integer assignmentId, Integer roundId, UserPrincipal currentUser);
```

Không nên để FE tự tính điểm làm nguồn dữ liệu chính. FE có thể hiển thị preview, nhưng backend phải tính lại để đảm bảo chính xác.

## Quyền truy cập

Admin:

- Xem toàn bộ điểm.
- Công bố điểm.
- Không nhất thiết trực tiếp chấm nếu nghiệp vụ yêu cầu Mentor chấm.

Mentor:

- Chỉ chấm assignment của sinh viên mình phụ trách.
- Chỉ sửa điểm khi submission chưa published.

Student:

- Chỉ xem điểm của mình.
- Chỉ xem khi round hoặc submission đã published.

## Frontend cần làm

Tạo `GradingFormModal.tsx` hoặc route riêng `AssessmentGradingView.tsx`.

Mentor view:

- Danh sách round active.
- Danh sách sinh viên cần chấm.
- Badge: Chưa chấm, Đang nháp, Đã submit.
- Form chấm theo criteria.
- Tự hiển thị tổng điểm preview.
- Button `Lưu nháp`, `Nộp điểm`.

Admin view:

- Bảng tổng hợp điểm theo phase/round.
- Button `Công bố điểm`.
- Export điểm.

Student view:

- Chỉ hiển thị kết quả đã công bố.
- Có breakdown theo criterion.
- Có comment mentor.

## Test cần có

- Mentor lấy form chấm cho assignment của mình thành công.
- Mentor lấy form của assignment mentor khác bị `403`.
- Submit thiếu criterion bị lỗi validation.
- Score vượt maxScore bị lỗi.
- Submit thành công tạo hoặc update assessment_results.
- Student không xem được điểm chưa published.
- Admin publish thành công.

## Rủi ro

- Nếu lưu result từng dòng mà không có trạng thái submission, khó biết round đã chấm xong hay chưa.
- Nếu FE tự tính điểm và backend không tính lại, dễ sai điểm khi có người sửa request.
- Nếu không khóa chỉnh sửa sau published, điểm đã công bố có thể thay đổi mà không có audit.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `04-rubric-grading-workflow`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-rubric-grading-workflow-da-sua>
git commit -m "feat: complete rubric grading workflow"
git push origin <branch-name>
```

Không gom thay đổi của task `05-role-based-dashboard` hoặc các task khác vào commit/push này.
