# Library Reuse Rule

Tai lieu nay quy dinh viec tim va uu tien dung thu vien, package, framework feature, component, plugin skill hoac MCP server da co truoc khi tu code lai tinh nang tu dau.

## Nguyen tac chung

1. Truoc khi tu code mot tinh nang co tinh pho bien, agent phai kiem tra thu vien, API, plugin skill hoac MCP server san co.
2. Uu tien tinh nang native cua framework dang dung truoc khi them dependency moi.
3. Uu tien dependency da co trong `build.gradle`, `package.json` hoac codebase truoc khi cai moi.
4. Chi them dependency moi khi no giam ro rang do phuc tap, tang bao mat, tang do tin cay hoac tiet kiem maintenance.
5. Khong them thu vien lon chi de lam mot viec nho co the dung API san co an toan.
6. Thu vien lien quan security/file upload/download phai duoc danh gia maintenance, license va rui ro truoc khi dung.
7. Moi dependency moi phai duoc ghi ly do trong task note/final note.
8. Neu plugin hoac MCP da cung cap nang luc can thiet, uu tien dung plugin/MCP truoc khi them dependency moi.

## Thu tu tim kiem bat buoc

1. Kiem tra codebase hien co bang `rg`:

```powershell
rg -n "<keyword>" BE FE docs -g "!*node_modules*"
```

2. Kiem tra dependency hien co:

```powershell
Get-Content BE\build.gradle
Get-Content FE\package.json
```

3. Kiem tra plugin va MCP neu workspace co:

- Doc `docs/rule/plugin-and-mcp-usage-rule.md`.
- Kiem tra plugin-local `skills/`, `rules/`, `mcp_config.json`, `hooks.json` neu co.
- Uu tien dung plugin skill hoac MCP server thay vi tu code lai hoac them dependency.

4. Kiem tra framework feature:

- Spring Boot/Spring Security/Spring Data JPA/Jakarta Validation.
- React/Vite/TypeScript browser APIs.
- Existing FE components/services/mappers.

5. Neu van can dependency moi, tim thu vien phu hop va danh gia:

- Maintenance gan day.
- Do pho bien.
- License.
- Kich thuoc package.
- Bao mat va CVE neu lien quan file/auth.
- Muc do phu hop voi task.

## Goi y reuse theo task 07-10

### Task 07 - Student Submission GitHub/ZIP

Nen reuse truoc:

- Spring `MultipartFile` cho upload.
- `java.nio.file.Files`, `Path`, `StandardCopyOption` cho luu file.
- Jakarta Validation cho request validation.
- Existing `FileStorageServiceImpl` pattern cho validate file path/storage.
- Existing `SuccessResponse`, `ErrorResponse`, `BadRequestException`, `ResourceNotFoundException`, `ResourceConflictException`.
- Existing auth/RBAC pattern voi `@PreAuthorize` va service ownership check.

Chi can them thu vien neu co nhu cau that su:

- Virus scanning.
- Archive inspection/chong zip slip nang cao.
- Cloud storage S3/GCS.

Khong nen tu code parser ZIP phuc tap neu chi can upload/download file. Neu can validate ZIP content, dung `java.util.zip.ZipInputStream` va gioi han kich thuoc/entry.

### Task 08 - DTO/Query Optimization

Nen reuse truoc:

- Spring Data `Page`, `Pageable`.
- JPQL constructor projection hoac interface projection.
- `@EntityGraph` cho query detail co relationship.
- Existing response DTO: `StudentResponse`, `InternshipAssignmentResponse`, `AssessmentGradingFormResponse`, `AssessmentResultResponse`, `WeeklyReportResponse`.

Khong nen them mapper/query library moi neu MapStruct va Spring Data da dap ung du.

### Task 09 - Frontend Content Density

Nen reuse truoc:

- Existing Tailwind utility classes.
- Existing view structure trong `FE/src/views`.
- Existing icons tu `lucide-react`.
- Existing API services and mappers.

Chi them UI table library neu:

- Can sorting/filtering/pagination phuc tap tren client.
- Table co nhieu column can column resize/hide.
- Team chap nhan dependency va style integration.

Neu them, can danh gia `@tanstack/react-table` truoc vi no headless va phu hop React/TypeScript.

### Task 10 - Detail Views

Nen reuse truoc:

- Existing modal pattern: `AssignmentDetailModal`, `GradingFormModal`, `ExportReportModal`.
- Existing response DTO cho student/assignment/grading/report.
- Existing service layer authorization.
- Browser `window.open` cho GitHub link sau khi URL da duoc backend validate.

Chi them modal/drawer library neu UI hien tai khong du dap ung accessibility/focus trap. Neu them, can danh gia Radix UI Dialog/Drawer hoac headless approach tu framework dang co.

## Tieu chi chap nhan dependency moi

Truoc khi them dependency moi, agent phai tra loi ngan gon trong task note:

```text
Library decision:
- Existing solution checked: <files/packages checked>
- New dependency needed: yes/no
- Selected library: <name/version or none>
- Reason: <why>
- Risk: <license/security/bundle size/maintenance>
```

## Cac truong hop khong duoc tu code tu dau

- Password hashing: phai dung `PasswordEncoder`/BCrypt hoac framework security.
- JWT parsing/signing: phai dung thu vien JWT dang co.
- Multipart parsing: phai dung Spring multipart support.
- Pagination: phai dung Spring Data `Pageable` va FE pagination pattern.
- Date/time parsing phuc tap: uu tien Java Time API hoac browser Intl.
- Table sorting/filtering phuc tap: can xem existing library/framework truoc.

## Cac truong hop khong nen them dependency

- Format message don gian.
- Validate string don gian co the dung Jakarta Validation/URL/URI.
- Mot modal/detail view don gian co the reuse component hien co.
- Mot table tinh voi pagination server-side don gian.
- Chuyen doi DTO don gian da co MapStruct hoac mapper hien tai.

## Goi y reuse theo task 17-19

### Task 17 - Dark Theme

Nen reuse truoc Tailwind CSS v4, CSS variables, theme.config.ts, ui.config.ts, layout.config.ts, lucide-react va motion da co trong FE. Khong them UI/theme library moi neu chi can chuan hoa token, class variant va component primitive.

Chi can can nhac dependency nho neu code class variant qua lap lai:

- clsx: ghep class co dieu kien.
- tailwind-merge: merge class Tailwind tranh conflict.
- class-variance-authority: tao variant cho Button/Card/Badge/Input neu primitive co nhieu state.

### Task 18 - Hardcoded Demo Data Cleanup va Auto Refresh

Khi nhieu page cung can loading/error/cache/refetch/invalidation, agent phai danh gia @tanstack/react-query truoc khi tu viet lai server-state layer.

Dung TanStack Query khi:

- Can cache server data theo query key.
- Can refetch sau mutation.
- Can polling dashboard/group/submission ma khong reload browser.
- Can optimistic update co rollback.
- Can tranh moi page tu viet loading/error/refetch rieng.

Can nhac @tanstack/react-table chi khi table can sorting/filtering/column visibility/column sizing phuc tap o client. Form phuc tap co the danh gia react-hook-form va zod, nhung khong bat buoc neu form hien tai don gian.

### Task 19 - Group Workspace, Chat, Read Receipts, Presence

Nen chia thanh 2 muc:

- MVP it rui ro: REST API + TanStack Query polling/refetch cho messages, read receipts, presence, tasks, submissions.
- Realtime phase: Spring WebSocket/STOMP o backend va @stomp/stompjs o frontend; chi them sockjs-client neu can fallback SockJS.

Thu vien can danh gia truoc khi tu code:

- Backend realtime: spring-boot-starter-websocket, Spring Messaging/STOMP.
- Frontend realtime: @stomp/stompjs.
- Popover/member detail/tooltip: uu tien Radix UI primitives hoac Floating UI neu component hien co chua dam bao focus, keyboard, positioning va accessibility.
- Animation nho: reuse motion da co.
- Icon/action: reuse lucide-react.
- Upload attachment/avatar: reuse component/file API cua task 14; chi them react-dropzone neu can drag-drop phuc tap tai nhieu man hinh.

Neu chua them realtime, phai ghi ro polling interval, query key, invalidate rule va ly do chua dung WebSocket.

## Bat buoc truoc khi final

- Neu co them dependency, chay build/test lien quan.
- Neu khong them dependency, noi ro da reuse gi.
- Khong de dependency moi khong dung trong `package.json` hoac `build.gradle`.
- Khong commit `node_modules`, build output, log, file upload local.
