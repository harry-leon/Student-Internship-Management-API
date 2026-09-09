# Task 46 - Production Database And Storage Hardening

## Muc tieu

Chuyen database/storage tu che do demo sang migration va metadata co the van hanh an toan trong production.

## Pham vi

- Chon Flyway hoac Liquibase theo stack hien tai, baseline schema va migration rollback strategy.
- Loai bo `ddl-auto=update` khoi production; validate schema khi startup.
- Tat `show-sql` trong production, chi bat qua config moi truong khi debug co kiem soat.
- Hoan thien StoredFile metadata: owner, scope, content type, size, checksum, storage key, status, created/updated actor.
- Chuan hoa soft delete, audit fields, unique/index/foreign-key constraints va cleanup orphan files.
- Kiem tra upload/download authorization, signed URL va virus/content validation neu scope yeu cau.

## Tieu chi nghiem thu

- Database moi co the tao bang migration reproducible; deploy lap lai khong loi.
- Schema khong mat du lieu khi nang cap va co huong rollback/restore da test.
- File khong the truy cap bang storage key neu khong qua authorization.
- Co integration test cho constraint, migration va file metadata.

## Rule bat buoc truoc khi lam

- Doc docs/rule/http-error-response-rule.md
- Doc docs/rule/skill-query-rule.md
- Doc docs/rule/library-reuse-rule.md
- Doc docs/rule/plugin-and-mcp-usage-rule.md
- Chay skill discovery phu hop voi task
- Kiem tra library/dependency/component/plugin/MCP san co truoc khi tu code (Flyway/Liquibase, file storage library)

## Kiem thu

- Chay backend test/build lien quan den database migration
- Chay migration validation (schema test)
- Verify migration rollback, file authorization
- Test constraint, migration va file metadata

## Git

- Chi add file thuoc task hien tai
- Commit rieng cho task: `feat(task-46): ...`
- Push thanh cong roi moi sang task tiep theo
- Khong commit file ngoai scope
