# Task 38 - Frontend Scale Hardening Last Mile

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/frontend-ui-configuration-rule.md`
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "frontend performance route guards role based menu data dense ui"
npx skills find "react code splitting query cache error states accessibility"
```

- Uu tien skill:
  - `frontend-design`

## Muc tieu

Frontend la task cuoi cung trong roadmap scale lon. Muc tieu khong phai lam dep noi dung, ma:

1. dong bo voi auth/permissions that
2. load nhanh hon
3. khong leak action/route sai quyen
4. render data lon on dinh

## Pham vi

### 1. Performance

- code splitting
- lazy load route
- data fetching cache
- memoization only where needed
- table virtualization if lists large

### 2. UI consistency

- config-driven routes/menu/permissions/theme
- compact tables/cards
- loading/error/empty states
- no hardcoded demo data

### 3. Accessibility and safety

- keyboard navigation
- proper contrast
- safe disabled states
- tooltip/labels for icons

### 4. Sync with backend rules

- hide menu/action when permission missing
- route guard on load
- refresh after permission change
- notification/dashboard/group workspace state sync

## Test

- route guard works
- hidden action never renders for forbidden role
- build/lint pass
- large list layout remains usable

## Acceptance criteria

- Frontend is the last-mile shell for the real system.
- No role can see or trigger what backend does not allow.

