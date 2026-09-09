# Task 49 - Cache, CDN And Client Query Strategy

## Muc tieu

Giam tai database/API va cai thien perceived performance ma van bao dam du lieu permission-sensitive luon dung.

## Pham vi

- Chon Redis/cache backend khi can; khong dung local in-memory cho du lieu can chia se giua instance.
- Them cache cho read endpoint phu hop, key bao gom tenant/scope/role/filter va TTL ro rang.
- Cache eviction/invalidation sau create/update/delete/permission change.
- Cau hinh `Cache-Control`/CDN cho asset public; khong cache response rieng tu hay file nhay cam sai cach.
- Chon React Query/SWR neu phu hop va dong bo query invalidation, refetch, stale/error state.

## Tieu chi nghiem thu

- Du lieu sau mutation khong hien thi stale qua policy cho phep.
- Cache hit/miss va latency do duoc; co fallback khi Redis khong san sang.
- Multi-instance dung chung cache khong sai scope.
- Co test authorization-aware key va invalidation.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Doc docs/rule/frontend-ui-configuration-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (Redis client, React Query/SWR)

## Kiem thu

- Chay backend test/build lien quan den cache, query strategy
- Chay frontend lint/typecheck/test/build
- Verify cache invalidation, TTL, scope, authorization-aware key
- Test cache hit/miss, fallback, multi-instance

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-49): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
