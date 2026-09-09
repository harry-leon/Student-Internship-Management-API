# File And Image Upload Storage Completion

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Bat buoc doc cac rule trong `docs/rule` truoc khi code:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/google-oauth-client-config-rule.md` neu dung Google Cloud trong deploy.
- Truoc khi code, chay `npx skills find "spring boot multipart file upload image avatar cloud storage react form validation"`.
- Neu task co cloud storage, uu tien kiem tra official SDK va dependency hien co truoc khi them dependency moi.
- Neu task co UI upload/preview, doc/use `frontend-design` neu co san.
- Neu task co test backend upload, doc/use `spring-boot-test-patterns` neu co san.

## Muc Tieu

Hoan thien mot lop upload/storage dung chung cho toan he thong de xu ly:

- Avatar user.
- Anh dai dien/cong ty neu co.
- Anh minh chung trong bao cao hoac danh gia.
- File bao cao tuan.
- File zip bai nop cua student.
- Tai file/download cho Admin/Mentor/Student theo dung permission va data scope.

Khong luu file binary truc tiep trong database. Database chi luu metadata, file that luu o local storage khi dev va cloud object storage khi deploy.

## Huong Phu Hop

Tao abstraction `FileStorageService` de tach nghiep vu upload khoi provider luu tru.

```text
FileStorageService
  - LocalFileStorageService: dung khi dev/local, khong ton phi cloud.
  - GoogleCloudStorageService: dung khi deploy production/staging neu da cau hinh GCS.
```

Ly do:

- Local dev khong phu thuoc internet/cloud credential.
- Production co the dung Google Cloud Storage private bucket.
- Sau nay neu chuyen AWS S3/Azure Blob chi can them adapter moi, khong sua business service.
- Giam duplicate code upload trong submission/avatar/report.

## Storage Provider De Xuat

Giai doan hien tai nen uu tien:

```text
Dev/Test: Local storage
Production: Google Cloud Storage private bucket
```

Khong public bucket. Download file private qua backend stream hoac signed URL ngan han.

## Database De Xuat

Tao bang metadata dung chung, vi du `stored_files`.

| Field | Type | Note |
| --- | --- | --- |
| file_id | identity/int hoac uuid | Primary key |
| owner_user_id | int | User upload/owner |
| linked_entity_type | varchar(50) | USER_AVATAR, STUDENT_SUBMISSION, WEEKLY_REPORT, COMPANY_IMAGE, EVIDENCE |
| linked_entity_id | int/null | ID resource lien quan neu co |
| storage_provider | varchar(30) | LOCAL, GCS |
| bucket_name | varchar(150)/null | Bucket neu cloud |
| object_key | varchar(500) | Path/key trong storage |
| original_file_name | varchar(255) | Ten file user upload |
| stored_file_name | varchar(255) | Ten file da sanitize/rename |
| content_type | varchar(100) | MIME type |
| file_extension | varchar(20) | jpg, png, pdf, zip |
| file_size | bigint | Byte |
| checksum_sha256 | varchar(64)/null | Neu can detect duplicate/integrity |
| status | varchar(20) | ACTIVE, DELETED |
| created_at | timestamp | Audit |
| updated_at | timestamp | Audit |

Khong luu path tuyet doi tren may local vao response public. Response chi tra `fileId`, metadata an toan va URL tam thoi neu can.

## Cau Hinh Backend

Can co config bang environment/application properties:

```properties
app.storage.provider=${STORAGE_PROVIDER:LOCAL}
app.storage.local-dir=${UPLOAD_DIR:uploads}
app.storage.max-avatar-size=${MAX_AVATAR_SIZE:2097152}
app.storage.max-image-size=${MAX_IMAGE_SIZE:5242880}
app.storage.max-document-size=${MAX_DOCUMENT_SIZE:10485760}
app.storage.max-zip-size=${MAX_ZIP_SIZE:104857600}
app.storage.gcs.bucket=${GCS_BUCKET:}
app.storage.gcs.project-id=${GCS_PROJECT_ID:}
app.storage.signed-url-expiration-minutes=${SIGNED_URL_EXPIRATION_MINUTES:5}
```

Neu dung GCS, credential khong commit vao repo. Dung bien moi truong hoac secret manager cua hosting.

## Backend Can Implement

### Storage Core

- `FileStorageService` interface:
  - `StoredFileMetadata store(MultipartFile file, FileUploadContext context)`
  - `Resource load(String objectKey)` hoac stream method cho local.
  - `String createReadUrl(StoredFile file, Duration ttl)` neu provider ho tro signed URL.
  - `void delete(String objectKey)` soft delete metadata truoc, hard delete storage neu business cho phep.
- `LocalFileStorageService`.
- `GoogleCloudStorageService` neu production config GCS da san sang.
- `FileValidationService` validate size, extension, content type va filename.
- `StoredFileRepository` va mapper response.

### Validation Bat Buoc

- Avatar chi cho phep: `image/jpeg`, `image/png`, `image/webp`.
- Evidence image chi cho phep image MIME type hop le.
- Report document co the cho phep: pdf, doc, docx, xlsx neu nghiep vu can.
- Submission zip chi cho phep `.zip` va MIME type phu hop.
- Gioi han size theo loai file.
- Sanitize filename, khong tin vao filename tu client.
- Object key phai do backend sinh, vi du:

```text
avatars/{userId}/{uuid}.webp
submissions/{assignmentId}/{studentId}/{uuid}.zip
weekly-reports/{reportId}/{uuid}.pdf
evidence/{entityType}/{entityId}/{uuid}.jpg
```

- Chong path traversal: khong ghep path tu input user truc tiep.
- Neu inspect zip, phai chong zip slip va gioi han entry count/size.

### API De Xuat

#### Avatar

`POST /api/files/avatar`

- Role: authenticated user.
- Upload avatar cua chinh user dang login.
- Update `Users.avatarUrl` hoac `avatarFileId` tuy schema hien co.

`GET /api/files/{fileId}`

- Lay metadata/download URL neu user co quyen.

#### Generic File Download

`GET /api/files/{fileId}/download`

- Backend check permission + data scope truoc khi tra file/signed URL.
- Admin co the download theo permission.
- Mentor chi download file cua student thuoc assignment/group minh quan ly.
- Student chi download file cua minh hoac file duoc publish cho minh.

#### Student Submission Zip

`POST /api/student-submissions/{submissionId}/file` hoac reuse endpoint task 07 neu da co`

- Student upload zip bai lam.
- Admin/Mentor xem/download theo scope.

#### Evidence/Report Files

`POST /api/weekly-reports/{reportId}/files`

`POST /api/assessment-results/{resultId}/evidence` neu can`

Chi implement endpoint nao map voi feature hien co. Khong tao API generic upload khong co linked entity neu khong co nhu cau nghiep vu ro rang.

## Frontend Can Implement

### Shared Components

- `FileUploadField`: upload mot hoac nhieu file.
- `AvatarUpload`: preview avatar, crop/resize neu co thu vien san co; neu khong thi preview + validate size/type.
- `FilePreviewList`: hien file da upload, size, type, action download/delete.
- `UploadProgress`: progress/loading/error state.
- `Dropzone` neu co the dung input native; khong them dependency lon neu khong can.

### Avatar UI

- User profile/header co avatar hien tai.
- Button upload/change avatar.
- Validate image truoc khi submit.
- Preview anh moi truoc khi upload.
- Sau upload cap nhat auth user/avatar tren UI.

### Submission/Report UI

- Student upload zip bai lam trong submission form.
- Student upload report/evidence file neu module support.
- Admin/Mentor co button download file.
- Neu submission la GitHub link thi hien Open Link; neu file zip thi hien Download.
- Neu khong co permission download thi an button.

## Permission Va Data Scope

Can phu hop task 13 neu da implement RBAC:

- `FILE_UPLOAD_AVATAR` hoac authenticated self action.
- `FILE_VIEW`.
- `FILE_DOWNLOAD`.
- `FILE_DELETE`.
- `SUBMISSION_UPLOAD`.
- `SUBMISSION_DOWNLOAD`.

Neu task 13 chua xong, van phai enforce bang role + service data scope hien co:

- Admin: xem/download file quan tri.
- Mentor: file cua student thuoc minh quan ly.
- Student: file cua chinh minh.

## Google Cloud Storage Yeu Cau

Neu implement GCS adapter:

- Dung private bucket.
- Khong public object mac dinh.
- Dung service account credential qua environment/hosting secret.
- Tao signed URL ngan han cho download neu phu hop.
- Khong commit service-account JSON.
- Doc `docs/rule/google-oauth-client-config-rule.md` de khong nham OAuth Client voi storage credential.

Ghi ro trong final note neu chi implement local adapter va de GCS adapter la cau hinh production tiep theo.

## Error Handling

Tat ca loi phai theo `docs/rule/http-error-response-rule.md`:

- File rong/sai format/sai size: `400 INVALID_INPUT_DATA`.
- Khong co quyen download/delete: `403 ACCESS_DENIED`.
- File khong ton tai: `404 RESOURCE_NOT_FOUND`.
- Duplicate hoac business conflict: `409 DUPLICATE_RESOURCE` neu co.
- Loi storage/cloud: `503 SERVICE_UNAVAILABLE` hoac `500 INTERNAL_SERVER_ERROR` tuy nguyen nhan.

UI khong hien stack trace, bucket name noi bo, object key noi bo hoac path local.

## Test Can Co

Backend:

- Upload avatar dung type/size thanh cong.
- Upload avatar sai type bi `400`.
- Upload zip dung rule thanh cong.
- Upload file qua size bi `400`.
- Download file cua minh thanh cong.
- Mentor download file student khong thuoc scope bi `403`.
- Student download file cua student khac bi `403`.
- File khong ton tai bi `404`.
- Filename path traversal khong tao file ngoai storage root.

Frontend:

- `npm run lint` pass.
- `npm run build` pass.
- Avatar upload co preview/loading/error.
- Submission zip upload co validate type/size.
- Admin/Mentor download button chi hien khi co quyen.

## Thu Tu Thuc Hien

1. Doc rule va chay skill discovery.
2. Audit upload/file code hien co trong BE/FE.
3. Kiem tra dependency hien co trong `BE/build.gradle` va `FE/package.json`.
4. Thiet ke `stored_files` va storage abstraction.
5. Implement local storage adapter truoc.
6. Implement validation va metadata repository.
7. Implement avatar upload endpoint va UI.
8. Tich hop zip upload/download voi student submissions.
9. Tich hop report/evidence upload neu module da co flow ro rang.
10. Them GCS adapter neu credential/config production da san sang; neu chua, de config ro trong docs/task note.
11. Chay backend tests.
12. Chay FE lint/build.
13. Smoke test upload avatar, upload zip, download voi Admin/Mentor/Student.

## Acceptance Criteria

- Co storage abstraction dung chung, khong code upload rieng le lap lai o tung module.
- Dev local upload duoc bang local storage.
- Avatar upload/preview/update UI hoat dong.
- Student upload zip bai lam hoat dong.
- Admin/Mentor download file submission dung scope.
- File metadata luu database, binary khong luu database.
- Validation type/size/filename/path traversal day du.
- Response khong expose path local, object key noi bo hoac secret.
- GCS production path duoc thiet ke/cau hinh ro, khong public bucket.
- Error handling dung rule.
- Backend test pass.
- Frontend lint/build pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `14-file-image-upload-storage-completion`, commit va push rieng task nay.

```powershell
git status
cd BE
.\gradlew.bat test
cd ..\FE
npm run lint
npm run build
git add <cac-file-task-14-da-sua>
git commit -m "feat: complete file and image upload storage"
git push origin <branch-name>
```

Khong gom thay doi cua task khac neu cac task do chua duoc thuc hien/push rieng.
