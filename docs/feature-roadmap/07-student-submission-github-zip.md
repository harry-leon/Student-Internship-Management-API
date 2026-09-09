# Student Submission GitHub Link va ZIP Upload

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Truoc khi code, chay `npx skills find "spring boot file upload validation workflow react form"`.
- Neu co skill phu hop va uy tin, cai bang `npx skills add <owner>/<repo>` roi doc `SKILL.md` cua skill do.
- Nen uu tien skill ve file upload security, REST API workflow, React form validation.

## Muc tieu

Them tinh nang de Student nop bai lam cho he thong cham diem. Student co the nop mot trong hai hinh thuc:

- GitHub URL: link repository, pull request, release hoac commit.
- ZIP file: file nen chua source code/bai lam.

Admin va Mentor co the xem danh sach bai nop, mo link GitHub neu bai la link, hoac download file ZIP neu bai la file. Mentor dung thong tin bai nop nay khi cham diem, Admin dung de theo doi tien do va audit.

## Huong di hop ly

Khong nen tron bai nop vao `WeeklyProgressReport` vi report tuan la noi mo ta tien do, con bai nop de cham diem la artifact gan voi `InternshipAssignment` va co the gan voi `AssessmentRound`. Nen tao thuc the rieng `student_submissions` de quan ly lifecycle, file metadata, GitHub URL, trang thai nop va nguoi nop.

Nen cho phep moi assignment + round co nhieu version bai nop, nhung chi mot version moi nhat duoc danh dau active. Cach nay giup Student nop lai khi Mentor yeu cau sua, va van giu lich su.

## Database de xuat

Tao bang `student_submissions`:

```sql
CREATE TABLE student_submissions (
    submission_id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    round_id INTEGER NULL,
    submitted_by INTEGER NOT NULL,
    submission_type VARCHAR(20) NOT NULL,
    github_url VARCHAR(500) NULL,
    original_file_name VARCHAR(255) NULL,
    stored_file_name VARCHAR(255) NULL,
    file_size_bytes BIGINT NULL,
    content_type VARCHAR(100) NULL,
    note TEXT NULL,
    version_no INTEGER NOT NULL DEFAULT 1,
    is_latest BOOLEAN NOT NULL DEFAULT TRUE,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_assignment FOREIGN KEY (assignment_id) REFERENCES internship_assignments(assignment_id),
    CONSTRAINT fk_submission_round FOREIGN KEY (round_id) REFERENCES assessment_rounds(round_id),
    CONSTRAINT fk_submission_user FOREIGN KEY (submitted_by) REFERENCES users(userid),
    CONSTRAINT ck_submission_type CHECK (submission_type IN ('GITHUB', 'ZIP'))
);
```

Index nen co:

```sql
CREATE INDEX idx_submission_assignment_round ON student_submissions(assignment_id, round_id);
CREATE INDEX idx_submission_latest ON student_submissions(assignment_id, round_id, is_latest);
CREATE INDEX idx_submission_submitted_by ON student_submissions(submitted_by);
```

## Entity va enum

Them enum:

```java
public enum StudentSubmissionType {
    GITHUB,
    ZIP
}
```

Them entity `StudentSubmission` voi quan he:

- `InternshipAssignment assignment`
- `AssessmentRound round` nullable
- `User submittedBy`

Khong luu raw file trong database. Chi luu metadata va ten file da store.

## DTO can tao va reuse

Can tao it DTO moi nhat co the:

- `StudentSubmissionResponse`: can co vi khong nen expose entity va can che giau `storedFileName` voi Student neu khong can.
- `StudentSubmissionCreateRequest`: dung cho GitHub URL + note. File ZIP dung `MultipartFile`, khong bat buoc tao DTO rieng.

Co the reuse du lieu co san trong response:

- Reuse `assignmentId`, `studentId`, `studentCode`, `studentFullName`, `mentorId`, `mentorFullName`, `phaseId`, `phaseName` tu query join/projection tu `InternshipAssignmentResponse` logic mapper.
- Neu can hien trong grading detail, chi add field `latestSubmission` vao `AssessmentGradingFormResponse` thay vi tao mot response detail moi.
- List response khong can tra `note` full neu note dai; detail moi tra note day du.

`StudentSubmissionResponse` de xuat:

```java
private Integer submissionId;
private Integer assignmentId;
private Integer roundId;
private String roundName;
private Integer studentId;
private String studentCode;
private String studentFullName;
private Integer mentorId;
private String mentorFullName;
private StudentSubmissionType submissionType;
private String githubUrl;
private String originalFileName;
private Long fileSizeBytes;
private String note;
private Integer versionNo;
private Boolean isLatest;
private LocalDateTime submittedAt;
```

Khong tra `storedFileName` ra FE. Download phai qua endpoint co kiem tra quyen.

## API de xuat

| Method | Endpoint | Role | Chuc nang |
| --- | --- | --- | --- |
| GET | `/api/student-submissions` | Admin, Mentor | Danh sach bai nop, filter theo phase/round/student/mentor/type |
| GET | `/api/student-submissions/my` | Student | Student xem bai da nop cua minh |
| GET | `/api/student-submissions/{id}` | Admin, Mentor, owner Student | Xem chi tiet bai nop |
| POST | `/api/student-submissions/github` | Student | Nop GitHub URL |
| POST | `/api/student-submissions/zip` | Student | Upload ZIP bai lam |
| GET | `/api/student-submissions/{id}/download` | Admin, Mentor assigned, owner Student | Download ZIP |
| DELETE | `/api/student-submissions/{id}` | Student owner, Admin | Xoa/thu hoi neu chua cham |

Query list:

```text
GET /api/student-submissions?phaseId=1&roundId=2&mentorId=3&studentCode=SE001&type=ZIP&page=0&size=20
```

## Security va validation

- Student chi nop cho assignment cua chinh minh.
- Mentor chi xem/download submission cua student minh duoc phan cong.
- Admin xem/download toan bo.
- GitHub URL chi chap nhan domain `github.com` hoac `www.github.com`, scheme `https`.
- ZIP chi chap nhan `.zip`, content type hop le, gioi han kich thuoc de xuat 20MB.
- Luu file ngoai web root, khong cho truy cap truc tiep qua static path.
- Download endpoint phai set `Content-Disposition: attachment` va kiem tra quyen moi lan goi.
- Khong log token, path noi bo, file content. Log metadata: actor, submission id, assignment id, file size.

## Backend service

Tao:

- `StudentSubmissionRepository`
- `StudentSubmissionService`
- `StudentSubmissionServiceImpl`
- `StudentSubmissionController`
- `StudentSubmissionMapper`

Service method de xuat:

```java
Page<StudentSubmissionResponse> getSubmissions(StudentSubmissionSearchCriteria criteria, Pageable pageable, UserPrincipal currentUser);
StudentSubmissionResponse getSubmissionById(Integer id, UserPrincipal currentUser);
StudentSubmissionResponse submitGithub(StudentSubmissionCreateRequest request, UserPrincipal currentUser);
StudentSubmissionResponse submitZip(Integer assignmentId, Integer roundId, String note, MultipartFile file, UserPrincipal currentUser);
Resource downloadZip(Integer submissionId, UserPrincipal currentUser);
```

Neu muon giam DTO, co the khong tao `StudentSubmissionSearchCriteria` ban dau, dung request params truc tiep trong controller. Khi filter phuc tap hon moi tach criteria.

## Frontend can lam

Them service `studentSubmissionService.ts`:

- `getAll(params)`
- `getMine(params)`
- `getById(id)`
- `submitGithub(body)`
- `submitZip(formData)`
- `download(id)`

Them UI:

- Student: tab `Nop bai` trong assignment/detail hoac route rieng `My Submissions`.
- Admin/Mentor: page `Submissions` trong nhom Evaluation hoac Internship.
- Table desktop: Student, Assignment, Round, Type, Version, Submitted At, Action.
- Action: `Open GitHub`, `Download ZIP`, `View Detail`.
- Upload form co radio `GitHub link` / `ZIP file`, note ngan, validate client truoc khi submit.

## Test can co

- Student nop GitHub URL hop le thanh cong.
- GitHub URL khong phai github.com bi `400 INVALID_INPUT_DATA`.
- Student upload non-zip bi `400 INVALID_INPUT_DATA`.
- Student nop assignment cua nguoi khac bi `403 ACCESS_DENIED`.
- Mentor download bai cua student khong duoc phan cong bi `403`.
- Admin list tat ca submission thanh cong.
- Latest version duoc cap nhat dung khi Student nop lai.

## Thu tu thuc hien

1. Them migration/entity/enum/repository.
2. Them storage validation cho ZIP, reuse logic tu file upload avatar neu phu hop.
3. Them service authorization va mapper response.
4. Them controller + Swagger annotation.
5. Them FE service va UI Student submit.
6. Them FE Admin/Mentor list va action download/open link.
7. Them test backend va build FE.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `07-student-submission-github-zip`, phai commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-student-submission-da-sua>
git commit -m "feat: add student submission upload workflow"
git push origin <branch-name>
```

Khong gom thay doi cua task UI density, detail view hoac response optimization vao commit nay.