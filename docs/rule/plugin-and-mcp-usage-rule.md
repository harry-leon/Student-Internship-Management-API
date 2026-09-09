# Plugin and MCP Usage Rule

Tai lieu nay quy dinh cach agent phai hieu va uu tien dung plugin, skills, MCP servers va hooks trong Antigravity khi lam viec voi workspace nay.

## Pham vi ap dung

Rule nay ap dung khi workspace co the chua plugin trong cac duong dan:

```text
.agents/plugins/
_agents/plugins/
~/.gemini/config/plugins/
```

Va khi task co the dung skill, rule, MCP server hoac hook do plugin cung cap.

## Cau truc plugin bat buoc

Mot plugin hop le phai co:

```text
plugins/<plugin-name>/
├── plugin.json
├── mcp_config.json   # optional
├── hooks.json        # optional
├── skills/
│   └── <skill-name>/
│       └── SKILL.md
└── rules/
    └── <rule-name>.md
```

## Nguyen tac uu tien

1. Neu workspace co plugin phu hop, doc plugin truoc khi tim tai nguyen ben ngoai.
2. Neu plugin co skill phu hop, doc `SKILL.md` cua skill do truoc khi code.
3. Neu plugin co rule phu hop, ap dung rule do cung voi rule trong `docs/rule`.
4. Neu plugin co `mcp_config.json` va task can ket noi dich vu ben ngoai, kiem tra MCP truoc khi tu code integration moi.
5. Chi dung hooks khi plugin da mo ta ro side effects va task can automation do.
6. Khong assume plugin ton tai neu khong co folder hoac manifest hop le.

## Thu tu kiem tra plugin

Khi bat dau task lien quan den UI, backend, skill, MCP hoac automation:

1. Kiem tra workspace plugin folders.
2. Doc `plugin.json` de xac nhan ten plugin va component co san.
3. Doc plugin-local `rules/*.md` neu co.
4. Doc plugin-local `skills/*/SKILL.md` neu co skill phu hop.
5. Doc `mcp_config.json` neu task co the dung MCP server.
6. Chi sau do moi quay sang skill search ben ngoai hoac code truc tiep.

## Cach su dung voi repo nay

- Khong tao lai skill noi dung da co trong plugin neu plugin da cung cap skill phu hop.
- Khong tu ve rule plugin/MCP trong `docs/rule` neu plugin da co rule ro rang; thay vao do doc va tuan thu.
- Khong tinh plugin la thay the cho security, permission hay backend enforcement. Plugin chi la lop nang cap nang luc cho agent.
- Khong commit secret, token, hoac cau hinh MCP nhay cam vao repo.

## Checklist truoc khi lam task co plugin/MCP

- Da xac dinh workspace co plugin phu hop chua.
- Da doc `plugin.json`.
- Da doc `SKILL.md` lien quan neu co.
- Da doc `rules/*.md` cua plugin neu co.
- Da kiem tra `mcp_config.json` neu task can ket noi dich vu.
- Da ghi ro trong final note skill/plugin/MCP nao duoc dung hoac ly do khong dung.
