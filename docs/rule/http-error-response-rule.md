# Quy tac tra ve loi HTTP giua Backend, Client UI va Log

Tai lieu nay quy dinh cach Backend tra ve response, cach Client UI hien thi loi va cach ghi log cho cac API cua Internship Management System. Ap dung cho tat ca API hien tai va API moi.

## Muc tieu

- Backend phai tra ve dung HTTP status theo ban chat loi.
- Response thanh cong va response loi phai dung cung mot contract.
- Client UI chi hien thong diep can thiet cho nguoi dung, khong hien stack trace hoac chi tiet noi bo.
- Backend log du thong tin de debug, nhung khong log trung lap cac loi nguoi dung co the sua.
- Frontend console chi dung cho debug ky thuat trong moi truong development, khong thay the thong bao UI.

## Contract response bat buoc

### Success response

Tat ca response thanh cong nen dung `SuccessResponse<T>`:

```json
{
  "success": true,
  "status_code": 200,
  "message": "Company updated successfully",
  "data": {},
  "timestamp": "2026-09-05T13:00:00"
}
```

Quy uoc status thanh cong:

| HTTP status | Khi nao dung |
| --- | --- |
| `200 OK` | Lay du lieu, cap nhat, thao tac thanh cong co response body |
| `201 Created` | Tao moi resource thanh cong |
| `204 No Content` | Xoa hoac thao tac thanh cong khong can body; chi dung neu client da xu ly duoc response rong |

### Error response

Tat ca response loi phai dung `ErrorResponse`:

```json
{
  "success": false,
  "status_code": 400,
  "error_code": "INVALID_INPUT_DATA",
  "message": "Invalid input data",
  "errors": [
    {
      "field": "email",
      "message": "Email is invalid"
    }
  ],
  "timestamp": "2026-09-05T13:00:00"
}
```

Quy uoc field:

| Field | Bat buoc | Ghi chu |
| --- | --- | --- |
| `success` | Co | Luon la `false` voi loi |
| `status_code` | Co | Trung voi HTTP status that cua response |
| `error_code` | Co | Dung enum `ErrorCode`, on dinh de FE co the xu ly theo ma |
| `message` | Co | Thong diep ngan gon, co the hien cho user neu la loi 4xx an toan |
| `errors` | Khong | Dung cho validation field hoac chi tiet loi nghiep vu co cau truc |
| `timestamp` | Co | Thoi diem tao response |

## Bang mapping status code

| Status | Nhom loi | Backend nen tra ve | Client UI nen hien thi | Backend log |
| --- | --- | --- | --- | --- |
| `2xx` | Thanh cong | `SuccessResponse` voi message ro nghia | Toast/snackbar thanh cong neu thao tac thay doi du lieu; khong can toast cho get list/detail | `INFO` cho thao tac quan trong, hoac khong log |
| `3xx` | Redirect | Han che dung cho REST API; neu co thi de infrastructure/client xu ly | Thuong khong hien rieng | `INFO` |
| `400 Bad Request` | Request sai format, thieu param, sai validation co ban | `INVALID_INPUT_DATA`; neu validation thi them `errors[]` theo field | Hien loi field ngay tren form; loi chung hien message ngan gon | `WARN`, khong stack trace |
| `401 Unauthorized` | Chua dang nhap, token thieu/het han/khong hop le, login sai | `BAD_CREDENTIALS`, `EXPIRED_JWT_TOKEN` hoac `INVALID_JWT_TOKEN` | Xoa token, hien "PhiÃªn Ä‘Äƒng nháº­p Ä‘Ã£ háº¿t háº¡n. Vui lÃ²ng Ä‘Äƒng nháº­p láº¡i." va dieu huong login; login sai thi hien "TÃªn Ä‘Äƒng nháº­p hoáº·c máº­t kháº©u khÃ´ng Ä‘Ãºng." | `WARN`, khong stack trace tru khi nghi ngo tan cong |
| `403 Forbidden` | Da dang nhap nhung khong co quyen | `ACCESS_DENIED` | Hien "Báº¡n khÃ´ng cÃ³ quyá»n thá»±c hiá»‡n thao tÃ¡c nÃ y." | `WARN` |
| `404 Not Found` | Resource khong ton tai theo id/filter bat buoc | `RESOURCE_NOT_FOUND` | Hien "KhÃ´ng tÃ¬m tháº¥y dá»¯ liá»‡u." hoac empty state neu la man danh sach | `INFO` neu la lookup thong thuong, `WARN` neu bat thuong |
| `409 Conflict` | Trung du lieu, sai trang thai workflow, xung dot nghiep vu do state hien tai | `DUPLICATE_RESOURCE` hoac error code nghiep vu cu the nhu `INVALID_ASSIGNMENT_STATE` | Hien thong diep nghiep vu cu the: "Dá»¯ liá»‡u Ä‘Ã£ tá»“n táº¡i.", "Sinh viÃªn Ä‘Ã£ Ä‘Æ°á»£c phÃ¢n cÃ´ng.", "KhÃ´ng thá»ƒ cáº­p nháº­t á»Ÿ tráº¡ng thÃ¡i hiá»‡n táº¡i." | `WARN` |
| `422 Unprocessable Entity` | Du lieu dung format nhung vi pham nghiep vu phuc tap | Nen dung khi can tach ro voi `400`; can bo sung handler/error code neu ap dung | Hien loi nghiep vu gan voi form hoac thao tac | `WARN` |
| `500 Internal Server Error` | Loi khong mong doi trong code/server | `INTERNAL_SERVER_ERROR`; message public la "Internal server error" hoac "ÄÃ£ xáº£y ra lá»—i há»‡ thá»‘ng." | Chi hien "ÄÃ£ xáº£y ra lá»—i há»‡ thá»‘ng. Vui lÃ²ng thá»­ láº¡i sau." | `ERROR` kem stack trace |
| `502 Bad Gateway` | Upstream/service phu thuoc loi | Error code service unavailable neu co | Hien "Há»‡ thá»‘ng táº¡m thá»i khÃ´ng kháº£ dá»¥ng. Vui lÃ²ng thá»­ láº¡i sau." | `ERROR` kem context service |
| `503 Service Unavailable` | Server dang qua tai/bao tri/service chua san sang | Error code service unavailable neu co | Hien "Há»‡ thá»‘ng táº¡m thá»i khÃ´ng kháº£ dá»¥ng. Vui lÃ²ng thá»­ láº¡i sau." | `ERROR` |
| `504 Gateway Timeout` | Timeout khi goi service phu thuoc | Error code timeout neu co | Hien "Há»‡ thá»‘ng pháº£n há»“i quÃ¡ lÃ¢u. Vui lÃ²ng thá»­ láº¡i sau." | `ERROR` kem duration/request id |

## Quy tac Backend

1. Controller khong tu `try/catch` de build loi thu cong. Hay throw exception dung loai va de `GlobalExceptionHandler` build `ErrorResponse`.
2. Loi validation request DTO phai dung `jakarta.validation`; response tra `400` va `errors[]` gom `field`, `message`.
3. Loi khong tim thay resource phai throw `ResourceNotFoundException` va tra `404`.
4. Loi trung du lieu hoac xung dot trang thai phai throw `ResourceConflictException` hoac exception nghiep vu cu the va tra `409`.
5. Loi user da dang nhap nhung khong co quyen phai tra `403`, khong tra `404` de che giau tru khi co yeu cau bao mat ro rang.
6. Loi chua dang nhap, token het han hoac token sai phai tra `401`; FE se xoa token va dieu huong login.
7. Loi khong mong doi phai tra `500` voi message public chung, khong tra message exception noi bo.
8. `status_code` trong body phai trung voi HTTP status.
9. `error_code` phai on dinh. Khong de FE phu thuoc vao text `message` de phan nhanh logic.
10. Khong expose stack trace, SQL, ten bang, duong dan file noi bo, secret, token trong response.

## Quy tac Client UI

1. FE phai doc loi theo thu tu: `error.response.data.message` hoac `message` tu `ErrorResponse`; fallback moi dung `HTTP status`.
2. Neu co `errors[]`, hien loi theo field trong form thay vi chi hien toast chung.
3. `401` phai xoa token local, phat event dang xuat va dieu huong ve login.
4. `403` hien man/alert khong co quyen; khong retry tu dong.
5. `404` voi man danh sach nen hien empty state; voi detail/action theo id thi hien thong bao khong tim thay.
6. `409` va `422` phai hien message nghiep vu tu backend neu message an toan cho user.
7. `500`, `502`, `503`, `504` khong hien message ky thuat tu backend; dung thong diep chung cho he thong.
8. Khong log day du response loi chua du lieu nhay cam ra browser console trong production.

## Quy tac log

| Truong hop | Muc log | Noi dung nen log |
| --- | --- | --- |
| Tao/cap nhat/xoa thanh cong nghiep vu quan trong | `INFO` | actor, action, resource id, request id |
| Validation fail, bad request | `WARN` | endpoint, actor neu co, field loi, request id; khong stack trace |
| Login sai, token sai, access denied | `WARN` | username/user id neu co, ip/request id, ly do tong quat |
| Resource not found | `INFO` hoac `WARN` | resource type, id, actor, request id |
| Conflict/business state invalid | `WARN` | action, resource id, current state, actor |
| Loi he thong 5xx | `ERROR` | stack trace, endpoint, actor, request id, input da mask du lieu nhay cam |

Thong tin nhay cam phai mask hoac khong log: password, token, refresh token, authorization header, OTP, secret key, file private, thong tin ca nhan khong can thiet.

## Chuan message

Backend message nen ngan gon, ro nghiep vu, khong dua chi tiet implementation:

| Nen dung | Khong nen dung |
| --- | --- |
| `Student not found with ID: 10` | `Cannot invoke getName() because student is null` |
| `Student code already exists` | `Duplicate entry 'SE001' for key students.uk_code` |
| `Only DRAFT reports can be submitted` | `Illegal state transition from REVIEWED to SUBMITTED in WeeklyReportServiceImpl` |
| `Internal server error` | Stack trace hoac SQL exception |

UI co the dich message sang tieng Viet hoac map theo `error_code`. Voi cac loi 5xx, UI luon dung message chung thay vi message ky thuat.

## Checklist khi them API moi

- Response thanh cong dung `SuccessResponse`.
- Response loi di qua `GlobalExceptionHandler`.
- Request DTO co validation ro rang.
- Loi duplicate/state conflict tra `409`, khong tra `400` chung chung.
- Loi not found tra `404`.
- Loi auth tra dung `401` hoac `403`.
- Loi 5xx khong expose chi tiet noi bo.
- FE co loading, empty state va error state phu hop voi status.
- FE xu ly `errors[]` cho form validation.
- Backend log dung muc va khong log du lieu nhay cam.
## Skill query khi sua error handling

Truoc khi sua response/error handling, agent phai doc file nay va chay skill discovery phu hop:

```powershell
npx skills find "spring boot error handling rest api validation security logging"
```

Neu task co lien quan frontend error UI, chay them:

```powershell
npx skills find "react api error handling toast form validation"
```

Neu co skill tot va phu hop, cai skill roi doc `SKILL.md` truoc khi code. Neu khong cai skill nao, ghi ly do trong final note.

Khi implement phai reuse `SuccessResponse`, `ErrorResponse`, `ErrorCode`, `GlobalExceptionHandler` va `apiClient` hien co truoc khi tao contract moi.
