# Task 32 - Rate Limiting And Abuse Protection

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
  - `docs/rule/google-oauth-client-config-rule.md` neu co login OAuth
- Truoc khi code:

```powershell
npx skills find "spring boot rate limiting abuse protection login throttling bucket4j"
npx skills find "api throttling upload protection search abuse defense"
```

- Uu tien skill:
  - `springboot-security`

## Muc tieu

Bao ve he thong truoc:

- brute force login
- spam API
- abuse upload
- notification spam
- search spam

## Pham vi

### 1. Endpoint limits

- login
- refresh token
- upload
- search
- notification polling
- comment/chat send

### 2. Per user and per IP policy

- limit theo IP
- limit theo user
- limit theo endpoint
- limit theo burst and window

### 3. Response

- 429 dung nghia
- retry-after hint
- log burst event

### 4. Complementary controls

- failed login counter
- temporary lockout
- quota for file upload
- abuse detection for repetitive action

## Test

- rate limited requests tra 429
- legit requests van qua
- upload spam bi chan

## Acceptance criteria

- He thong khong de bi spam/abuse.
- Rate limit co rule ro rang theo endpoint va user scope.

