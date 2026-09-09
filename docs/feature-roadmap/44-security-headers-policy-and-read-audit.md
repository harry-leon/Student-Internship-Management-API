# Task 44 - Security Headers, Central Policy And Read Audit

## Muc tieu

Khac phuc cac gap security cua task 26: security headers, policy tap trung va audit read operations nhay cam.

## Pham vi

- Cau hinh HSTS chi trong HTTPS, X-Content-Type-Options, frame protection, Referrer-Policy va CSP phu hop FE.
- Tao PolicyService/authz helper dung chung cho ownership, group scope va admin override.
- Audit cac read operation nhay cam theo policy, khong log token/password/file content.
- Kiem tra CORS, actuator exposure va cache headers khong lam lo response rieng tu.
- Viet security test verify headers va scope bypass.

## Tieu chi nghiem thu

- Production response co header can thiet, local HTTP khong bi cau hinh HSTS sai.
- Moi endpoint nhay cam co authorization service-layer, khong chi dua vao menu FE.
- Audit log co actor, action, target, result, request/correlation id va timestamp.
- Khong ghi secret, credential hoac noi dung file vao log.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code

## Kiem thu

- Chay backend test/build lien quan den security, headers, audit
- Chay frontend lint/typecheck/test/build neu co FE lien quan
- Verify security headers, CORS, CSP, authorization
- Test scope bypass va security negative cases

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-44): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
