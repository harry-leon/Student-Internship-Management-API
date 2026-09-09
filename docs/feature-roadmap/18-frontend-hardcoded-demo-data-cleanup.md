# Task 18 - Frontend Hardcoded Demo Data Cleanup

## Skill n?n d?ng

- B?t bu?c d?ng skill local `internship-management-system` trong `SKILL.md` n?u t?n t?i.
- B?t bu?c ??c v? ?p d?ng:

```powershell
Get-Content docs\rule\frontend-ui-configuration-rule.md
Get-Content docs\rule\http-error-response-rule.md
Get-Content docs\rule\library-reuse-rule.md
Get-Content docs\rule\skill-query-rule.md
```

- Tr??c khi code, ch?y skill discovery:

```powershell
npx skills find "react frontend mock data cleanup api integration dashboard empty state testing"
```

- N?u c? skill ph? h?p v? React/API integration/testing, c?i v? ??c `SKILL.md` tr??c khi implement.

## Library reuse va server-state decision

Agent phai doc docs/rule/library-reuse-rule.md va danh gia thu vien truoc khi tu viet lai fetch/cache/refetch logic.

Khuyen nghi cho task 18:

- Neu nhieu page can loading/error/cache/refetch sau mutation/polling/permission reload, uu tien danh gia @tanstack/react-query thay vi tu code useEffect/useState lap lai o tung page.
- Dung query key ro rang theo resource: students, mentors, companies, groups, groupTasks, submissions, assignments, dashboardSummary, permissions.
- Sau mutation create/update/delete phai invalidate query lien quan hoac optimistic update co rollback.
- Dashboard KPI va danh sach can moi phai dung refetchInterval hop ly neu chua co realtime. Khong hardcode data de che API chua co.
- Chi danh gia @tanstack/react-table khi table can sorting/filtering/column visibility/resizing phuc tap o client. Neu server-side pagination don gian thi khong can them.
- Form phuc tap co the danh gia react-hook-form va zod, nhung khong bat buoc neu component/form hien tai da du.

Can kiem tra truoc khi code:

```powershell
Get-Content FE\package.json
rg -n "fetch\(|axios|apiClient|useEffect\(|setInterval|mock|demo|fallback|INITIAL_|Math\.random|Date\.now" FE\src -S
```

Neu them @tanstack/react-query, phai tao query client/provider mot lan o app root, khong tao client moi trong page. Final note phai liet ke query keys, invalidation sau mutation va polling interval dang dung.
## B?i c?nh

Frontend hi?n c?n nhi?u ch? ?ang hi?n th? d? li?u demo, fallback gi? ho?c hardcode m?c ??nh. M?t s? ph?n c? th? ch?p nh?n l? c?u h?nh UI, nh?ng nhi?u ph?n ?ang l?m ng??i d?ng hi?u nh?m r?ng d? li?u ?? ??n t? database.

M?c ti?u task n?y l? d?n c?c d? li?u demo/hardcode g?y sai nghi?p v?, n?i d? li?u th?t t? API n?u ?? c? endpoint, ho?c thay b?ng loading/empty/error state ??ng chu?n n?u backend ch?a h? tr?.

## M?c ti?u

1. Kh?ng c?n hi?n th? d? li?u demo nh? d? li?u th?t.
2. Kh?ng t? t?o sinh vi?n, mentor, company, ?i?m, phase, deadline, avatar b?ng fallback gi? n?u API kh?ng tr?.
3. Kh?ng truy?n `[]` v?o component c?n d? li?u th?t n?u c? th? load t? API.
4. Kh?ng g?i API b?ng id hardcode nh? `1` khi t?o assignment/student.
5. Mapper kh?ng ???c t? b?a d? li?u nghi?p v? quan tr?ng.
6. C?c page thi?u API ph?i hi?n th? empty state ho?c th?ng b?o backend ch?a h? tr?, kh?ng render demo data.
7. Gi? l?i nh?ng hardcode l? c?u h?nh UI h?p l? nh? enum status/filter n?u kh?ng c?n query database.
8. Sau khi cleanup, admin/mentor/student nh?n th?y d? li?u ??ng scope permission.
9. C?c trang quan tr?ng ph?i c?p nh?t d? li?u sau thao t?c ho?c theo c? ch? realtime/auto refresh ph? h?p, kh?ng b?t ng??i d?ng reload browser m?i th?y d? li?u m?i.

## Ph?n lo?i hardcode

Agent ph?i ph?n lo?i t?ng ch? t?m ???c th?nh 1 trong 4 nh?m:

| Nh?m | ? ngh?a | C?ch x? l? |
| --- | --- | --- |
| Demo data | T?n ng??i/company/?i?m/phase/task gi? render ra UI | B? ho?c thay b?ng API th?t |
| Unsafe fallback | API l?i/empty nh?ng UI t? b?a d? li?u | Thay b?ng loading/empty/error state |
| Temporary dev helper | Quick fill account, mock OAuth, demo avatar | Ch? gi? trong dev mode ho?c x?a kh?i production |
| Valid UI config | Status enum, tab list, route label, icon name | C? th? gi?, nh?ng n?n ??a v?o config n?u l?p l?i |

## C?c v? tr? ?? ph?t hi?n c?n x? l?

### 1. `FE/src/data/mockData.ts`

File n?y ch?a nhi?u d? li?u demo:

- `INITIAL_ASSIGNMENTS`
- `INITIAL_PHASES`
- `INITIAL_ROUNDS`
- `INITIAL_STUDENTS`
- `INITIAL_MENTORS`
- `INITIAL_USERS`
- `INITIAL_CRITERIA`
- avatar/link ?nh demo
- t?n sinh vi?n, mentor, company, phase demo

Y?u c?u:

- Ki?m tra file n?y c? c?n ???c import kh?ng.
- N?u kh?ng d?ng, x?a file.
- N?u c?n d?ng, thay n?i d?ng b?ng API th?t ho?c empty state.
- Kh?ng ?? mock data n?y render trong production UI.

### 2. `FE/src/App.tsx`

?? ph?t hi?n nhi?u component ?ang nh?n m?ng r?ng:

```tsx
<DashboardView assignments={[]} students={[]} mentors={[]} />
<UsersView users={[]} onRefreshData={() => {}} />
<AssessmentResultsView students={[]} />
<EvaluationCriteriaView criteria={[]} />
<CommandPaletteModal students={[]} mentors={[]} />
<QuickActionModal mentors={[]} />
```

Y?u c?u:

- Kh?ng truy?n `[]` n?u component c?n search/ch?n d? li?u th?t.
- Load d? li?u t? API/service t??ng ?ng ho?c ?? ch?nh page t? fetch nh?t qu?n.
- `CommandPaletteModal` ph?i c? d? li?u th?t ho?c ch? hi?n th? navigation n?u ch?a c? search data.
- `QuickActionModal` ph?i nh?n students/mentors/phases th?t ho?c b? modal n?u flow ?? c? page CRUD ri?ng.

V?n ?? nguy hi?m c?n s?a:

```ts
studentId: Number(newAsg.id) || 1,
mentorId: 1,
phaseId: Number(activePhase.id) || 1,
```

Kh?ng ???c t?o assignment b?ng id hardcode. Form ph?i ch?n `studentId`, `mentorId`, `phaseId` th?t t? API.

### 3. Active phase fallback trong `App.tsx`

Hi?n c? fallback:

```ts
name: 'Ch?a c? ??t th?c t?p',
term: 'Spring 2026',
startDate: '2026-01-01',
endDate: '2026-05-30',
```

Y?u c?u:

- Kh?ng d?ng phase gi? nh? d? li?u th?t.
- N?u kh?ng c? active phase, hi?n th? empty state ho?c CTA c?u h?nh phase cho user c? quy?n.
- Dashboard kh?ng ???c render KPI d?a tr?n fallback phase gi?.

### 4. `FE/src/components/QuickActionModal.tsx`

?? ph?t hi?n:

- default `Fall 2026`
- default department `Software Engineering`
- avatar Unsplash hardcode
- mentor fallback `Dr. Nguyen Van Minh`
- company fallback `FPT Software`
- local id `asg-${Date.now()}` / `std-${Date.now()}`
- email fallback `${code}@university.edu.vn`
- t?o object local r?i g?i l?n App x? l? ti?p

Y?u c?u:

- Kh?ng t?o d? li?u local gi? cho flow ch?nh.
- N?u gi? QuickActionModal, refactor th?nh form d?ng id th?t t? API.
- N?u backend ch?a h? tr? ??, disable action v? hi?n th? message ??ng chu?n.
- Kh?ng d?ng avatar/company/mentor/phase fallback gi?.

### 5. `FE/src/views/AssessmentResultsView.tsx`

?? ph?t hi?n fallback demo:

- `Nguyen Van A`
- `Tran Thi C`
- `FPT Software`
- `Viettel Telecom`
- `Spring 2026`
- score `8.7`, `9.2`
- KPI hardcode `8.72`, `64.2%`, `78.5%`
- progress/score t? t?nh gi? t? `Student.score`

Y?u c?u:

- B? fallback list demo.
- N?u API kh?ng c? d? li?u, hi?n th? empty state.
- KPI ph?i l?y t? API aggregate th?t ho?c ?n n?u backend ch?a c?.
- Student ch? xem k?t qu? ?? publish c?a m?nh/group m?nh.
- Mentor ch? xem/ch?m trong scope group m?nh n?u c? permission.
- Admin xem to?n h? th?ng.

### 6. `FE/src/components/dashboard/MentorDashboard.tsx`

?? ph?t hi?n danh s?ch b?o c?o c?n review hardcode:

- `Nguyen Van A`
- `Tran Thi C`
- tu?n 2
- title b?o c?o demo

Y?u c?u:

- Thay b?ng API th?t t? weekly reports/submissions/tasks n?u ?? c?.
- N?u ch?a c? API, hi?n th? empty state: `Ch?a c? b?o c?o c?n review`.
- Kh?ng render danh s?ch demo.

### 7. `FE/src/components/dashboard/StudentDashboard.tsx`

?? ph?t hi?n d? li?u hardcode:

- status `?ang Th?c T?p`
- deadline `17:00 Ch? Nh?t`
- timeline tu?n 1/2/3 hardcode
- company `FPT Software`
- v? tr? `Fullstack Java/React Intern`
- mentor doanh nghi?p `Anh Tr?n Minh Ho?ng`

Y?u c?u:

- Thay b?ng API th?t: profile, group tasks, submissions, assignment/company n?u c?.
- N?u backend ch?a c?, hi?n th? empty state r?.
- Kh?ng t? n?i student ?ang th?c t?p n?u database kh?ng tr? tr?ng th?i ??.

### 8. `FE/src/api/mappers.ts`

?? ph?t hi?n mapper t? g?n d? li?u nghi?p v?:

- Student: phase/mentor/company/status/progress m?c ??nh.
- Mentor: `maxCapacity: 15`, `rating: 5.0`.
- User: department `H? th?ng`, lastActive `V?a xong`.
- Phase: term `Spring 2026`, progress `50`, counts `0`.
- Assignment: company `Doanh nghi?p th?c t?p`, project `?? t?i th?c t?p`, date fallback `2026-01-15`.
- Criterion: category `??nh gi? chung`, weight `20`.

Y?u c?u:

- Mapper ch? map d? li?u th?t t? DTO.
- Field backend ch?a tr? th? d?ng `null`, `undefined`, ho?c text `Ch?a c?p nh?t` ? UI.
- Kh?ng t? b?a s? li?u nh? progress, score, rating, max capacity n?u backend kh?ng tr?.
- N?u UI c?n field ??, t?o issue/note backend c?n b? sung DTO/API.

### 9. `FE/src/views/InternshipApplicationsView.tsx`

V?n ?? nghi?p v?:

- V?n c? flow `Student t?o ??n ??ng k? th?c t?p`.
- Scope m?i c?a h? th?ng l? doanh nghi?p/qu?n tr? qu?n l? th?c t?p sinh, kh?ng c?n student self-registration portal.
- Dropdown mentor review hardcode id `1`, `2` v? t?n GV.

Y?u c?u:

- Theo task 16, b? ho?c ?n flow student self-registration n?u kh?ng c?n ??ng scope.
- N?u v?n c?n application approval cho admin n?i b?, refactor th?nh workflow ??ng nghi?p v? doanh nghi?p.
- Mentor dropdown ph?i l?y t? mentor API th?t.
- Kh?ng hardcode mentor id.

### 10. `FE/src/context/AuthContext.tsx`

?? ph?t hi?n fallback quy?n:

- Admin c? t?t c? quy?n n?u capabilities ch?a load.
- Feature flag m?c ??nh enabled n?u ch?a c? capabilities.

Y?u c?u:

- Ph?i ph? h?p v?i task 13 Dynamic RBAC.
- Kh?ng d?ng fallback m? to?n quy?n trong production.
- N?u capabilities ?ang loading, UI n?n ? tr?ng th?i loading/skeleton thay v? assume c? quy?n.
- Feature flag ch?a load th? kh?ng n?n m?c ??nh b?t c?c t?nh n?ng nh?y c?m.

### 11. `FE/src/views/LoginView.tsx`

?? ph?t hi?n:

- quick fill t?i kho?n `mentor1/mentor123`, `student1/student123`
- OAuth demo t?o email random `student.google${Math.random()}@fpt.edu.vn`

Y?u c?u:

- Ch? hi?n th? quick fill/demo login trong dev mode.
- Kh?ng ?? production UI t?o OAuth fake user/random email.
- N?u Google OAuth ch?a s?n s?ng, button ph?i d?ng flow OAuth th?t ho?c disabled v?i th?ng b?o r?.

### 12. `FE/src/components/Sidebar.tsx`

?? ph?t hi?n footer hardcode:

- `Academic Term 2024-2`
- `Active Operational Phase`

Y?u c?u:

- L?y active phase t? API/context n?u c?n hi?n th?.
- N?u kh?ng c? active phase, hi?n th? `Ch?a c? active phase` ho?c ?n block n?y.

## C?c hardcode c? th? gi? t?m

Kh?ng c?n x?a m?y m?c c?c ph?n sau n?u ch?ng l? c?u h?nh UI h?p l?:

- Status filter arrays nh? `['ALL', 'SUBMITTED', 'REVIEWED', ...]`.
- Placeholder input nh? `VD: SE190099`, `https://github.com/...`.
- Route label/icon trong config.
- Public landing static content n?u ??ng scope s?n ph?m.
- `ui-avatars.com` fallback avatar c? th? gi? t?m ??n khi task upload avatar ho?n t?t, nh?ng kh?ng ???c d?ng avatar ?nh ng??i demo t? Unsplash cho d? li?u th?t.

## H??ng th?c hi?n ?? xu?t

### B??c 1 - Qu?t l?i to?n b? frontend

Ch?y c?c l?nh:

```powershell
rg -n "mock|demo|sample|fake|dummy|fallback|hardcode|INITIAL_|Math\.random|Date\.now|Fall 2026|Spring 2026|FPT Software|Viettel|Nguyen Van|Tran Thi|Dr\.|GV\.|mentor1|student1|admin01" FE\src -S
rg -n "students=\{\[\]\}|mentors=\{\[\]\}|assignments=\{\[\]\}|criteria=\{\[\]\}|onRefreshData=\{\(\) => \{\}\}" FE\src -S
rg -n "studentId:\s*1|mentorId:\s*1|phaseId:\s*1|selectedMentorId.*1|progress:\s*[0-9]+|score:\s*[0-9]+|rating:\s*[0-9]+" FE\src -S
```

T?o checklist c?c k?t qu? tr??c khi s?a ?? tr?nh b? s?t.

### B??c 2 - B? mock data kh?ng d?ng

- Ki?m tra import c?a `FE/src/data/mockData.ts`.
- N?u kh?ng c?n import, x?a file.
- N?u c?n import, refactor n?i import tr??c.

### B??c 3 - S?a c?c fallback g?y sai d? li?u

?u ti?n s?a theo th? t?:

1. `App.tsx` hardcode `[]` v? id `1`.
2. `QuickActionModal.tsx` demo object/id/avatar/company.
3. `AssessmentResultsView.tsx` fallback danh s?ch ?i?m gi? v? KPI gi?.
4. `MentorDashboard.tsx` list report demo.
5. `StudentDashboard.tsx` timeline/company/deadline demo.
6. `mappers.ts` field t? b?a d? li?u.
7. `InternshipApplicationsView.tsx` flow sai scope v? mentor hardcode.
8. `AuthContext.tsx` fallback quy?n/feature flag.
9. `LoginView.tsx` demo login/OAuth fake.
10. `Sidebar.tsx` active term hardcode.

### B??c 4 - Thay b?ng API th?t ho?c empty state

M?i n?i cleanup ph?i ch?n m?t h??ng r?:

- N?u API ?? c?: g?i service hi?n c?, reuse DTO/mappers ??ng chu?n.
- N?u API ch?a c?: kh?ng b?a data, hi?n th? empty state ho?c note thi?u API.
- N?u l? dev helper: guard b?ng `import.meta.env.DEV` ho?c bi?n env r? r?ng.

V? d? kh?ng t?t:

```tsx
const displayStudents = apiData.length > 0 ? apiData : demoStudents;
```

V? d? t?t:

```tsx
if (loading) return <LoadingState />;
if (error) return <ErrorState message={error} />;
if (students.length === 0) return <EmptyState title="Ch?a c? d? li?u" />;
```

## Realtime / Auto Refresh

Task n?y kh?ng y?u c?u b?t bu?c ph?i d?ng WebSocket cho m?i trang, nh?ng b?t bu?c ki?m tra v? x? l? c?c ph?n d? li?u c?n c?p nh?t ngay sau thao t?c.

Agent ph?i r? so?t c?c trang ch?nh v? x?c ??nh d? li?u n?o c?n c?p nh?t kh?ng c?n reload:

```text
/dashboard
/students
/mentors
/companies
/groups
/groups/:groupId
/tasks
/submissions
/assignments
/applications
/assessment-results
/settings/permissions
```

Y?u c?u t?i thi?u:

- Sau khi t?o/s?a/x?a entity, list hi?n t?i ph?i t? refresh ho?c update local state ??ng.
- Sau khi t?o task/submission/review/message, tab li?n quan ph?i th?y d? li?u m?i kh?ng c?n reload page.
- Dashboard KPI ph?i refresh sau thao t?c l?m thay ??i s? li?u, ho?c c? n?t refresh r? r?ng n?u ch?a c? realtime.
- Permissions sau khi l?u ph?i reload capabilities/permissions runtime ?? menu/route/action ??ng b? ngay.
- Notification, group room, messages, read receipts, online presence n?u ?? c? ph?i d?ng polling/SSE/WebSocket ph? h?p.
- Kh?ng d?ng d? li?u demo ?? che vi?c API ch?a realtime.

Ph?n lo?i c?ch c?p nh?t:

| Lo?i d? li?u | C?ch x? l? khuy?n ngh? |
| --- | --- |
| CRUD list th??ng | refetch sau mutation ho?c optimistic update c? rollback |
| Dashboard KPI | refetch sau mutation li?n quan, ho?c refresh interval nh? 30-60s |
| Group chat/message | polling 5-10s cho MVP, sau n?y WebSocket/SSE |
| Online presence | heartbeat 30-60s, polling presence kho?ng 30s |
| Read receipts | mark-read batch khi m?/xem message, refetch receipt compact |
| Permission/menu | reload permissions ngay sau khi l?u ph?n quy?n |

N?u backend ch?a c? endpoint realtime/summary c?n thi?t, ph?i ghi r? API c?n thi?u thay v? hardcode d? li?u.

## Backend/API note

N?u ph?t hi?n frontend c?n d? li?u nh?ng backend ch?a c? endpoint/field, kh?ng ???c t? b?a ? frontend. Ph?i ghi r? c?n backend b? sung.

C?c API/DTO c? th? c?n b? sung:

- dashboard mentor review queue
- student dashboard current assignment/company/group/task summary
- assessment aggregate KPI
- active phase summary
- command palette search API
- application approval mentor options
- user last active/department/avatar
- mentor capacity/rating n?u th?t s? c?n hi?n th?

## RBAC v? scope d? li?u

Sau khi cleanup, d? li?u ph?i ??ng scope:

- Admin: th?y to?n h? th?ng.
- Mentor: ch? th?y group/task/submission/student thu?c ph?m vi m?nh qu?n l?.
- Student: ch? th?y profile, group, task, submission, k?t qu? c?a m?nh ho?c group m?nh.

Kh?ng d?ng d? li?u fallback ?? v??t qua tr?ng th?i r?ng ho?c l?i ph?n quy?n.

## UI y?u c?u

- Reuse `PageContainer`, `PageHeader`, `Card`, `Button`, `Badge`, `Can` n?u c?.
- Kh?ng l?m x?u dark theme/task 17.
- Loading/empty/error state ph?i r? v? ??ng b?.
- Kh?ng d?ng `alert` cho l?i ch?nh n?u ?? c? pattern error UI t?t h?n; ?u ti?n error state/toast theo rule hi?n c?.

## Kh?ng ???c l?m

- Kh?ng x?a nh?ng hardcode l? enum/config h?p l? m? kh?ng c? l? do.
- Kh?ng thay mock data b?ng mock data kh?c.
- Kh?ng ?? API l?i r?i render demo.
- Kh?ng hardcode id `1` ?? bypass thi?u d? li?u.
- Kh?ng t? th?m dependency n?u component/service hi?n c? x? l? ???c.
- Kh?ng ??i business logic ngo?i scope cleanup n?u ch?a c?n.

## Verification b?t bu?c

Trong th? m?c `FE`, ch?y:

```powershell
npm run lint
npm run build
```

Ki?m tra th? c?ng ?t nh?t:

```text
/dashboard
/students
/mentors
/companies
/groups
/tasks
/submissions
/assignments
/applications
/assessment-results
/settings/permissions
/login
```

V?i t?ng role:

- Admin kh?ng th?y d? li?u demo khi API empty.
- Mentor dashboard kh?ng c?n report queue fake.
- Student dashboard kh?ng c?n company/deadline/timeline fake.
- Assessment results kh?ng c?n student/score/KPI fake.
- Quick action kh?ng t?o assignment b?ng id hardcode.
- Login production kh?ng hi?n demo OAuth fake/random email.
- Sau khi t?o/s?a/x?a d? li?u ? c?c trang CRUD, UI c?p nh?t ngay kh?ng c?n reload browser.
- Sau khi l?u ph?n quy?n, menu/route/action c?a role hi?n t?i ??ng b? ngay.
- Group room/chat/task/submission n?u c? d? li?u m?i ph?i c? polling/refetch ho?c c? ch? realtime ph? h?p.

## K?t qu? c?n b?n giao

Agent ph?i tr? v?:

1. Rule ?? ??c.
2. Skill query ?? ch?y v? skill ?? d?ng/c?i n?u c?.
3. Danh s?ch hardcode/demo ?? x? l?.
4. Danh s?ch hardcode ???c gi? l?i v? l? do.
5. File ?? x?a n?u c?, ??c bi?t `mockData.ts`.
6. File ?? refactor ?? d?ng API th?t ho?c empty state.
7. API/DTO backend c?n thi?u n?u frontend ch?a th? x? l? th?t.
8. Danh s?ch trang/d? li?u ?? ki?m tra realtime ho?c auto refresh, c? ch? ?ang d?ng: refetch, optimistic update, polling, SSE ho?c WebSocket.
9. Nh?ng ph?n v?n c?n reload ho?c ch?a realtime ???c v? l? do.
10. K?t qu? `npm run lint`.
11. K?t qu? `npm run build`.
12. Commit hash v? tr?ng th?i push GitHub cho task 18.
