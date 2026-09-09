# Current Implementation vs Roadmap Review

Tai lieu nay dung de danh gia cach trien khai hien tai so voi cac task trong `docs/feature-roadmap`, sau do chon huong lam phu hop nhat cho project. Nguyen tac chinh: khong bat code phai giong roadmap mot cach may moc; giu cach hien tai neu no hop kien truc va tot hon, chi sua nhung diem roadmap dang dung hon ve nghiep vu, bao mat hoac kha nang van hanh that.

## Cach Doc Ket Qua

Moi task duoc danh gia theo ba nhom:

- `Giu cach hien tai`: code dang di dung huong, nen tiep tuc theo pattern da co.
- `Sua theo roadmap`: roadmap dang mo ta dung hon ve nghiep vu hoac tieu chuan ky thuat.
- `Can kiem chung`: chua du bang chung ve test, migration, API contract hoac flow end-to-end.

## Ket Qua Kiem Tra Build

- Frontend `npm run lint`: pass.
- Frontend `npm run build`: pass khi chay ngoai sandbox do Vite/esbuild can spawn process.
- Backend `./gradlew.bat compileJava`: pass khi chay ngoai sandbox do Gradle can ghi cache/lock trong user home.
- Backend test: chua co test nghiep vu dang ke; hien chi thay test `contextLoads`.

## 00 Platform Foundation

### Cach hien tai

Project da co mot so nen tang dung huong:

- `SecurityConfig` co JWT filter, stateless session, CORS doc tu `app.cors.allowed-origins`.
- `application.properties` da dung bien moi truong cho database, JWT, CORS, upload dir va OAuth2 config.
- Frontend `apiClient` da doc `VITE_API_BASE_URL`.
- Frontend da co co che xoa token va dispatch `auth:unauthorized` khi API tra `401`.
- `AuthContext` co goi `/me` de khoi phuc user state khi reload.

### So voi roadmap

Cach hien tai nen giu cho cac phan `CORS`, `VITE_API_BASE_URL`, `401 auto logout` vi da hop voi yeu cau roadmap. Tuy nhien, chua nen coi task nay hoan thanh vi con cac diem sau:

- `jwt.secret` dang co default secret trong source. Dieu nay tien cho local demo nhung khong dat tieu chuan security cua roadmap.
- OAuth2 Google co config dependency va properties, nhung can kiem tra day du callback, exchange code, status API va UI button.
- Avatar hien van con nhieu fallback `ui-avatars.com`; can kiem tra upload API, profile upload UI, validate file type/size va storage path.
- Refresh token, logout all hoac token revocation chua du bang chung hoan thien.
- Audit log da xuat hien trong service, nhung can chuan hoa actor, target, metadata va tranh lam fail nghiep vu chinh neu ghi log loi.

### Huong chon

Giu kien truc hien tai, nhung sua theo roadmap o cac diem bao mat:

- Bo default secret that trong `application.properties`; dung env hoac gia tri demo ro rang khong dung cho deploy.
- Hoan thien avatar upload neu scope task `00` bat buoc.
- Hoan thien OAuth2 flow neu project can Google login that.
- Them test cho login, inactive user, private API thieu token, ownership, avatar validation.

## 01 Company Management

### Cach hien tai

Task nay dang duoc trien khai kha dung:

- Backend co `CompanyController`, `CompanyService`, `CompanyServiceImpl`, entity, repository, DTO, mapper.
- API dung `SuccessResponse`.
- Co `@PreAuthorize`: Admin duoc create/update/status/delete, Admin/Mentor/Student duoc xem.
- Service co duplicate check theo `companyName`.
- Delete dang la soft delete bang `isActive = false`, phu hop voi roadmap.
- Frontend co `CompaniesView` va `companyService`, co loading, error, empty state, filter status, search, modal create/edit.

### So voi roadmap

Cach hien tai phu hop hon viec viet lai theo roadmap tu dau. Mot so diem can xem tiep:

- Search hien tai chi theo `companyName`, trong khi roadmap goi y search theo ten, nganh, email, trang thai.
- Chua thay check capacity khi assign sinh vien vao company.
- Chua thay test backend cho duplicate, 403 va deactivate.
- Can kiem tra schema/migration that su co `companies` va `company_id` trong `internship_assignments`.

### Huong chon

Giu cach hien tai. Chi bo sung:

- Search nhieu field neu UI can.
- Capacity check trong luong tao assignment/approve application.
- Test cho create/update/status/permission.

## 02 Internship Registration And Approval

### Cach hien tai

Backend dang co workflow ro:

- Student tao draft.
- Student update neu `DRAFT` hoac `REJECTED`.
- Student submit sang `SUBMITTED`.
- Admin approve/reject.
- Reject bat buoc co ly do.
- Approve co the tao `InternshipAssignment` neu co mentor.
- Service co check student ownership o cac thao tac cua Student.
- Frontend co `InternshipApplicationsView`, tabs/filter, form tao draft, submit, cancel, modal approve/reject.

### So voi roadmap

Cach hien tai co cau truc tot va nen giu, nhung co mot diem nghiep vu quan trong can quyet dinh:

- Khi approve, neu assignment cung `student + phase` da ton tai, service hien tai khong tao assignment moi nhung van set application thanh `APPROVED`.
- Roadmap de xuat approve phai bao loi duplicate hoac lien ket vao assignment hien co mot cach ro rang.

Ngoai ra frontend dang hard-code:

- `phaseId: 1`.
- danh sach mentor trong select chi co option cung.

Dieu nay phu hop demo nhung chua phu hop he thong that.

### Huong chon

Giu workflow hien tai, sua theo roadmap cac diem sau:

- Approve phai xu ly duplicate assignment ro rang: hoac throw conflict, hoac link application voi assignment hien co neu domain chap nhan.
- FE phai lay phase active va mentor list tu API thay vi hard-code.
- Them notification khi submit, approve, reject.
- Them test cho state transition va duplicate assignment.

## 03 Weekly Progress Report

### Cach hien tai

Backend dang di dung huong:

- Co `WeeklyReportController`, `WeeklyReportServiceImpl`, DTO, mapper, repository.
- Co filter theo `phaseId`, `assignmentId`, `studentId`, `mentorId`, `status`, `weekNumber`.
- Student bi ep chi thay report cua minh.
- Mentor bi ep chi thay report cua assignment minh phu trach.
- Co unique check theo `assignmentId + weekNumber`.
- Student chi update khi `DRAFT` hoac `NEEDS_REVISION`.
- Mentor/Admin review report `SUBMITTED`.

### So voi roadmap

Backend hien tai phu hop roadmap. Diem yeu nam o frontend:

- `WeeklyReportsView` khoi tao bang mock data.
- Khi API loi, UI giu mock data va con gia lap create/submit/review thanh cong tren local state.
- Cach nay phu hop demo offline, nhung sai voi yeu cau he thong that vi nguoi dung co the tuong thao tac da thanh cong trong khi backend that that bai.

### Huong chon

Giu backend hien tai. Sua FE theo roadmap:

- Bo mock fallback cho thao tac create/submit/review.
- Neu API loi, hien error state ro rang.
- Chi hien data that tu API.
- Neu can demo offline, tach thanh che do mock co flag rieng, khong de mac dinh trong production flow.
- Them notification khi Student submit va Mentor review.

## 04 Rubric Grading Workflow

### Cach hien tai

Cach hien tai manh hon roadmap toi thieu:

- Da co `AssessmentSubmission`, khong chi luu result roi rac.
- Co status `DRAFT`, `SUBMITTED`, `PUBLISHED`.
- Backend tinh lai `totalScore` va `weightedScore`.
- Backend validate score khong vuot `maxScore`.
- Submit yeu cau du criteria cua round.
- Student chi xem khi da `PUBLISHED`.
- Khong cho sua grading da published.
- FE co `GradingFormModal`, hien criteria, score, comment, draft, submit, publish.

### So voi roadmap

Nen giu cach hien tai vi dung voi rui ro roadmap da neu: neu khong co submission status thi kho quan ly viec cham da xong hay chua.

Diem can quyet dinh:

- Controller cho phep `ADMIN` va `MENTOR` cung save draft/submit grading.
- Roadmap nghieng ve Mentor cham, Admin publish.

Neu nghiep vu truong yeu cau Admin co the cham thay, cach hien tai chap nhan duoc. Neu muon dung phan vai chat che, nen doi thanh:

- Mentor: save draft, submit.
- Admin: view, publish.
- Student: view published only.

### Huong chon

Giu model `AssessmentSubmission`. Bo sung:

- Xac dinh lai quyen Admin cham diem.
- Them test cho missing criterion, score vuot max, mentor khac assignment bi 403, student xem diem chua publish bi 403, admin publish.
- Kiem tra unique constraint `assignment + round` o database.

## 05 Role Based Dashboard

### Cach hien tai

Frontend da tach component:

- `DashboardView`.
- `AdminDashboard`.
- `MentorDashboard`.
- `StudentDashboard`.

Backend co `/api/dashboard/me` thong qua `DashboardServiceImpl`.

### So voi roadmap

Cach tach component la dung va nen giu. Tuy nhien, du lieu hien tai chua dat roadmap:

- `AdminDashboard`, `MentorDashboard`, `StudentDashboard` con nhieu so hard-code.
- `DashboardServiceImpl` voi Mentor dang dung `assignmentRepository.count()` va `weeklyReportRepository.count()`, tuc la dem toan he thong thay vi theo mentor hien tai.
- Admin `pendingApplications` dang dung `applicationRepository.count()`, khong filter `SUBMITTED`.
- Student dashboard chua lay assignment/report/result that cua student.

### Huong chon

Chon cach roadmap cho phan data, giu cach hien tai cho phan component structure:

- BE `/api/dashboard/me` phai tra KPI dung theo role.
- Mentor dashboard phai filter theo mentor.
- Student dashboard phai filter theo student.
- FE dashboard nen goi `dashboardService.getMyDashboard()` thay vi hien so cung.
- Khong hien action ma role khong co quyen.

## 06 Notification And Deadline Reminder

### Cach hien tai

Project da co nen tang notification:

- `NotificationService`.
- `NotificationServiceImpl`.
- `NotificationController`.
- `NotificationRepository`.
- `NotificationScheduler`.
- `SchedulingConfig` voi `@EnableScheduling`.
- `NotificationBell` co polling 60 giay va mark read/read all.
- Notification co `dedupeKey`.

### So voi roadmap

Cach hien tai dung ve skeleton, nhung chua dat flow nghiep vu that:

- `notifyUser` chi thay duoc goi tu scheduler, chua thay duoc goi tu `InternshipApplicationService`, `WeeklyReportService`, `AssessmentGradingService`.
- Scheduler dang gui reminder cho tat ca assignment moi ngay, chua check report nao chua nop, due soon hay overdue.
- FE `NotificationBell` van co mock notifications va fallback local khi API loi.
- Target route mapping con don gian, chua dieu huong den chi tiet target.

### Huong chon

Giu service/API/scheduler hien tai, sua theo roadmap o phan integration:

- Khi Student submit application: notify Admin.
- Khi Admin approve/reject application: notify Student.
- Khi assignment created: notify Student va Mentor.
- Khi Student submit weekly report: notify Mentor.
- Khi Mentor review weekly report: notify Student.
- Khi Mentor submit score: notify Admin.
- Khi Admin publish result: notify Student.
- Scheduler chi tao due/overdue reminder khi co dieu kien that, va dedupe theo recipient + target + date.
- FE bo mock fallback mac dinh.

## Uu Tien Sua De Dat Roadmap

1. Sua dashboard role-based data vi day la loi hien thi sai nghiep vu ro nhat.
2. Bo mock fallback trong `WeeklyReportsView` va `NotificationBell`.
3. Gan notification vao cac service nghiep vu.
4. Sua duplicate assignment trong approval flow.
5. Chuan hoa security secret va OAuth/avatar neu task `00` nam trong scope cham diem.
6. Them test cho cac business rule chinh.

## Tieu Chi Hoan Thanh That

Mot task chi nen danh dau hoan thanh khi co du cac diem sau:

- Backend co entity/repository/service/controller/DTO neu task yeu cau.
- API tra `SuccessResponse` va loi theo chuan hien co.
- Role duoc chan bang `@PreAuthorize`.
- Ownership duoc check trong service, khong chi an nut tren FE.
- Frontend goi API that, co loading/empty/error state.
- Khong co mock fallback trong flow production.
- Co test hoac it nhat manual checklist ro rang cho state transition, permission va loi nghiep vu.
- `npm run lint`, `npm run build`, `./gradlew.bat compileJava` pass.
- Neu lam theo quy trinh roadmap day du, commit va push rieng cho tung task.

