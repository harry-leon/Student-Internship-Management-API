# Task 17 - System-wide Dark Theme Standardization

## Skill n?n d?ng

- B?t bu?c d?ng skill local `internship-management-system` trong `SKILL.md` n?u t?n t?i.
- B?t bu?c ??c v? ?p d?ng `docs/rule/frontend-ui-configuration-rule.md` tr??c khi ch?nh UI.
- Tr??c khi code, ch?y skill discovery:

```powershell
npx skills find "tailwind dark mode design system react dashboard accessibility contrast"
```

- N?u c? skill t?t v? Tailwind/design system/accessibility, c?i v? ??c `SKILL.md` tr??c khi code.
- T?i thi?u ph?i ?p d?ng t? duy c?a c?c skill:
  - `tailwind-design-system`: design token, semantic token, dark variant, component variants.
  - `frontend-design`: giao di?n dashboard th?c t?, d? scan, t??ng ph?n t?t, kh?ng l?m UI ki?u landing page.

## Library reuse bat buoc

Agent phai doc docs/rule/library-reuse-rule.md va ghi Library decision truoc khi sua UI.

Khuyen nghi cho task 17:

- Khong them UI/theme library moi mac dinh. Dark theme nen xu ly bang Tailwind CSS v4, CSS variables va config chung hien co.
- Reuse FE/src/config/theme.config.ts, FE/src/config/ui.config.ts, FE/src/config/layout.config.ts, index.css va UI primitives truoc khi sua tung page.
- Reuse lucide-react cho icon va motion da co cho transition nho neu can.
- Neu class conditional qua lap lai, moi duoc danh gia them clsx, tailwind-merge, class-variance-authority. Phai ghi ro ly do, file nao bi lap, bundle/maintenance risk va ket qua build.
- Khong dung theme package/component library lon chi de doi mau dark mode.

Can kiem tra truoc khi code:

```powershell
Get-Content FE\package.json
rg -n "themeConfig|uiConfig|layoutConfig|dark:|data-theme|classList.toggle|localStorage.theme|bg-white|text-black|border-gray-200" FE\src -S
```

Output cua task phai neu ro: reuse gi, co them dependency khong, va vi sao.
## B?i c?nh

H? th?ng ?? c? n?t chuy?n theme dark/light, nh?ng dark theme hi?n ch?a ho?n ch?nh. M?t s? trang khi b?t dark theme v?n c?n nhi?u v?ng n?n s?ng:

- card tr?ng
- table tr?ng
- filter/search panel tr?ng
- form input/select s?ng
- empty state s?ng
- workflow/status card s?ng
- modal/drawer/dropdown n?u c?
- button ph?, badge, border, shadow ch?a ph? h?p dark mode

C?c trang ?ang th?y l?i r?:

```text
/assignments
/applications
```

V?n ?? hi?n t?i kh?ng n?n x? l? b?ng c?ch s?a l? t?ng trang. Ph?i chu?n h?a token/theme/component chung ?? c?c task sau kh?ng ti?p t?c t?o UI l?ch theme.

## M?c ti?u

Khi b?t dark theme:

1. To?n b? layout ph?i ??ng b? dark mode.
2. Kh?ng c?n card/table/panel tr?ng g?y ch?i.
3. Text ch?nh/ph?/placeholder ph?i d? ??c v? thu?n m?t.
4. Border, shadow, hover, active, selected, disabled state ph?i ph? h?p dark mode.
5. Status colors v?n ph?n bi?t r? nh?ng kh?ng qu? r?c/neon.
6. Light theme kh?ng b? h?ng.
7. Theme ph?i d?ng token/config chung, kh?ng hardcode l? t? trong t?ng page.
8. Giao di?n v?n gi? t?nh ch?t operational dashboard: compact, nhi?u d? li?u, ?t l?n chu?t.

## Nguy?n t?c thi?t k?

Dark theme kh?ng c? ngh?a l? d?ng n?n ?en tuy?t ??i ? m?i n?i. H?y thi?t k? theo nhi?u t?ng surface:

| Layer | Light | Dark |
| --- | --- | --- |
| App background | n?n s?ng nh? | slate/navy r?t t?i |
| Surface/card | tr?ng ho?c slate r?t nh?t | t?i h?n/s?ng h?n app background m?t m?c |
| Elevated surface | n?i nh? so v?i card | s?ng h?n card m?t ch?t |
| Border | slate/gray nh? | slate/xanh x?m nh?, ?? th?y |
| Text primary | g?n ?en | g?n tr?ng nh?ng kh?ng ch?i |
| Text secondary | x?m ??m | x?m xanh s?ng v?a |
| Muted text | x?m v?a | kh?ng qu? m? |

Kh?ng d?ng trong dark mode:

```text
bg-white kh?ng c? dark variant
text-black kh?ng c? dark variant
border-gray-100 ho?c border-gray-200 kh?ng c? dark variant
shadow s?ng qu? m?nh
status color neon qu? g?t
opacity to?n page ?? gi? dark mode
```

## Ph?m vi b?t bu?c

### 1. Theme/config chung

Ki?m tra v? c?p nh?t c?c file:

```text
FE/src/config/theme.config.ts
FE/src/config/ui.config.ts
FE/src/config/layout.config.ts
FE/src/index.css
FE/src/App.css n?u c?
FE/tailwind.config.* n?u c?
```

N?u project d?ng Tailwind v4/CSS variables, ?u ti?n semantic token:

```css
:root {
  --color-background: ...;
  --color-foreground: ...;
  --color-card: ...;
  --color-card-foreground: ...;
  --color-muted: ...;
  --color-muted-foreground: ...;
  --color-border: ...;
  --color-input: ...;
}

.dark {
  --color-background: ...;
  --color-foreground: ...;
  --color-card: ...;
  --color-card-foreground: ...;
  --color-muted: ...;
  --color-muted-foreground: ...;
  --color-border: ...;
  --color-input: ...;
}
```

N?u project ?ang d?ng config object, m? r?ng `themeConfig` theo h??ng semantic:

```ts
export const themeConfig = {
  surface: {
    app: "bg-slate-50 text-slate-950 dark:bg-slate-950 dark:text-slate-100",
    card: "bg-white text-slate-950 border-slate-200 dark:bg-slate-900 dark:text-slate-100 dark:border-slate-700/70",
    muted: "bg-slate-50 dark:bg-slate-800/70",
    elevated: "bg-white dark:bg-slate-900/95",
  },
  input: {
    base: "bg-white border-slate-200 text-slate-950 placeholder:text-slate-400 dark:bg-slate-900 dark:border-slate-700 dark:text-slate-100 dark:placeholder:text-slate-500",
  },
};
```

Kh?ng t?o th?m nhi?u config tr?ng m?c ??ch n?u ?? c? config hi?n t?i.

### 2. UI primitives

Refactor component chung tr??c khi s?a page:

```text
FE/src/components/ui/PageContainer.tsx
FE/src/components/ui/PageHeader.tsx
FE/src/components/ui/Card.tsx
FE/src/components/ui/Button.tsx
FE/src/components/ui/Badge.tsx
```

N?u thi?u c?c component d??i ??y, c?n nh?c t?o th?m khi th?t s? c?n v? d?ng l?i nhi?u n?i:

```text
Table
Input
Select
Modal
Dropdown
EmptyState
```

C?c component chung ph?i c? light/dark style ??y ??, g?m hover/focus/active/disabled.

### 3. Page background

To?n b? app background ph?i nh?t qu?n:

- Light: n?n s?ng nh?, kh?ng ch?i.
- Dark: n?n slate/navy t?i, kh?ng ?en tuy?t ??i n?u g?y m?i m?t.

Kh?ng ?? content page c? n?n kh?c bi?t b?t th??ng v?i app shell.

### 4. Header v? Sidebar

Header/sidebar hi?n t??ng ??i ?n nh?ng v?n ph?i ki?m tra:

- search input
- theme toggle
- user dropdown
- notification icon
- breadcrumb
- selected menu item
- hover menu item
- section title trong sidebar

Text, icon v? border ph?i ?? contrast.

### 5. Cards v? Panels

C?c card nh? page header, filter panel, stats card, workflow card, empty state ph?i c? dark style.

Y?u c?u:

- Kh?ng c? n?n tr?ng trong dark mode.
- Border r? nh?.
- Shadow gi?m l?i ho?c ?u ti?n border thay shadow.
- Spacing gi? compact theo `ui.config.ts`.

### 6. Tables

Table l? khu v?c quan tr?ng nh?t.

Dark mode table c?n c?:

- header row n?n t?i kh?c body m?t ch?t
- body row d?ng surface ph? h?p
- row border nh?
- hover row r? nh?ng kh?ng ch?i
- selected row n?u c?
- empty/loading/error state dark
- text primary/secondary r?
- action buttons d? nh?n

Kh?ng ?? table tr?ng tr?n n?n t?i.

### 7. Forms / Inputs / Selects

C?c input ph?i c? dark style:

- search input
- select
- textarea
- date picker n?u c?
- checkbox
- switch
- file upload

Placeholder kh?ng ???c qu? m?. Focus ring ph?i r?:

```text
focus:ring-blue-500/40
focus:border-blue-500
```

### 8. Buttons

Chu?n h?a button theo variant:

```text
primary
secondary
ghost
outline
danger
success
```

Trong dark mode:

- primary v?n n?i b?t
- outline kh?ng qu? s?ng
- danger kh?ng ch?i
- disabled r? l? disabled
- hover/active state c? ph?n h?i

### 9. Badges / Status

Status badge c?n m?u d?u trong dark mode:

```text
COMPLETED
PENDING
REJECTED
APPROVED
ACTIVE
INACTIVE
DRAFT
SUBMITTED
LATE
```

V? d?:

```text
completed: bg-emerald-500/10 text-emerald-300 border-emerald-500/30
pending:   bg-amber-500/10 text-amber-300 border-amber-500/30
rejected:  bg-rose-500/10 text-rose-300 border-rose-500/30
```

Kh?ng d?ng n?n xanh/?? qu? s?ng trong dark mode.

### 10. Modals / Drawers / Dropdowns

N?u c? modal, drawer, dropdown:

- overlay ph?i t?i v?a
- panel d?ng surface/card token
- text/border/input b?n trong ??ng dark
- kh?ng c? v?ng tr?ng c?n s?t

### 11. Empty / Loading / Error States

Empty state trong `/applications` ?ang c? n?n tr?ng. C?n chuy?n sang surface dark.

Y?u c?u:

- icon muted nh?ng v?n th?y
- title ?? s?ng
- description ?? contrast
- background kh?ng tr?ng

## Accessibility / Contrast

Dark theme ph?i h??ng t?i WCAG AA cho text ch?nh.

Y?u c?u t?i thi?u:

- Text ch?nh tr?n dark surface d?ng `slate-100` ho?c `slate-50`.
- Text ph? d?ng `slate-300` ho?c `slate-400`.
- Tr?nh text `slate-500` tr?n n?n qu? t?i n?u kh? ??c.
- Border d?ng `slate-700` ho?c `slate-700/70`.
- Focus state nh?n r? b?ng b?n ph?m.
- Kh?ng d?ng m?u s?c l?m t?n hi?u duy nh?t cho tr?ng th?i quan tr?ng.

## C?c trang c?n ki?m tra t?i thi?u

B?t dark theme v? ki?m tra ?t nh?t:

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
/assignments
/applications
/assessment-results
/settings/roles
/settings/permissions
```

N?u route n?o ch?a t?n t?i, ghi r? trong final note.

## Quy tr?nh th?c hi?n ?? xu?t

1. ??c rule frontend config v? ki?m tra c?u tr?c theme hi?n t?i.
2. Qu?t to?n b? frontend ?? t?m hardcode light style:

```powershell
rg -n "bg-white|bg-gray-50|bg-slate-50|text-black|text-gray-900|text-slate-900|border-gray-100|border-gray-200|shadow-xl|shadow-lg|background:\s*['"]white|color:\s*['"]black" FE\src
```

3. Chu?n h?a token trong `theme.config.ts`, `ui.config.ts`, CSS variables n?u c?.
4. Refactor UI primitives d?ng chung.
5. S?a c?c page c?n hardcode m?u s?ng, ?u ti?n page l?i r? tr??c: `/assignments`, `/applications`.
6. Ki?m tra l?i to?n b? route ch?nh ? light mode v? dark mode.
7. Ch?y lint/build.
8. Commit v? push ri?ng task 17.

## Kh?ng ???c l?m

- Kh?ng ch? s?a ri?ng `/assignments` v? `/applications` r?i b? qua to?n h? th?ng.
- Kh?ng hardcode dark style l?p l?i ? nhi?u page n?u c? th? ??a v?o token/component chung.
- Kh?ng l?m dark theme b?ng c?ch gi?m opacity to?n b? page.
- Kh?ng ??i business logic.
- Kh?ng l?m m?t light theme.
- Kh?ng th?m th? vi?n UI/theme m?i n?u ch?a ki?m tra kh? n?ng hi?n c? c?a project.

## Verification b?t bu?c

Trong th? m?c `FE`, ch?y:

```powershell
npm run lint
npm run build
```

Ki?m tra th? c?ng:

1. B?t light theme: giao di?n kh?ng b? h?ng.
2. B?t dark theme: kh?ng c?n card/table/panel tr?ng b?t th??ng.
3. V?o `/assignments`: table v? filter panel ??ng dark mode.
4. V?o `/applications`: workflow card, tab, empty state ??ng dark mode.
5. V?o `/settings/permissions`: b?ng checkbox d? ??c.
6. Hover/focus/selected state r?.
7. Text kh?ng b? ch?m, kh?ng b? ch?i.
8. Refresh browser v?n gi? theme ?? ch?n.

N?u c? th?, d?ng Playwright ho?c screenshot ?? ki?m tra ?t nh?t desktop viewport.

## K?t qu? c?n b?n giao

Agent ph?i tr? v?:

1. Rule ?? ??c.
2. Skill query ?? ch?y v? skill ?? d?ng/c?i n?u c?.
3. C?c file theme/config ?? ch?nh.
4. C?c component chung ?? refactor.
5. C?c page ?? s?a l?i dark mode.
6. Danh s?ch class hardcode s?ng ?? lo?i b? ho?c c?n t?n t?i.
7. C?ch ??m b?o light theme kh?ng b? ?nh h??ng.
8. K?t qu? `npm run lint`.
9. K?t qu? `npm run build`.
10. Commit hash v? tr?ng th?i push GitHub cho task 17.
