# Response DTO va Query Optimization

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Truoc khi code, chay `npx skills find "spring boot dto projection api performance database query optimization"`.
- Neu co skill tot ve API design, JPA projection hoac performance, cai va doc truoc khi code.

## Muc tieu

Chuan hoa cach tra response va cach lay du lieu cho cac man hinh list/detail/submission/grading. Muc tieu la reuse DTO hien co khi hop ly, khong tao qua nhieu DTO, nhung van dam bao bao mat va khong lam cham he thong do query thua hoac expose entity.

## Van de hien tai

He thong da co nhieu response co the reuse:

- `StudentResponse`
- `InternshipAssignmentResponse`
- `WeeklyReportResponse`
- `AssessmentResultResponse`
- `AssessmentGradingFormResponse`
- `CompanyResponse`
- `UserResponse`

Khong nen tao moi response cho moi card/table neu chi khac 1-2 field. Tuy nhien cung khong nen tra entity truc tiep hoac nh?i qua nhieu nested object vi de lo du lieu va gay query N+1.

## Nguyen tac DTO

1. List response phai gon, phuc vu table/card, khong tra text dai hoac nested object sau.
2. Detail response co the day du hon, nhung uu tien reuse response hien co va them field can thiet.
3. Khong expose entity JPA truc tiep ra controller.
4. Khong tra password hash, token, stored file path, internal filename, audit payload nhay cam.
5. FE khong duoc dua vao text `message` de branch logic; dung `error_code` va HTTP status.
6. Response phai dung `SuccessResponse<T>` va `ErrorResponse` da co.

## Phan loai response nen dung

| Nhu cau | Cach lam de xuat |
| --- | --- |
| List table | Reuse response hien co neu field du gon; neu qua nang thi tao `SummaryResponse` duy nhat cho domain lon |
| Detail modal/page | Reuse response hien co va bo sung field lien quan can thiet |
| Select/dropdown | Tao endpoint projection nhe neu list lon, vi response day du gay cham |
| File/download | Khong tra path noi bo; tra metadata, download qua endpoint rieng |
| Dashboard | Dung aggregate query/projection, khong load entity roi count tren memory |

## Reuse cu the cho tinh nang nop bai

- `StudentSubmissionResponse` la DTO moi can co vi domain moi va co yeu cau bao mat file path.
- Trong `AssessmentGradingFormResponse`, chi them field `StudentSubmissionResponse latestSubmission` hoac `List<StudentSubmissionResponse> submissions` neu can hien bai nop khi cham diem.
- Trong `InternshipAssignmentResponse`, co the them `latestSubmissionId`, `latestSubmissionType`, `latestSubmittedAt` cho table assignment neu can hien trang thai nop bai. Khong can nested full submission trong list assignment.
- Trong `StudentResponse`, khong nen them danh sach submission vi se lam n?ng list students. Student detail moi query submission rieng.

## Query optimization

Backend nen uu tien:

- Repository query co `@EntityGraph` hoac JPQL join fetch cho detail can relationship.
- Projection interface/DTO constructor cho list lon.
- Pageable cho tat ca list co kha nang tang du lieu.
- Index theo filter hay dung: `student_id`, `mentor_id`, `phase_id`, `round_id`, `assignment_id`, `submitted_at`.
- Khong goi repository trong loop neu co the join query mot lan.

Vi du list submission nen query projection:

```java
Page<StudentSubmissionListProjection> findSubmissions(..., Pageable pageable);
```

Projection chi gom field can hien tren table:

```java
Integer getSubmissionId();
Integer getAssignmentId();
String getStudentCode();
String getStudentFullName();
String getMentorFullName();
String getRoundName();
String getSubmissionType();
String getOriginalFileName();
LocalDateTime getSubmittedAt();
```

Detail moi load them note va cac field day du.

## Bao mat response

Khong tra cac field sau ra client:

- `passwordHash`
- JWT/refresh token tru endpoint login
- `storedFileName`
- absolute file path tren server
- DB internal exception/SQL
- stack trace
- audit log raw payload neu co sensitive data

Download ZIP phai qua endpoint:

```text
GET /api/student-submissions/{id}/download
```

Endpoint nay kiem tra role/ownership va chi sau do moi doc file.

## Backend changes

- Review tat ca controller moi phai tra `SuccessResponse`.
- Neu DTO hien co qua nang cho list, tao mot `ListItemResponse` cho domain co list lon, khong tao nhieu response trung lap.
- Them query projection cho `StudentSubmission`, grading result summary va student detail neu can.
- Them test cho response khong expose field nhay cam.

## Frontend changes

- FE service chi nhan DTO da chuan hoa, mapper sang type UI neu can.
- Table list dung DTO gon.
- Detail modal/page goi API detail khi user bam xem, khong load detail cho tat ca row ngay tu dau.
- Cache nhe trong state theo id neu user mo lai detail trong cung session.

## Acceptance criteria

- Khong controller nao tra entity truc tiep cho API moi.
- List API co pageable va khong tra field nhay cam.
- Detail API chi query khi can.
- Submission file khong expose internal path.
- Test backend pass.
- FE build pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `08-response-dto-query-optimization`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-response-query-optimization-da-sua>
git commit -m "refactor: optimize response dto and query usage"
git push origin <branch-name>
```

Khong gom UI density hoac student submission feature neu task do chua hoan thanh trong cung scope da duoc phe duyet.