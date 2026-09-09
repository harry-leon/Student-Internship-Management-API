# Google OAuth Client Configuration Rule

Tai lieu nay luu chuan cau hinh Google OAuth Client cho he thong Internship Management System.

## Muc Tieu

Dung cho tinh nang dang nhap Google voi Spring Boot OAuth2 Client va frontend React/Vite.

Rule nay chi luu cau hinh public nhu origin, redirect URI va bien moi truong can co. Khong duoc luu client secret that vao repository.

## Google Cloud Project

- Project name/display: `auth-service`
- Project id hien tai: `auth-service-481816`
- Console area: Google Auth Platform > Clients
- OAuth client type: `Web application`
- OAuth client name goi y: `Internship Manage` hoac `Internship Management System`

## Authorized JavaScript Origins

Khi tao OAuth Client ID, them cac origin sau:

```text
http://localhost:3000
http://localhost:5173
https://ims.harryleon.id.vn
https://www.ims.harryleon.id.vn
```

Ghi chu:

- `localhost:3000` dung cho frontend dev mac dinh.
- `localhost:5173` dung cho Vite dev neu chay port 5173.
- `https://ims.harryleon.id.vn` dung cho frontend production.
- `https://www.ims.harryleon.id.vn` chi dung neu DNS/hosting co cau hinh subdomain `www.ims`.
- Khong them wildcard nhu `*.harryleon.id.vn` vi Google OAuth Web Client khong chap nhan wildcard origin.

## Authorized Redirect URIs

Them cac redirect URI sau:

```text
http://localhost:8080/login/oauth2/code/google
https://api-ims.harryleon.id.vn/login/oauth2/code/google
```

Ghi chu:

- Local backend Spring Boot dung `http://localhost:8080/login/oauth2/code/google`.
- Production backend dung `https://api-ims.harryleon.id.vn/login/oauth2/code/google`.
- Redirect URI phai match tuyet doi voi Spring Security registration id `google`.
- Neu doi backend domain, phai them redirect URI moi vao Google Cloud truoc khi deploy.

## Bien Moi Truong Backend

Khong hard-code secret vao `application.properties`. Backend dang doc tu bien moi truong:

```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID:dummy-google-client-id}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET:dummy-google-client-secret}
```

Moi truong local/deploy phai set:

```text
GOOGLE_CLIENT_ID=<google-oauth-client-id>
GOOGLE_CLIENT_SECRET=<google-oauth-client-secret>
```

## Thong Tin Duoc Phep Luu Trong Repo

Duoc phep luu:

- Project id.
- OAuth client type.
- Authorized JavaScript origins.
- Authorized redirect URIs.
- Ten bien moi truong.
- Huong dan setup.

Khong duoc luu:

- `GOOGLE_CLIENT_SECRET` that.
- File JSON OAuth client co secret.
- Screenshot hien client secret neu commit vao repo.
- `.env`, `.env.local`, file log co secret.

## Bao Mat Bat Buoc

1. Neu client secret da bi paste vao chat, screenshot, issue, commit hoac tai lieu public, phai rotate secret tren Google Cloud Console.
2. Khi deploy, set secret bang bien moi truong cua hosting provider.
3. Khong tra `clientSecret` qua API.
4. Khong log OAuth token, authorization code, access token, refresh token hoac ID token.
5. Neu dung GitHub, dam bao `.env*` va credential JSON nam trong `.gitignore`.
6. OAuth consent screen trong che do testing chi cho phep test users. Muon user ngoai test login thi can publish/verify theo yeu cau cua Google.

## DNS Production Can Co

Domain chinh `harryleon.id.vn` dang dung PA Vietnam nameserver. Khong sua record root `@` hoac `www` neu dang noi voi du an khac.

Can tao subdomain rieng:

```text
ims.harryleon.id.vn      -> frontend
api-ims.harryleon.id.vn  -> backend
```

Neu hosting frontend cung cap domain dich, dung `CNAME`:

```text
Type: CNAME
Name: ims
Value: <frontend-host-domain>
```

Neu backend co IP co dinh, dung `A record`:

```text
Type: A
Name: api-ims
Value: <backend-server-ip>
```

Neu backend hosting cung cap domain dich, dung `CNAME`:

```text
Type: CNAME
Name: api-ims
Value: <backend-host-domain>
```

## Checklist Khi Cau Hinh Google Login

- [ ] Da tao OAuth Client type `Web application`.
- [ ] Da them local frontend origin `http://localhost:3000`.
- [ ] Da them Vite frontend origin `http://localhost:5173` neu can.
- [ ] Da them production frontend origin `https://ims.harryleon.id.vn`.
- [ ] Da them local backend redirect URI `http://localhost:8080/login/oauth2/code/google`.
- [ ] Da them production backend redirect URI `https://api-ims.harryleon.id.vn/login/oauth2/code/google`.
- [ ] Da set `GOOGLE_CLIENT_ID` va `GOOGLE_CLIENT_SECRET` trong environment.
- [ ] Khong commit client secret hoac OAuth JSON vao repo.
- [ ] Neu secret tung bi lo, da rotate secret.
- [ ] OAuth consent screen co test users neu app dang Testing.

## Khi Agent Sua OAuth

Truoc khi sua tinh nang Google login, agent phai doc file nay va kiem tra:

```powershell
Get-Content docs\rule\google-oauth-client-config-rule.md
rg -n "GOOGLE_CLIENT|oauth2|login/oauth2/code/google|OAuth" BE FE docs -g "!*node_modules*"
Get-Content BE\src\main\resources\application.properties
```

Neu can them domain moi, chi them vao rule va huong dan Google Cloud Console. Khong them secret vao markdown.
