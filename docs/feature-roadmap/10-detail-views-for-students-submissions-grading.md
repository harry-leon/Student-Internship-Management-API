# Detail Views cho Student, Submission va Grading

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Truoc khi code, chay `npx skills find "react detail view spring boot api dto authorization testing"`.
- Neu co skill tot ve React detail view, API design hoac Spring authorization, cai va doc truoc khi code.

## Muc tieu

Them kha nang xem detail cho cac thong tin quan trong. Hien tai nhieu trang chi co list/table/card, thieu man xem chi tiet de tra loi cac cau hoi nghiep vu:

- Student nay la ai, dang thuc tap o dau, mentor nao phu trach?
- Bai nop nay cua ai, nop cho round nao, link GitHub/file ZIP nao?
- Ai da cham bai, diem tung criterion bao nhieu, nhan xet ra sao?
- Trang thai workflow hien tai la gi, khi nao nop, khi nao review, khi nao publish?

## Nguyen tac detail view

- Desktop uu tien side panel hoac modal rong de khong roi context list.
- Detail chi fetch khi user bam `View detail`, khong load detail cho toan bo row.
- Detail response phai di qua backend authorization, khong chi an nut tren FE.
- Detail co action lien quan: open GitHub, download ZIP, cham diem, publish, xem report.
- Khong expose field nhay cam hoac internal path.

## Detail can lam

### 1. Student detail

Endpoint de xuat:

```text
GET /api/students/{student_id}/detail
```

Co the reuse `StudentResponse` va them cac block rieng:

- Profile: tu `StudentResponse`.
- Current assignment: reuse `InternshipAssignmentResponse`.
- Latest submission: `StudentSubmissionResponse` neu task 07 da xong.
- Recent weekly reports: list gon tu `WeeklyReportResponse` hoac summary.
- Assessment summary: diem tong theo round tu `AssessmentGradingFormResponse`/summary projection.

Neu muon giam DTO, co the FE goi nhieu endpoint detail hien co theo tab. Tuy nhien voi student detail, mot endpoint aggregate gon se cho UX tot hon va tranh FE goi qua nhieu request. Backend nen query bang projection/join co kiem soat.

Role:

- Admin: xem tat ca.
- Mentor: xem student minh phu trach.
- Student: xem chinh minh.

### 2. Submission detail

Endpoint:

```text
GET /api/student-submissions/{id}
```

Noi dung:

- Student info: code, full name, email.
- Assignment info: company, mentor, phase, status.
- Round info neu co.
- Submission type: GitHub/ZIP.
- GitHub URL hoac file metadata.
- Note cua student.
- Submitted at, version.
- Action download/open link.

Role:

- Admin: xem tat ca.
- Mentor: xem submission cua assignment minh phu trach.
- Student: xem submission cua minh.

### 3. Grading/detail result

Endpoint nen reuse:

```text
GET /api/assessment_grading/results?assignmentId=1&roundId=2
GET /api/assessment_grading/forms?assignmentId=1&roundId=2
```

Neu endpoint hien co da tra `AssessmentGradingFormResponse`, khong tao DTO moi. Chi bo sung `latestSubmission` neu can lien ket bai nop.

Noi dung detail:

- Student + assignment + mentor.
- Round.
- Submission gan voi bai cham.
- Evaluated by: id/name.
- Criteria table: criterion, max score, weight, score, comment.
- Total score, weighted score, status.
- Submitted at, published at.

Role:

- Admin: xem tat ca va publish neu dung workflow.
- Mentor: xem/cham assignment cua minh neu chua published.
- Student: chi xem ket qua da published cua minh.

## API va DTO reuse

Nen reuse cac DTO hien co:

- `StudentResponse` cho profile block.
- `InternshipAssignmentResponse` cho assignment block.
- `WeeklyReportResponse` cho report detail/list ngan.
- `AssessmentGradingFormResponse` cho grading detail.
- `AssessmentResultResponse` cho result row neu can hien bang chi tiet tung criterion.
- `StudentSubmissionResponse` tu task 07 cho bai nop.

Chi tao DTO aggregate neu that su can gom nhieu block cho mot detail page:

```java
public class StudentDetailResponse {
    private StudentResponse student;
    private InternshipAssignmentResponse currentAssignment;
    private StudentSubmissionResponse latestSubmission;
    private List<WeeklyReportResponse> recentReports;
    private List<AssessmentGradingFormResponse> gradingSummaries;
}
```

Khong tao `StudentDetailDto`, `StudentInfoDto`, `StudentFullDto` rieng le neu field trung lap.

## Frontend UI

Them pattern chung `DetailDrawer` hoac `DetailModal`:

- Header compact: ten doi tuong, status badge, close button.
- Tabs: Overview, Submission, Grading, Reports.
- Body co grid 2 cot tren desktop, 1 cot mobile.
- Action bar sticky duoi modal neu co action chinh.

Ap dung vao:

- `StudentsView.tsx`: action `View detail`.
- `AssignmentsView.tsx`: detail assignment + submission status.
- `AssessmentResultsView.tsx`: detail diem/cham bai.
- `WeeklyReportsView.tsx`: detail report neu chua co.
- `SubmissionsView.tsx`: detail bai nop sau task 07.

## Performance

- List page khong load detail nested.
- Detail cache theo id trong state neu user mo lai nhanh.
- Backend detail endpoint dung query co join/projection ro rang.
- Khong tra file content trong detail; file download rieng.

## Test can co

Backend:

- Mentor xem detail student khong duoc phan cong bi `403`.
- Student xem detail cua student khac bi `403`.
- Admin xem detail thanh cong.
- Detail grading chua published khong cho Student xem.
- Detail submission khong expose `storedFileName`.

Frontend:

- Bam `View detail` mo drawer/modal.
- Loading/detail/error state hien dung.
- GitHub action mo link moi.
- ZIP action goi download endpoint.
- Mobile khong overlap.

## Thu tu thuc hien

1. Chuan hoa detail component/drawer neu can.
2. Them student detail backend + FE.
3. Them submission detail sau khi task 07 xong.
4. Gan latest submission vao grading form/detail.
5. Them action detail vao cac table.
6. Test role authorization va build.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `10-detail-views-for-students-submissions-grading`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-detail-view-da-sua>
git commit -m "feat: add detail views for students submissions and grading"
git push origin <branch-name>
```

Khong gom UI density hoac response optimization neu task do chua hoan thanh/push rieng.