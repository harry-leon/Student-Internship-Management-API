# Frontend UI Configuration Rule

Rule n?y b?t bu?c ?p d?ng cho t?t c? task frontend ti?p theo c?a Internship Management System.

## M?c ti?u

Frontend kh?ng ???c c?u h?nh giao di?n l? t? trong t?ng page/component. T?t c? c?u h?nh d?ng chung ph?i ?i qua c?c file config v? UI primitive ?? c? trong `FE/src`.

Agent khi l?m task frontend ph?i ?u ti?n reuse c?c file ?? ???c t?o t? task refactor frontend config:

```text
FE/src/config/permissions.config.ts
FE/src/config/theme.config.ts
FE/src/config/ui.config.ts
FE/src/config/layout.config.ts
FE/src/config/navigation.config.ts
FE/src/config/routes.config.tsx
FE/src/components/ui/PageContainer.tsx
FE/src/components/ui/PageHeader.tsx
FE/src/components/ui/Card.tsx
FE/src/components/ui/Button.tsx
FE/src/components/ui/Badge.tsx
FE/src/components/ui/index.ts
FE/src/components/Can.tsx
```

N?u file tr?n kh?ng t?n t?i ? branch hi?n t?i, agent ph?i ki?m tra l?ch s?/branch ho?c t?o l?i theo ??ng chu?n n?y tr??c khi vi?t page m?i.

## Nguy?n t?c b?t bu?c

1. Kh?ng hardcode style d?ng chung trong t?ng page.
2. Kh?ng hardcode menu theo role.
3. Kh?ng hardcode permission string r?i r?c trong component.
4. Kh?ng t?o button/card/badge/page wrapper m?i n?u UI primitive hi?n t?i ??p ?ng ???c.
5. Kh?ng s?a ri?ng m?t page theo style kh?c n?u ??y l? pattern d?ng chung.
6. Light theme v? dark theme ph?i c?ng ?i qua token/config chung.
7. Menu, route v? action ph?i d?ng c?ng permission source of truth.

## File config l? source of truth

### `permissions.config.ts`

D?ng ?? khai b?o permission code t?p trung.

Kh?ng vi?t tr?c ti?p:

```tsx
<Can permission="STUDENT_CREATE">
```

Ph?i d?ng:

```tsx
<Can permission={PermissionCode.STUDENT_CREATE}>
```

N?u c?n th?m permission m?i, th?m v?o `PermissionCode` tr??c, sau ?? m?i d?ng trong route/menu/action.

### `theme.config.ts`

D?ng ?? qu?n l? m?u s?c, role badge, status badge v? button variant.

Kh?ng l?p l?i c?c class m?u nh?:

```text
bg-emerald-50 text-emerald-700
bg-rose-50 text-rose-700
bg-[#f3e8ff] text-[#6b21a8]
```

Ph?i ??a v?o `themeConfig.status`, `themeConfig.roles`, ho?c component `<Badge />`.

### `ui.config.ts`

D?ng ?? qu?n l? radius, spacing, typography, density v? grid.

Kh?ng l?p l?i c?c class nh?:

```text
rounded-2xl
p-8
text-3xl
grid-cols-1 sm:grid-cols-2 xl:grid-cols-4
```

N?u l? style l?p l?i, ??a v?:

```text
uiConfig.radius
uiConfig.spacing
uiConfig.typography
uiConfig.density
uiConfig.grid
```

### `layout.config.ts`

D?ng ?? qu?n l? k?ch th??c layout ch?nh:

```text
sidebar width
header height
content max width
content padding
content offset
```

Kh?ng hardcode l?i:

```text
w-[228px]
lg:pl-[228px]
h-[56px]
pt-[56px]
max-w-[1480px]
```

### `navigation.config.ts`

Sidebar/Menu ph?i render t? config n?y.

Kh?ng vi?t menu tr?c ti?p trong `Sidebar.tsx` theo role:

```tsx
if (role === "ADMIN") {
  return adminMenu;
}
```

Ph?i d?ng:

```ts
{
  label: "Students",
  path: "/students",
  icon: "school",
  requiredPermissions: [PermissionCode.STUDENT_VIEW],
}
```

Role n?o kh?ng c? permission th? kh?ng th?y menu.

### `routes.config.tsx`

Route ph?i khai b?o t?p trung v? g?n permission r? r?ng.

Kh?ng t?o route l? t? trong nhi?u file n?u route ?? thu?c app ch?nh.

Route m?i ph?i theo resource-based URL:

```text
/dashboard
/users
/students
/mentors
/companies
/groups
/groups/:groupId
/groups/:groupId/tasks
/tasks
/submissions
/assessment-results
/settings/roles
/settings/permissions
```

Kh?ng d?ng URL theo role:

```text
/admin/*
/mentor/*
/student/*
```

N?u c?n backward compatibility, route c? ch? ???c redirect sang route m?i.

## UI primitive b?t bu?c reuse

### Page wrapper

Page m?i ph?i d?ng:

```tsx
<PageContainer>
  <PageHeader title="..." subtitle="..." />
  ...
</PageContainer>
```

Kh?ng t? t?o wrapper page v?i padding/max-width ri?ng n?u kh?ng c? l? do r? r?ng.

### Card/Panel

D?ng:

```tsx
<Card>
  ...
</Card>
```

Kh?ng t? hardcode:

```tsx
<div className="bg-white rounded-2xl shadow-lg p-8">
```

### Button

D?ng component Button d?ng chung n?u ?? c?:

```tsx
<Button variant="primary" size="md">
```

Button m?i ph?i c? state hover/focus/disabled v? dark mode ph? h?p.

### Badge

D?ng:

```tsx
<Badge status={status} />
<Badge role={role} />
```

Kh?ng t?o badge m?u ri?ng trong t?ng page.

### Permission wrapper

D?ng:

```tsx
<Can permission={PermissionCode.SUBMISSION_GRADE}>
  <Button>Ch?m b?i</Button>
</Can>
```

Kh?ng render action/button r?i ch? ch?n khi click.

## Dark theme

M?i component/page m?i ph?i h? tr? dark theme ngay t? ??u.

Kh?ng ???c th?m c?c class n?n s?ng m? kh?ng c? dark variant ho?c semantic token:

```text
bg-white
bg-gray-50
bg-slate-50
text-black
text-slate-900
border-gray-100
border-gray-200
```

N?u c?n d?ng class tr?c ti?p, ph?i c? dark counterpart:

```tsx
className="bg-white text-slate-900 border-slate-200 dark:bg-slate-900 dark:text-slate-100 dark:border-slate-700"
```

T?t h?n l? d?ng UI primitive/token chung:

```tsx
<Card>
```

Dark theme ph?i ??m b?o:

- text ch?nh d? ??c
- text ph? kh?ng qu? m?
- border th?y ???c nh?ng kh?ng g?y r?i
- input/select/table kh?ng b? n?n tr?ng trong dark mode
- status color kh?ng neon qu? g?t
- focus state r? khi d?ng keyboard

## Content density

Giao di?n l? admin/operation portal, n?n ?u ti?n hi?n th? nhi?u th?ng tin, ?t l?n chu?t.

Page m?i ph?i:

- gi? header/sidebar k?ch th??c ?n ??nh
- content compact h?n hero/landing page
- card kh?ng qu? l?n
- table/list hi?n th? nhi?u d?ng h?n tr?n desktop
- desktop n?n c? nhi?u column h?n khi d?ng card/list
- mobile kh?ng b? v? layout

D?ng grid token trong `ui.config.ts` thay v? hardcode grid ri?ng.

## RBAC ??ng b? v?i UI

Khi th?m menu, route, page ho?c action m?i, b?t bu?c l?m ?? 4 l?p:

1. Permission code trong `permissions.config.ts`.
2. Menu item trong `navigation.config.ts` n?u c?n hi?n tr?n sidebar.
3. Route config trong `routes.config.tsx`.
4. Button/action b?c b?ng `<Can />` n?u l? thao t?c c? quy?n ri?ng.

Frontend ch? ?n UI ?? ??ng tr?i nghi?m. Backend v?n ph?i enforce authorization ri?ng.

## Quy tr?nh b?t bu?c tr??c khi s?a frontend

Tr??c khi implement task frontend, agent ph?i ??c:

```powershell
Get-Content docs\rule\frontend-ui-configuration-rule.md
Get-Content docs\rule\library-reuse-rule.md
Get-Content docs\rule\skill-query-rule.md
Get-Content FE\package.json
Get-ChildItem FE\src\config
Get-ChildItem FE\src\components\ui
```

Sau ?? ph?i search ?? tr?nh t?o tr?ng l?p:

```powershell
rg -n "PageContainer|PageHeader|themeConfig|uiConfig|layoutConfig|PermissionCode|navigationSections|routeConfigs" FE\src
```

## Verification b?t bu?c

Sau khi s?a frontend, ch?y:

```powershell
cd FE
npm run lint
npm run build
```

N?u task c? thay ??i theme/layout, ph?i ki?m tra t?i thi?u:

```text
/dashboard
/users
/students
/mentors
/companies
/groups
/tasks
/submissions
/assessment-results
/settings/roles
/settings/permissions
```

Ki?m tra c? light theme v? dark theme.

## K?t qu? b?n giao b?t bu?c

Final note c?a agent ph?i ghi r?:

- File config ?? reuse ho?c c?p nh?t.
- UI primitive ?? reuse.
- Page/component ?? refactor.
- Permission/menu/route/action ?? ??ng b? ra sao.
- Hardcode style n?o ?? lo?i b?.
- K?t qu? `npm run lint`.
- K?t qu? `npm run build`.
- C?c ?i?m c?n hardcode n?u ch?a x? l? ???c v? l? do.

## Plugin aware reuse

- Neu workspace plugin cung cap shared UI config, design tokens hoac primitives, phai uu tien dung plugin-and-mcp-usage-rule.md va reuse no truoc khi tao config rieng trong FE/src.
- Khong nhan ban source of truth cua plugin sang file rieng neu plugin da co san.
