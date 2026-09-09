# Task 45 - Authentication Hardening, Secret Rotation And Lockout

## Muc tieu

Loai bo rui ro auth critical va bao ve login/token khoi brute force, secret leak va reuse token.

## Pham vi

- Xoa JWT secret hardcoded fallback; production phai fail fast neu thieu secret hop le.
- Tach secret theo moi truong, huong dan rotation va khong commit secret.
- Implement refresh token rotation/revocation va replay detection theo datastore phu hop.
- Rate limit login, dem failed attempts, temporary lockout va thong diep khong leak username ton tai.
- Ap dung password policy cho tao/reset password; migrate hash an toan neu can.
- Kiem tra OAuth redirect/origin va cookie/token policy.

## Tieu chi nghiem thu

- Build/test production config that bai khi thieu secret bat buoc.
- Refresh token bi revoke khong dung lai duoc; logout va password change invalidate token theo policy.
- Login abuse tra ve 429 hoac 423 phu hop, co Retry-After khi bi rate limit.
- Co test auth negative, lockout, rotation, secret config va OAuth callback.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (JWT library, rate limiting, lockout)

## Kiem thu

- Chay backend test/build lien quan den authentication, security
- Chay frontend lint/typecheck/test/build
- Verify secret rotation, lockout, rate limit
- Test auth negative, lockout, rotation, secret config

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-45): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit secret file
