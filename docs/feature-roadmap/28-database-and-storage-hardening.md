# Task 28 - Database And Storage Hardening

## Skill nen dung

- Bat buoc dung skill local `internship-management-system`.
- Bat buoc doc:
  - `docs/rule/http-error-response-rule.md`
  - `docs/rule/skill-query-rule.md`
  - `docs/rule/library-reuse-rule.md`
- Truoc khi code:

```powershell
npx skills find "database design normalization indexing migration file storage object storage schema"
npx skills find "spring boot multipart upload avatar file metadata storage"
```

- Uu tien skill:
  - `database-schema-designer`
  - `spring-boot-crud-patterns`

## Muc tieu

Scale lon se dung vao DB va storage truoc tien. Task nay chuan hoa:

1. schema
2. indexes
3. migrations
4. object/file storage
5. audit metadata

## Pham vi

### 1. Database design

- 3NF truoc, denormalize co chu y
- FK/unique/check constraints day du
- index theo access pattern
- soft delete and audit fields
- transaction boundaries ro rang

### 2. Storage design

- avatar
- image
- zip/file submission
- document download
- attachment in group/task/chat

Can co metadata table:

- file_id
- owner_id
- scope_type
- scope_id
- storage_provider
- object_key
- mime_type
- size_bytes
- checksum
- uploaded_at

### 3. Migration safety

- backward compatible migrations
- zero downtime khi co the
- backfill strategy
- rollback plan

### 4. Scale concerns

- query projection
- read/write hot tables
- archive strategy
- retention policy

## Test

- schema constraints pass
- upload metadata persist dung
- duplicate file/reference bi chan neu can
- migration co UP/DOWN ro rang

## Acceptance criteria

- DB design va storage design duoc chuan hoa.
- Metadata va constraints duoc quan ly thay vi hardcode.
- Task 14 co the reuse schema nay de scale.

