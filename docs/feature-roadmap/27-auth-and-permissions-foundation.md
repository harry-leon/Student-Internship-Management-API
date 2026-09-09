# Task 27 - Auth And Permissions Foundation

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/google-oauth-client-config-rule.md` neu co Google login
- Truoc khi code:

```powershell
npx skills find "spring boot authentication oauth2 jwt refresh token permissions feature flags"
npx skills find "spring security role permission matrix login session revocation"
```

- Uu tien skill:
  - `springboot-security`
  - `spring-boot-crud-patterns`

## Muc tieu

Hoan thien lop xac thuc va phan quyen de he thong co the scale ma khong bi hardcode role:

1. JWT/session/oauth flow ro rang.
2. Refresh token/revoke flow ro rang.
3. Permission matrix co the thay doi runtime.
4. Menu, route va action FE phai dong bo theo quyen that.

## Pham vi

### 1. Authentication

Can chuan hoa:

- login
- logout
- token refresh
- token expiry
- revocation
- Google OAuth if used

### 2. Permission model

- role -> permission
- feature flag -> enabled/disabled
- ownership checks
- admin override co gioi han ro rang
- capability reload sau khi thay doi phan quyen

### 3. Frontend sync

Khi permissions thay doi, FE phai:

- refresh capabilities runtime
- hide/disable menu and actions
- route guard chuan
- khong hien form/action sai quyen

### 4. Security posture

- password policy
- brute force protection
- rate limited login
- no secrets in repo

## Test

- login success/fail
- refresh token revoke
- permission update reflected immediately
- route blocked when capability missing

## Acceptance criteria

- Auth and permissions khong hardcode.
- Runtime permission sync hoat dong.
- Google OAuth/client config dung rule neu duoc dung.

