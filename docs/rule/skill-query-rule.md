# Skill Query Rule

Tai lieu nay quy dinh cach agent phai tim skill truoc khi thuc hien cac task trong `docs/feature-roadmap` va cac task sua code co lien quan den nang luc cua agent.

## Nguyen tac bat buoc

1. Truoc khi code mot task, agent phai doc file task markdown tuong ung trong `docs/feature-roadmap`.
2. Agent phai doc cac rule lien quan trong `docs/rule` truoc khi sua code.
3. Agent phai kiem tra plugin local/global va doc rule `docs/rule/plugin-and-mcp-usage-rule.md` truoc khi tim skill ben ngoai.
4. Agent phai chay `npx skills find "<query-phu-hop>"` truoc khi implement khi task can skill search.
5. Neu workspace co conflict dependency khi chay `npx skills`, phai chay command tu mot folder temp sach.
6. Chi cai skill khi skill that su phu hop, co nguon dang tin cay va noi dung huu ich cho task.
7. Sau khi cai skill, agent phai doc `SKILL.md` cua skill do truoc khi code.
8. Neu khong cai skill nao, phai ghi ly do trong final note/task note.
9. Khong duoc cai skill chi vi ten gan dung nhung noi dung khong lien quan.

## Plugin-aware discovery

Neu workspace co plugin phu hop, thu tu uu tien la:

1. Doc `plugin.json`.
2. Doc plugin-local `rules/*.md`.
3. Doc plugin-local `skills/*/SKILL.md`.
4. Kiem tra `mcp_config.json` neu task can integration ben ngoai.
5. Chi sau do moi chay `npx skills find` cho skill ben ngoai khi can.

Khong bo qua plugin skill chi vi chua tim thay skill ben ngoai.

## Query theo task 07-10

| Task | Query bat buoc | Skill local bat buoc |
| --- | --- | --- |
| `07-student-submission-github-zip.md` | `spring boot file upload validation workflow react form` | `internship-management-system` |
| `08-response-dto-query-optimization.md` | `spring boot dto projection api performance database query optimization` | `internship-management-system` |
| `09-frontend-content-density-standardization.md` | `react dashboard table layout responsive design frontend testing` | `internship-management-system` |
| `10-detail-views-for-students-submissions-grading.md` | `react detail view spring boot api dto authorization testing` | `internship-management-system` |
| `11-admin-crud-completeness-audit.md` | `spring boot crud api react admin dashboard forms testing` | `internship-management-system` |

## Query bo sung theo nhu cau

| Khi task co phan | Query bo sung nen chay |
| --- | --- |
| Upload/download file | `spring boot secure file upload multipart validation storage` |
| GitHub URL validation | `url validation security java spring boot` |
| Authorization/RBAC | `spring security method authorization jwt rbac testing` |
| JPA performance | `spring data jpa projection entitygraph n plus one pagination` |
| React table/list density | `react data table responsive compact dashboard ui` |
| Detail modal/drawer | `react drawer modal detail view accessibility` |
| Frontend testing | `react vite typescript testing user flow` |
| Admin CRUD audit | `spring boot crud api react admin dashboard forms testing` |
| Plugin/MCP integration | `antigravity plugin mcp skills rules hooks` |

## Mau ghi note sau khi tim skill

Sau khi chay skill discovery, final note hoac task note phai co:

```text
Skill discovery:
- Query used: <query>
- Skill installed: <skill-name or none>
- Reason: <why selected or why skipped>
- Applied to implementation: <short impact>
```

Neu task dung plugin hoac MCP thay vi skill ben ngoai, ghi ro trong note:

```text
Plugin discovery:
- Plugin checked: <plugin-name>
- Plugin skills/rules/MCP used: <what was used>
- Reason: <why plugin was preferred>
```

## Lenh fallback khi npx skills loi trong repo

```powershell
$tmp = Join-Path $env:TEMP ('skills-' + [guid]::NewGuid().ToString())
New-Item -ItemType Directory -Path $tmp | Out-Null
Push-Location $tmp
npx skills find "<query-phu-hop>"
Pop-Location
```

## Dieu kien truoc khi code

Agent chi nen bat dau code khi da co:

- Da doc file task.
- Da doc rule lien quan trong `docs/rule`.
- Da kiem tra plugin co san va rule `plugin-and-mcp-usage-rule.md` neu can.
- Da chay skill discovery hoac da xac nhan dung plugin skill/MCP thay the.
- Da quyet dinh co cai skill hay khong.
- Da kiem tra code hien co de tranh tao lai logic/DTO/component da co.
