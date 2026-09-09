# Admin CRUD Completeness Audit va Implementation

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Bat buoc doc cac rule trong `docs/rule` truoc khi code:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code, chay `npx skills find "spring boot crud api react admin dashboard forms testing"`.
- Neu task co chinh UI CRUD, doc/use `frontend-design` neu co san.
- Neu task co them test backend, doc/use `spring-boot-test-patterns` neu co san.

## Muc tieu

Kiem tra lai toan bo he thong de xac dinh tinh nang CRUD nao dang thieu o Backend, Frontend hoac ca hai. Ket qua cuoi cung bat buoc la FE Admin co CRUD day du cho cac chuc nang quan tri he thong, voi API backend tuong ung hoat dong dung, bao mat dung role va response dung chuan.

Task nay khong chi viet bao cao. Sau khi audit, agent phai implement cac phan thieu de Admin UI co the thao tac day du.

## Dinh nghia CRUD day du

Voi moi module quan tri, Admin phai co:

- Create: tao moi resource neu nghiep vu cho phep.
- Read list: xem danh sach co search/filter/pagination neu du lieu co the tang.
- Read detail: xem chi tiet mot record.
- Update: cap nhat thong tin chinh cua record.
- Delete/Deactivate/Cancel: xoa mem, vo hieu hoa, archive hoac huy theo dung nghiep vu.

Neu mot module khong nen xoa cung vi rang buoc nghiep vu, phai co action thay the va ghi ro ly do.

## Pham vi module can audit

| Module | Backend endpoint | FE Admin can co |
| --- | --- | --- |
| Users | `/api/users` | List, detail, create, edit, change role, active/inactive, delete/deactivate |
| Students | `/api/students` | List, detail, create, edit, delete/deactivate |
| Mentors | `/api/mentors` | List, detail, create, edit, delete/deactivate |
| Companies | `/api/companies` | List, detail, create, edit, active/inactive, delete/deactivate |
| Internship Phases | `/api/internship_phases` | List, detail, create, edit, delete/archive |
| Assignments | `/api/internship_assignments` | List, detail, create, edit fields if needed, update status, cancel/delete |
| Applications | `/api/internship_applications` | List, detail, create draft if Admin can, approve, reject, cancel, update draft if applicable |
| Weekly Reports | `/api/weekly_reports` | List, detail, review, update allowed fields, delete if role/rule allows |
| Evaluation Criteria | `/api/evaluation_criteria` | List, detail, create, edit, delete/archive |
| Assessment Rounds | `/api/assessment_rounds` | List, detail, create, edit, delete/archive, manage criteria |
| Round Criteria | `/api/round_criteria` | List by round, detail, add, edit weight/max score, remove |
| Assessment Results / Grading | `/api/assessment_results`, `/api/assessment_grading` | List, detail, create/save draft, update, submit, publish, delete/reset if allowed |
| Student Submissions | `/api/student-submissions` | List, detail, open GitHub, download ZIP, delete/archive if allowed |
| Notifications | `/api/notifications` | Read/read-all/delete own; Admin CRUD only if manual notification management is required |

## Audit method bat buoc

Agent phai lap bang audit trong final note hoac task note:

| Module | BE Create | BE List | BE Detail | BE Update | BE Delete/Deactivate | FE Create | FE List | FE Detail | FE Update | FE Delete/Deactivate | Gap | Action |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

Lenh kiem tra bat buoc:

```powershell
rg -n "@(Get|Post|Put|Patch|Delete)Mapping|@RequestMapping" BE\src\main\java\com\se191116\studymanagement\controller -g "*.java"
rg -n "api\.(get|post|put|delete)|handle(Create|Update|Delete|Edit|Save)|Detail" FE\src -g "*.ts" -g "*.tsx"
Get-Content BE\build.gradle
Get-Content FE\package.json
```

Khong duoc doan dua tren ten file view. Phai doi chieu controller/service/FE service/view action thuc te.

## Huong implement Backend

Neu backend thieu CRUD endpoint:

1. Reuse entity, repository, DTO, mapper, service hien co truoc khi tao moi.
2. Khong expose entity truc tiep.
3. Tao DTO moi chi khi DTO hien co khong dap ung bao mat hoac contract.
4. Dung `SuccessResponse` va `ErrorResponse` theo rule.
5. Dung status code dung: POST `201`, GET/PUT/PATCH `200`, DELETE/deactivate `200` hoac `204` neu FE xu ly duoc.
6. Them `@PreAuthorize` cho Admin action.
7. Service phai validate business rule va ownership/role.
8. Delete nen uu tien soft delete/deactivate voi bang co quan he.

## Huong implement Frontend Admin

Moi trang Admin CRUD phai co:

- Button create neu Admin duoc tao.
- Table/list co search/filter can thiet.
- Action detail cho tung row.
- Action edit cho tung row.
- Action delete/deactivate/cancel co confirm modal.
- Loading, empty va error state.
- Form validation FE co ban truoc khi submit.
- Sau create/update/delete phai refresh list hoac update state dung.
- Khong co action nao goi endpoint chua ton tai.

UI phai tuan theo task 09: content compact, dong bo spacing, desktop hien nhieu cot, sidebar/header giu nguyen.

## Cac gap co dau hieu can kiem tra ky

Day la danh sach nghi van, agent phai xac minh lai bang code truoc khi sua:

- `StudentController` co create/update/list/detail nhung can kiem tra delete/deactivate backend va FE action.
- `MentorController` co create/update/list/detail nhung can kiem tra delete/deactivate backend va FE action.
- `InternshipAssignmentController` co create/list/detail/update status, can xem co can update fields va cancel/delete khong.
- `AssessmentResultController` co list/create/update, can xem co detail/delete/reset khong hay da duoc thay bang grading workflow.
- `EvaluationCriteriaView` can xac minh create/update/delete co goi API that hay chi local/mock.
- `PhasesView`, `AssessmentRoundsView`, `MentorsView`, `StudentsView` can xac minh co form create/edit/delete day du hay chua.
- `SubmissionsView` can xac minh Admin co list/detail/download/delete va role action dung.

## Thu tu thuc hien

1. Audit backend controllers/service/repositories.
2. Audit frontend services/views/components.
3. Lap bang gap BE vs FE.
4. Implement backend gap truoc, kem test.
5. Implement frontend Admin gap sau.
6. Chay backend test.
7. Chay frontend lint/build.
8. Login Admin va smoke test CRUD quan trong neu backend dang chay.

## Test can co

Backend:

- Admin create/read/update/delete hoac deactivate thanh cong cho module bo sung.
- Non-admin bi `403` khi goi Admin CRUD endpoint.
- Not found tra `404 RESOURCE_NOT_FOUND`.
- Duplicate/conflict tra `409 DUPLICATE_RESOURCE` neu co.
- Validation fail tra `400 INVALID_INPUT_DATA`.

Frontend:

- TypeScript pass.
- Build pass.
- Admin view co du action create/detail/edit/delete hoac deactivate.
- Delete/deactivate co confirm.
- API error hien message than thien.

## Acceptance criteria

- Co bang audit CRUD cho tat ca module trong pham vi.
- Moi gap phai ghi ro thieu o BE, FE hay ca hai.
- FE Admin co CRUD day du cho cac module quan tri sau khi task xong.
- Khong co action FE nao goi endpoint chua ton tai.
- Khong co endpoint backend quan tri nao thieu authorization Admin.
- Response/error handling dung rule trong `docs/rule/http-error-response-rule.md`.
- Khong tao DTO moi neu DTO hien co co the reuse an toan.
- Khong them dependency moi neu framework/codebase hien co da dap ung.
- `BE`: `./gradlew.bat test` pass.
- `FE`: `npm run lint` va `npm run build` pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `11-admin-crud-completeness-audit`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-crud-audit-va-implementation-da-sua>
git commit -m "feat: complete admin CRUD coverage"
git push origin <branch-name>
```

Khong gom thay doi cua task 07-10 neu cac task do chua duoc thuc hien/push rieng.