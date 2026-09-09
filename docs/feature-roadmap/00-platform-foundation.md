# Platform Foundation

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "spring boot security jwt oauth2 google login file upload"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ Spring Security, JWT hardening, OAuth2, file upload hoặc frontend auth testing.

## Mục tiêu

Task này tập trung hoàn thiện nền tảng kỹ thuật trước khi mở rộng các tính năng nghiệp vụ lớn như company, registration, weekly report hoặc grading workflow. Nếu nền auth, bảo mật, upload file, cấu hình môi trường và phân quyền chưa chắc, các module nghiệp vụ phía sau sẽ dễ bị lỗi lặp lại, khó bảo trì và khó triển khai thật.

## Phạm vi chính

- Upload avatar cho user, student, mentor.
- Đăng nhập bằng Google OAuth2.
- Củng cố JWT authentication.
- Chuẩn hóa authorization theo role và ownership.
- Refresh token hoặc token rotation nếu cần.
- Logout/token invalidation rõ ràng hơn.
- Cấu hình CORS, environment, secret management.
- Audit log cho hành động quan trọng.
- Chuẩn hóa error handling cho auth/security.
- Tối ưu frontend auth state và route guard.

## 1. Avatar Upload

### Vấn đề hiện tại

Frontend đang dùng avatar sinh tự động qua `ui-avatars.com`. Cách này tiện cho demo nhưng chưa đủ cho hệ thống thật vì user không thể cập nhật ảnh đại diện của mình, Admin không quản lý được ảnh hồ sơ và dữ liệu phụ thuộc dịch vụ ngoài.

### Database đề xuất

Thêm field vào bảng `users`.

```sql
ALTER TABLE users
ADD COLUMN avatar_url VARCHAR(500);
```

Nếu muốn quản lý file kỹ hơn, tạo bảng riêng:

```sql
CREATE TABLE uploaded_files (
    file_id SERIAL PRIMARY KEY,
    owner_id INTEGER NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    storage_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_uploaded_file_owner FOREIGN KEY (owner_id) REFERENCES users(user_id)
);
```

### Backend API đề xuất

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| POST | `/api/users/me/avatar` | Admin, Mentor, Student | Upload avatar của chính mình |
| POST | `/api/users/{user_id}/avatar` | Admin | Admin cập nhật avatar user |
| DELETE | `/api/users/me/avatar` | Admin, Mentor, Student | Xóa avatar của mình |

Request dùng `multipart/form-data`.

```java
@PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<SuccessResponse<UserResponse>> uploadMyAvatar(
        @RequestPart("file") MultipartFile file
) {
    return ResponseEntity.ok(SuccessResponse.success(userService.uploadMyAvatar(file)));
}
```

### Validation file

- Chỉ nhận `image/jpeg`, `image/png`, `image/webp`.
- Giới hạn dung lượng, ví dụ `2MB`.
- Không dùng tên file gốc làm tên lưu trữ.
- Kiểm tra extension và content type.
- Nếu lưu local, cấu hình thư mục ngoài source code, ví dụ `uploads/avatars`.

### Frontend cần làm

- Profile page có nút đổi avatar.
- Preview ảnh trước khi upload.
- Hiển thị loading khi upload.
- Nếu upload fail, giữ avatar cũ.
- Header/sidebar dùng `user.avatarUrl` nếu có, fallback về generated avatar.

## 2. Google Login OAuth2

### Mục tiêu

Cho phép người dùng đăng nhập bằng tài khoản Google, phù hợp với môi trường trường học/doanh nghiệp. Nếu email trùng user hiện có, hệ thống liên kết tài khoản. Nếu email chưa có, tùy policy mà tạo user mới hoặc chặn đăng nhập.

### Backend dependency

Thêm Spring Security OAuth2 Client:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

### Cấu hình properties

Không hard-code client secret trong source. Dùng environment variables.

```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=openid,email,profile
```

### Luồng đề xuất

1. FE chuyển user tới `/oauth2/authorization/google`.
2. Google xác thực.
3. Backend nhận callback.
4. Backend kiểm tra email.
5. Backend tạo JWT nội bộ của hệ thống.
6. Backend redirect về FE kèm token hoặc authorization code nội bộ.

### Callback strategy

Không nên redirect kèm JWT dài trực tiếp trên URL nếu có thể tránh, vì token có thể bị lưu trong browser history. Phương án đơn giản cho đồ án/demo:

```text
http://localhost:3000/oauth2/callback?token=...
```

Phương án tốt hơn:

```text
http://localhost:3000/oauth2/callback?code=short-lived-code
```

Sau đó FE gọi:

```text
POST /api/auth/oauth2/exchange
```

để đổi code lấy JWT.

### API đề xuất

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/oauth2/authorization/google` | Public | Bắt đầu Google login |
| POST | `/api/auth/oauth2/exchange` | Public | Đổi code lấy JWT |
| GET | `/api/auth/oauth2/status` | Public | Kiểm tra OAuth2 config |

### Business rules

- Chỉ cho đăng nhập email thuộc domain được phép nếu hệ thống thuộc trường, ví dụ `@fpt.edu.vn`.
- Nếu email chưa tồn tại, có thể tạo user mặc định role `STUDENT`.
- Nếu email tồn tại nhưng user inactive, trả `403`.
- Nếu user login bằng Google lần đầu, lưu `provider = GOOGLE`, `providerId`.

### Database bổ sung

```sql
ALTER TABLE users
ADD COLUMN auth_provider VARCHAR(30) NOT NULL DEFAULT 'LOCAL',
ADD COLUMN provider_id VARCHAR(150);
```

## 3. JWT Security Hardening

### Vấn đề cần kiểm tra

JWT hiện tại đã hoạt động nhưng cần đảm bảo các điểm sau:

- Secret đủ dài và lấy từ environment.
- Token có expiration rõ ràng.
- Không log token ra console.
- Backend validate signature, expiration, subject.
- FE tự logout khi nhận `401`.
- API logout có ý nghĩa rõ nếu backend muốn blacklist token.

### Cấu hình secret

Không nên lưu secret thật trong `application.properties`.

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}
```

### Access token và refresh token

Giai đoạn đầu có thể chỉ dùng access token 24h. Khi hệ thống nghiêm túc hơn, nên tách:

- Access token: 15-30 phút.
- Refresh token: 7-30 ngày.
- Refresh token lưu database, có thể revoke.

Database refresh token:

```sql
CREATE TABLE refresh_tokens (
    token_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

API:

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| POST | `/api/auth/refresh` | Public | Cấp access token mới |
| POST | `/api/auth/logout` | Authenticated | Revoke refresh token/current session |
| POST | `/api/auth/logout-all` | Authenticated | Đăng xuất tất cả thiết bị |

### Token blacklist

Nếu chỉ dùng stateless access token, logout phía backend thường không thể vô hiệu hóa token đã phát hành cho đến khi hết hạn. Có ba hướng:

- Chỉ xóa token ở FE, chấp nhận token cũ sống đến expiration.
- Dùng access token ngắn hạn.
- Dùng blacklist/revocation table cho token id `jti`.

Khuyến nghị thực tế: access token ngắn hạn + refresh token có revoke.

## 4. Authorization Theo Role Và Ownership

### Mục tiêu

Role-based access chưa đủ. Hệ thống cần ownership-based access:

- Student chỉ xem/sửa hồ sơ của mình.
- Mentor chỉ xem sinh viên được phân công.
- Mentor chỉ chấm assignment của mình.
- Student chỉ xem assessment result của mình.

### Backend rule

Không nên chỉ dựa vào FE hide menu. Service backend phải kiểm tra quyền dữ liệu.

Ví dụ:

```java
if (currentUser.hasRole("STUDENT") && !studentId.equals(currentUser.getUserId())) {
    throw new AccessDeniedException("Student can only access own profile");
}
```

Với Mentor:

```java
boolean assigned = internshipAssignmentRepository.existsByMentorIdAndStudentId(
    currentUser.getUserId(),
    studentId
);

if (!assigned) {
    throw new AccessDeniedException("Mentor can only access assigned students");
}
```

### Frontend rule

Frontend nên dùng helper quyền:

```ts
canAccessPage(role, page)
canManageSystemData(role)
canGrade(role)
canReviewWeeklyReport(role)
```

Nhưng luôn nhớ: frontend chỉ tối ưu trải nghiệm, không phải bảo mật thật.

## 5. CORS Và Environment Config

### Vấn đề

Khi FE chạy `localhost:3000`, BE chạy `localhost:8080`, cần CORS đúng. Khi deploy, domain sẽ thay đổi. Nếu hard-code, rất dễ lỗi môi trường.

### Backend config

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

Trong `SecurityConfig`, cấu hình CORS:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### Frontend env

Tạo `.env.example`.

```env
VITE_API_BASE_URL=http://localhost:8080
```

`apiClient.ts` nên nối base URL:

```ts
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';
const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
```

## 6. Audit Log

### Mục tiêu

Các hành động quan trọng cần có lịch sử:

- Login thành công/thất bại.
- Logout.
- Tạo/sửa/xóa user.
- Đổi role.
- Deactivate account.
- Tạo assignment.
- Chấm điểm.
- Công bố điểm.

### Database

```sql
CREATE TABLE audit_logs (
    audit_id SERIAL PRIMARY KEY,
    actor_id INTEGER,
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id INTEGER,
    ip_address VARCHAR(64),
    user_agent VARCHAR(255),
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Backend service

```java
void log(Integer actorId, String action, String targetType, Integer targetId, Map<String, Object> metadata);
```

Không nên làm audit log khiến nghiệp vụ chính fail nếu ghi log lỗi. Có thể bắt exception và log server-side.

## 7. Frontend Auth State

### Vấn đề thường gặp

- Token hết hạn nhưng UI vẫn tưởng đang login.
- Reload page bị mất user state nếu không gọi `/api/auth/me`.
- Logout xong route vẫn ở trang private.
- Role backend trả `ADMIN` nhưng FE dùng `Admin`.

### Việc nên làm

- Chuẩn hóa role ngay trong `authService`.
- Khi API trả `401`, tự xóa token và redirect public page.
- Route guard redirect user chưa login về public landing.
- Nút logout luôn gọi backend logout rồi clear local state trong `finally`.
- Không lưu thông tin nhạy cảm trong localStorage ngoài token.

### API client nên cải thiện

```ts
if (response.status === 401) {
  removeStoredToken();
  window.dispatchEvent(new Event('auth:unauthorized'));
}
```

`AuthContext` lắng nghe event:

```ts
useEffect(() => {
  const handleUnauthorized = () => {
    setUser(null);
    setToken(null);
  };

  window.addEventListener('auth:unauthorized', handleUnauthorized);
  return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
}, []);
```

## 8. Error Handling Cho Auth

Chuẩn lỗi nên có:

| HTTP | Error code | Trường hợp |
| --- | --- | --- |
| 400 | `INVALID_INPUT_DATA` | Thiếu username/password |
| 401 | `BAD_CREDENTIALS` | Sai username/password |
| 401 | `EXPIRED_JWT_TOKEN` | Token hết hạn |
| 401 | `INVALID_JWT_TOKEN` | Token sai |
| 403 | `ACCOUNT_DISABLED` | Tài khoản bị khóa |
| 403 | `ACCESS_DENIED` | Không đủ quyền |
| 409 | `OAUTH_EMAIL_ALREADY_LINKED` | Email đã liên kết provider khác |

Frontend nên map message dễ hiểu:

- Sai tài khoản hoặc mật khẩu.
- Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.
- Tài khoản đã bị vô hiệu hóa, liên hệ quản trị viên.
- Bạn không có quyền thực hiện thao tác này.

## 9. Checklist Triển Khai

### Backend

- Thêm avatar field hoặc uploaded file table.
- Thêm upload API.
- Thêm OAuth2 dependency và Google config.
- Đưa JWT secret ra environment.
- Cấu hình CORS bằng env.
- Rà soát `SecurityConfig` whitelist: login, swagger, oauth2 callback.
- Thêm refresh token nếu cần.
- Thêm ownership check ở service.
- Thêm audit log cho thao tác nhạy cảm.
- Chuẩn hóa error code auth/security.

### Frontend

- Thêm `.env.example`.
- Sửa `apiClient` dùng `VITE_API_BASE_URL`.
- Thêm auto logout khi `401`.
- Thêm Google login button.
- Thêm OAuth2 callback page.
- Thêm avatar upload trong profile.
- Dùng avatar từ API ở Header/Profile.
- Kiểm tra menu theo role sau khi role được normalize.

## 10. Test Cần Có

### Backend tests

- Login local thành công.
- Login sai password trả `BAD_CREDENTIALS`.
- User inactive không login được.
- API private thiếu token trả `401`.
- Student gọi API của student khác trả `403`.
- Mentor xem student không được phân công trả `403`.
- Upload avatar sai file type trả `400`.
- Upload avatar quá dung lượng trả `400`.
- Google login với email không hợp lệ bị chặn.
- Logout revoke refresh token thành công.

### Frontend tests/manual checklist

- Login Admin thấy menu Admin.
- Login Mentor không thấy menu quản lý Users.
- Login Student không thấy menu quản lý Users/Students.
- Refresh page vẫn giữ đúng user.
- Token hết hạn thì tự logout.
- Logout chuyển về public page.
- Upload avatar xong Header đổi ảnh.
- Google login callback vào đúng dashboard.

## 11. Thứ Tự Làm Khuyến Nghị

1. Chuẩn hóa `VITE_API_BASE_URL` và CORS.
2. Củng cố JWT secret, expiration, error handling.
3. Auto logout khi API trả `401`.
4. Ownership check ở backend service.
5. Avatar upload.
6. Audit log.
7. Google OAuth2 login.
8. Refresh token/logout all nếu muốn hoàn thiện bảo mật hơn.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `00-platform-foundation`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-platform-foundation-da-sua>
git commit -m "feat: complete platform foundation"
git push origin <branch-name>
```

Không gom thay đổi của task `01-company-management` hoặc các task khác vào commit/push này.

## Kết Luận

Task `00-platform-foundation` nên làm trước hoặc song song với các feature nghiệp vụ lớn. Đây là lớp nền giúp hệ thống ổn định hơn: đăng nhập đáng tin cậy, phân quyền đúng, file upload có kiểm soát, môi trường dễ cấu hình và bảo mật không phụ thuộc vào việc frontend ẩn nút.
