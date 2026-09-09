# Frontend Content Density va Layout Standardization

## Skill Nen Dung

- Bat buoc dung skill local `internship-management-system` trong `SKILL.md`.
- Truoc khi code, chay `npx skills find "react dashboard table layout responsive design frontend testing"`.
- Neu co skill tot ve React UI/testing, cai va doc truoc khi code.

## Muc tieu

Toi uu kich thuoc noi dung tren cac trang admin/mentor/student de hien thi nhieu thong tin hon tren desktop, giam cuon trang, nhung giu sidebar/header hien tai vi kich thuoc dang on.


## Vai tro thiet ke bat buoc

Khi thuc hien task nay, agent phai dong vai mot Frontend Designer/Frontend Engineer co nhieu nam kinh nghiem thiet ke dashboard va operational system. Khong chi thu nho component cho vua man hinh, ma phai thiet ke lai content area de giao dien:

- Dep, gon, sang va co tinh san pham that.
- De scan du lieu trong moi ngay lam viec.
- Uu tien hieu qua thao tac hon trang tri.
- Nhat quan spacing, typography, border, shadow, badge, table row va action button giua cac trang.
- Giam cam giac card qua to/hero qua lon trong admin dashboard.
- Hien thi duoc nhieu thong tin hon tren desktop nhung khong lam UI bi chat, roi mat hoac kho doc.

Agent phai tu danh gia UI nhu mot nguoi thiet ke FE senior truoc khi final:

- Neu mot section chi co vai con so/text ngan, khong duoc dung card qua cao.
- Neu page la management/list, desktop phai uu tien table hoac compact grid thay vi card lon.
- Neu co nhieu action, action phai ro uu tien chinh/phu va khong lam row cao bat thuong.
- Mau sac phai tiet che, phuc vu status/data, khong lam page thanh landing page.
- Moi man hinh phai co visual hierarchy ro: title, filter/action, data, status/empty/error.

## Pham vi

Giu nguyen:

- Sidebar/menu size.
- Header/topbar size.
- Routing va role menu hien co.

Chinh:

- Kich thuoc page content.
- Card/table spacing.
- Grid desktop co nhieu column hon.
- Typography trong content.
- Chieu cao row va padding trong table/card.
- Detail modal/page width phu hop.

## Van de UI hien tai

- Nhieu card dashboard va section dang qua lon so voi luong thong tin.
- Mot hang desktop dang it cot, gay lang phi chieu ngang.
- Heading trong content qua lon, day noi dung xuong duoi.
- Card padding/rounded/shadow lam page trong nhu landing page hon la operational dashboard.
- Table/detail chua dong bo kich thuoc giua cac trang.

## Design standard de xuat

### Page container

- Content max width nen theo `max-w-none` hoac `max-w-[1600px]` thay vi layout qua hep.
- Padding desktop: `px-6 py-5` hoac `px-8 py-6`.
- Padding mobile: `px-4 py-4`.
- Khoang cach section: `gap-4` hoac `gap-5`, tranh `gap-8` neu khong can.

### Typography

| Element | Size de xuat |
| --- | --- |
| Page title | `text-2xl` hoac `text-3xl` toi da |
| Section title | `text-lg` hoac `text-xl` |
| Card label | `text-xs` / `text-sm` |
| Table cell | `text-sm` |
| Metadata | `text-xs` |

Khong dung hero-scale text trong dashboard/admin pages.

### Cards

- Border radius toi da `rounded-lg` hoac `rounded-xl`, khong nen `rounded-3xl` cho operational pages.
- Padding card desktop: `p-4`, card compact: `p-3`.
- Dashboard KPI card nen cao khoang 120-140px, khong qua 180px.
- Khong long card trong card neu khong can.

### Grid desktop

- KPI dashboard: 4 cot tren desktop, 2 cot tablet, 1 cot mobile.
- List cards nho: 3-4 cot desktop neu noi dung ngan.
- Management pages nen uu tien table thay vi card tren desktop.
- Filter bar nen gom tren 1 hang desktop: search, role/status/select, action button.

Vi du:

```tsx
<div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
```

Voi card thong tin nho:

```tsx
<div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-4">
```

### Table standard

- Header row: `h-10`, cell padding `px-3 py-2`.
- Data row: `min-h-12`, khong de card row qua cao.
- Action buttons dung icon + tooltip neu co nhieu action.
- Sticky table header neu list dai.
- Desktop hien nhieu column; mobile co the collapse thanh card compact.

## Trang can uu tien chinh

1. `DashboardView.tsx`: KPI grid, chart sections, list workload/company distribution.
2. `StudentsView.tsx`: table desktop, compact filters, action detail.
3. `AssignmentsView.tsx`: them status/submission summary, compact row.
4. `AssessmentResultsView.tsx`: table grading results, detail action.
5. `WeeklyReportsView.tsx`: list/report row gon hon.
6. `CompaniesView.tsx`, `MentorsView.tsx`, `UsersView.tsx`: dong bo table/card density.

## Implementation plan

1. Tao hoac chuan hoa cac utility class/component chung neu repo da co pattern:
   - `PageHeader`
   - `Toolbar`
   - `DataTable`
   - `StatusBadge`
   - `CompactCard`
2. Chinh dashboard truoc vi tac dong lon nhat toi screenshot hien tai.
3. Chinh cac management pages theo cung spacing token.
4. Kiem tra desktop 1366px, 1600px, 1920px; mobile 390px.
5. Dam bao text khong overlap va button khong bi tran.

Neu project chua co component chung, co the chinh truc tiep tung view truoc. Chi tach component khi da lap lai it nhat 2-3 lan.

## Acceptance criteria

- Header/sidebar khong bi thay doi kich thuoc.
- UI sau khi chinh phai dep hon, gon hon, nhat quan hon va co cam giac duoc thiet ke boi FE designer co kinh nghiem.
- Dashboard desktop hien 4 KPI tren cung mot hang.
- Management pages desktop hien table nhieu cot thay vi card qua lon.
- Giam vertical spacing, it cuon hon nhung van doc ro.
- Mobile van khong overlap text/button.
- `npm run lint` va `npm run build` pass.

## GitHub Push Sau Khi Hoan Thanh Task

Sau khi hoan thanh task `09-frontend-content-density-standardization`, commit va push rieng task nay.

```powershell
git status
cd FE
npm run lint
npm run build
git add <cac-file-ui-density-da-sua>
git commit -m "style: standardize content density across pages"
git push origin <branch-name>
```

Khong gom backend submission hoac detail API vao commit nay neu chua nam trong task.