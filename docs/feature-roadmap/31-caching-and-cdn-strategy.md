# Task 31 - Caching And CDN Strategy

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "spring boot cache redis response caching invalidation cdn"
npx skills find "cache strategy frontend query cache dashboard performance"
```

- Uu tien skill:
  - `database-schema-designer`
  - `spring-boot-crud-patterns`

## Muc tieu

Scale lon can cache dung cho:

1. repeated reads
2. dashboard summaries
3. static assets
4. file/image delivery

## Pham vi

### 1. Backend caching

- cache keys ro rang
- TTL ro rang
- invalidation sau mutation
- khong cache du lieu nhay cam sai scope

### 2. CDN strategy

- images/avatar/static assets qua CDN neu co
- cache-control headers dung
- signed url neu co file private

### 3. Frontend cache

- query cache / refetch strategy
- no stale permissions/dashboard after save
- prefetch read-only detail where useful

### 4. Safety

- do not cache sensitive per-user data broadly
- cache must respect role/scope

## Test

- cache hit/miss valid
- invalidation sau update/delete
- permissioned data khong bi cross-user leak

## Acceptance criteria

- Repeated reads giam load.
- Asset/file delivery co CDN plan.
- Cache invalidation ro rang va test duoc.

