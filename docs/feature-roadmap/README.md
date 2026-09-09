# Internship Management System Feature Roadmap

Tài liệu này chia nhỏ các tính năng nên bổ sung cho hệ thống Internship Management System. Mỗi file mô tả một nhóm tính năng theo hướng có thể triển khai thật trong project hiện tại, gồm mục tiêu nghiệp vụ, database, backend API, frontend, phân quyền, kiểm thử và thứ tự làm.

## Thứ tự ưu tiên đề xuất

1. `00-platform-foundation.md`
2. `01-company-management.md`
3. `02-internship-registration-approval.md`
4. `03-weekly-progress-report.md`
5. `04-rubric-grading-workflow.md`
6. `05-role-based-dashboard.md`
7. `06-notification-deadline-reminder.md`
8. `07-student-submission-github-zip.md`
9. `08-response-dto-query-optimization.md`
10. `09-frontend-content-density-standardization.md`
11. `10-detail-views-for-students-submissions-grading.md`
12. `11-admin-crud-completeness-audit.md`
13. `12-mentor-group-management.md`
14. `13-dynamic-rbac-permission-feature-flags.md`
15. `14-file-image-upload-storage-completion.md`
16. `15-mentor-group-collaboration-room.md`
17. `16-business-scope-group-task-submission-redesign.md`
18. `17-system-wide-dark-theme-standardization.md`
19. `18-frontend-hardcoded-demo-data-cleanup.md`
20. `19-group-workspace-read-receipts-presence.md`

## Skill discovery bắt buộc cho từng task

Trước khi làm bất kỳ task nào trong roadmap, phải tự tìm skill phù hợp cho task đó:

```powershell
npx skills find "<query-phu-hop-voi-task>"
```

Nếu có skill phù hợp, cài vào repository:

```powershell
npx skills add <owner>/<repo>
```

Sau khi cài, đọc `SKILL.md` của skill đó và áp dụng vào quá trình triển khai task. Mục tiêu là dùng skill để cải thiện chất lượng làm task, không chỉ ghi tên skill cho có.

Skill local của dự án luôn là skill nền:

```text
internship-management-system
```

Query đề xuất cho từng task:

| Task | Query nên chạy | Skill bắt buộc |
| --- | --- | --- |
| `00-platform-foundation.md` | `spring boot security jwt oauth2 google login file upload` | `internship-management-system` |
| `01-company-management.md` | `spring boot crud api database migration react form` | `internship-management-system` |
| `02-internship-registration-approval.md` | `workflow approval state machine react spring boot` | `internship-management-system` |
| `03-weekly-progress-report.md` | `weekly report file upload workflow react testing` | `internship-management-system` |
| `04-rubric-grading-workflow.md` | `rubric grading workflow api design react form validation` | `internship-management-system` |
| `05-role-based-dashboard.md` | `react dashboard role based access frontend testing` | `internship-management-system` |
| `06-notification-deadline-reminder.md` | `spring scheduler notification reminder react notification bell` | `internship-management-system` |
| `07-student-submission-github-zip.md` | `spring boot file upload validation workflow react form` | `internship-management-system` |
| `08-response-dto-query-optimization.md` | `spring boot dto projection api performance database query optimization` | `internship-management-system` |
| `09-frontend-content-density-standardization.md` | `react dashboard table layout responsive design frontend testing` | `internship-management-system` |
| `10-detail-views-for-students-submissions-grading.md` | `react detail view spring boot api dto authorization testing` | `internship-management-system` |
| `11-admin-crud-completeness-audit.md` | `spring boot crud api react admin dashboard forms testing` | `internship-management-system` |
| `12-mentor-group-management.md` | `spring boot group membership invitation code react admin mentor student workflow` | `internship-management-system` |
| `13-dynamic-rbac-permission-feature-flags.md` | `spring security rbac permissions feature flags react admin access control` | `internship-management-system` |
| `14-file-image-upload-storage-completion.md` | `spring boot multipart file upload image avatar cloud storage react form validation` | `internship-management-system` |
| `15-mentor-group-collaboration-room.md` | `spring boot websocket chat group task management file submission react realtime` | `internship-management-system` |
| `16-business-scope-group-task-submission-redesign.md` | `database design schema normalization spring boot erd` + `internship management group task submission workflow react spring boot` | `internship-management-system` |
| `17-system-wide-dark-theme-standardization.md` | `tailwind dark mode design system react dashboard accessibility contrast` | `internship-management-system` |
| `18-frontend-hardcoded-demo-data-cleanup.md` | `react frontend mock data cleanup api integration dashboard empty state testing` | `internship-management-system` |
| `19-group-workspace-read-receipts-presence.md` | `spring boot websocket chat read receipts online presence react group workspace` + `database design chat read receipts presence indexes` | `internship-management-system` |

Kết quả tìm kiếm skill hiện tại cho frontend có các lựa chọn đáng cân nhắc:

- `langgenius/dify@frontend-testing`: dùng khi cần kiểm thử giao diện và flow FE.
- `sickn33/agentic-awesome-skills@react-nextjs-development`: dùng tham khảo cho React development, dù project hiện tại là Vite chứ không phải Next.js.

Không cài skill chỉ vì có tên gần đúng. Chỉ cài khi task thật sự cần và skill có uy tín, nhiều lượt dùng, nội dung phù hợp.

Khi hoàn thành task, ghi lại trong commit/task note:

- Query đã dùng để tìm skill.
- Skill đã cài, nếu có.
- Lý do chọn hoặc lý do không cài skill nào.
- Cách skill đó ảnh hưởng đến thiết kế/triển khai.


## Rule bat buoc truoc khi implement

Truoc khi implement bat ky task nao, dac biet task 07 den 10, agent phai doc va lam dung cac rule trong folder `docs/rule`:

1. `docs/rule/http-error-response-rule.md`: chuan HTTP status, response body, UI error message va logging.
2. `docs/rule/skill-query-rule.md`: bat buoc chay skill discovery voi query tuong ung task truoc khi code.
3. `docs/rule/library-reuse-rule.md`: bat buoc kiem tra framework feature, dependency va component san co truoc khi tu code lai tinh nang.
4. `docs/rule/google-oauth-client-config-rule.md`: cau hinh Google OAuth Client, origins, redirect URIs va quy tac khong luu secret trong repo.
5. `docs/rule/frontend-ui-configuration-rule.md`: bat buoc dung frontend config, design token, UI primitive, navigation config, route config va permission config tap trung khi lam task frontend.

Quy trinh toi thieu truoc khi code:

```powershell
Get-Content docs\rule\http-error-response-rule.md
Get-Content docs\rule\skill-query-rule.md
Get-Content docs\rule\library-reuse-rule.md
Get-Content docs\rule\google-oauth-client-config-rule.md
Get-Content docs\rule\frontend-ui-configuration-rule.md
Get-Content docs\feature-roadmap\<task-file>.md
npx skills find "<query-tuong-ung-task>"
rg -n "<keyword-cua-tinh-nang>" BE FE docs -g "!*node_modules*"
Get-Content BE\build.gradle
Get-Content FE\package.json
```

Sau khi hoan thanh moi task, final note phai ghi ro:

- Rule da doc.
- Skill query da chay va ket qua chon/khong chon skill.
- Thu vien/component/API san co da reuse.
- Dependency moi neu co, ly do them va rui ro.
- Lenh test/build da chay.
- Commit/push rieng cho task do.

## Nguyên tắc triển khai

- Backend vẫn là lớp phân quyền chính. Frontend chỉ ẩn menu/nút để trải nghiệm đúng role.
- Mọi API mới nên trả về cùng chuẩn `SuccessResponse` và `ErrorResponse` hiện tại.
- Các API yêu cầu đăng nhập phải dùng JWT `Authorization: Bearer <token>`.
- Mỗi tính năng nên làm theo lát cắt dọc: database, entity, repository, service, controller, FE service, FE view, test.
- Không nên làm tất cả entity trước rồi mới làm UI. Cách đó dễ tạo nhiều model chết và khó kiểm thử nghiệp vụ.
- Mỗi task phải có một commit riêng và một lần push riêng lên GitHub sau khi task đó hoàn thành.
- Không gom nhiều task vào cùng một commit hoặc cùng một lần push.

## Quy trình GitHub bắt buộc sau mỗi task

Trước khi bắt đầu triển khai frontend, kiểm tra remote của repo FE:

```powershell
git remote -v
```

Nếu `origin` chưa trỏ tới repo frontend, cấu hình lại:

```powershell
git remote set-url origin https://github.com/harry-leon/Student-Internship-Management-Frontend.git
```

Nếu repo chưa có `origin`, thêm mới:

```powershell
git remote add origin https://github.com/harry-leon/Student-Internship-Management-Frontend.git
```

Sau khi hoàn thành một task, luôn chạy kiểm tra chất lượng trước:

```powershell
npm run lint
npm run build
```

Sau đó commit và push riêng task đó:

```powershell
git status
git add <cac-file-cua-task-hien-tai>
git commit -m "feat: complete <task-name>"
git push origin <branch-name>
```

Quy tắc quan trọng:

- Chỉ add các file thuộc task vừa làm.
- Không commit file build tạm, `.env`, `node_modules`, log hoặc file nhạy cảm.
- Nếu task có cả backend và frontend, commit/push theo repo tương ứng, nhưng vẫn giữ nguyên nguyên tắc một task một lần push.
- Sau khi push xong task hiện tại mới chuyển sang task tiếp theo.

## Role tổng quát

| Role | Trọng tâm nghiệp vụ |
| --- | --- |
| Admin | Quản lý tài khoản, phase, mentor, student, company, assignment, approval, report tổng hợp |
| Mentor | Theo dõi sinh viên được phân công, review báo cáo tuần, chấm điểm theo rubric |
| Student | Đăng ký thực tập, xem phân công, nộp báo cáo tuần, xem kết quả đánh giá |

## Checklist chất lượng chung

- Có migration hoặc hướng dẫn schema rõ ràng.
- Có DTO request/response riêng, không expose entity trực tiếp.
- Có validation bằng `jakarta.validation`.
- Có phân quyền bằng `@PreAuthorize`.
- Có Swagger annotation cho API quan trọng.
- Có xử lý lỗi business rõ ràng: duplicate, not found, access denied, invalid state.
- Frontend có loading, empty state, error state.
- Frontend không render nút thao tác nếu role không có quyền.
- Build backend và frontend đều chạy thành công sau mỗi lát cắt.

## Addendum: Tasks 20-25

Roadmap bo sung de cover cac uu tien con thieu sau review:

20. `20-role-scoped-production-dashboard-and-refresh.md`
21. `21-workflow-integrity-concurrency-and-state-guards.md`
22. `22-realtime-notification-reliability-and-deadline-intelligence.md`
23. `23-detail-discovery-filter-pagination-server-side.md`
24. `24-critical-business-flow-test-suite.md`
25. `25-admin-operational-analytics-and-reporting.md`

Rule bat buoc cho cac task moi van giu nguyen:

- doc `docs/rule/http-error-response-rule.md`
- doc `docs/rule/skill-query-rule.md`
- doc `docs/rule/library-reuse-rule.md`
- doc `docs/rule/frontend-ui-configuration-rule.md` khi co UI
- chay skill discovery truoc khi implement
- uu tien reuse thu vien/skill co san truoc khi code moi
- moi task phai co commit va push rieng

## Scale Roadmap Addendum

Roadmap nay bo sung cho muc tieu scale lon, sap xep theo thu tu uu tien da de xuat:

### Phase 1 - Security, Auth, Database, Backend

26. `26-security-and-rls-data-scope.md`
27. `27-auth-and-permissions-foundation.md`
28. `28-database-and-storage-hardening.md`
29. `29-apis-and-backend-logic-contracts.md`

### Phase 2 - Observability, Performance, Resilience

30. `30-error-tracking-and-logs-observability.md`
31. `31-caching-and-cdn-strategy.md`
32. `32-rate-limiting-and-abuse-protection.md`
33. `33-load-balancing-and-horizontal-scaling.md`
34. `34-availability-and-recovery.md`

### Phase 3 - Delivery And Runtime

35. `35-cicd-and-version-control-governance.md`
36. `36-hosting-and-deployment-strategy.md`
37. `37-cloud-and-compute-resource-plan.md`
38. `38-frontend-scale-hardening-last.md`

### Rule su dung cho scale roadmap

- Security va auth phai lam truoc moi UI polish.
- Database va backend phai co truoc cache, rate limit va scaling.
- Error tracking/logs phai co truoc khi toi uu performance.
- Availability/recovery phai co truoc hosting cloud finalization.
- Frontend la lop cuoi, chuyen sang dong bo voi backend thay vi hardcode.
- Moi task van phai chay skill discovery phu hop va uu tien reuse skill/thu vien san co.

## Remediation Roadmap: Tasks 39-53

Danh sach nay duoc tao tu ban review code thuc te cua tasks 20-38. Day la cac task bo sung de dong cac gap, khong lap lai phan da dat.

### Phase 1 - Workflow, Product And Test Gaps

39. `39-workflow-ui-state-action-guards.md`
40. `40-notification-realtime-center-and-entity-deeplinks.md`
41. `41-detail-routes-sorting-and-drilldown.md`
42. `42-test-pyramid-and-frontend-quality-gates.md`
43. `43-admin-analytics-reporting-export.md`

### Phase 2 - Security, Auth, Database And API

44. `44-security-headers-policy-and-read-audit.md`
45. `45-auth-hardening-secret-rotation-and-lockout.md`
46. `46-production-database-storage-migrations.md`
47. `47-api-contract-versioning-and-idempotency.md`

### Phase 3 - Observability, Performance And Resilience

48. `48-observability-structured-logging-and-error-tracking.md`
49. `49-cache-cdn-and-client-query-strategy.md`
50. `50-rate-limit-and-abuse-protection.md`
51. `51-scaling-scheduler-and-graceful-runtime.md`
52. `52-availability-recovery-and-resilience.md`

### Phase 4 - Delivery, Cloud And Frontend Last Mile

53. `53-cicd-deployment-cloud-and-frontend-quality.md`

## Rule rieng cho remediation tasks

- Bat dau bang viec doc ban review va xac nhan gap bang code/test hien tai; khong sua phan da dat neu khong co regression.
- Task 44-47 phai duoc uu tien truoc cache, scaling va deployment production.
- Task 42 phai duoc cap nhat dan cung moi task de moi regression co test tuong ung.
- Truoc khi them dependency, kiem tra `.agents/plugins`, `_agents/plugins`, `~/.gemini/config/plugins`, skill local va MCP theo `docs/rule/plugin-and-mcp-usage-rule.md`.
- Moi task phai co commit rieng, chi commit file thuoc task, chay verification va push thanh cong moi chuyen task tiep theo.
- Khong commit secret, `.env`, token OAuth, build artifact, log, `node_modules` hoac thu muc `target`.
