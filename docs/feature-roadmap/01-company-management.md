# Company Management

## Skill Nên Dùng

- Bắt buộc dùng skill local `internship-management-system` trong `SKILL.md`.
- Trước khi làm task, chạy `npx skills find "spring boot crud api database migration react form"`.
- Nếu tìm thấy skill phù hợp và uy tín, cài bằng `npx skills add <owner>/<repo>` rồi đọc `SKILL.md` của skill đó trước khi code.
- Nếu không có skill đủ tốt, ghi rõ lý do không cài trong task note.
- Nên ưu tiên skill hỗ trợ API design, database migration, CRUD workflow hoặc React form.

## Mục tiêu

Hiện hệ thống có sinh viên, mentor và assignment nhưng chưa có thực thể doanh nghiệp. Trong thực tế, thực tập luôn gắn với công ty, người liên hệ, vị trí thực tập và khả năng tiếp nhận sinh viên. Tính năng Company Management giúp Admin quản lý đối tác thực tập, hỗ trợ phân công sinh viên chính xác hơn và tạo nền cho báo cáo chất lượng doanh nghiệp sau này.

## Người dùng hưởng lợi

| Role | Nhu cầu |
| --- | --- |
| Admin | Tạo, cập nhật, theo dõi danh sách công ty đối tác |
| Mentor | Xem công ty nơi sinh viên mình phụ trách đang thực tập |
| Student | Xem thông tin công ty trong phân công của mình |

## Database đề xuất

Tạo bảng `companies`.

```sql
CREATE TABLE companies (
    company_id SERIAL PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL UNIQUE,
    tax_code VARCHAR(50),
    industry VARCHAR(100),
    address VARCHAR(255),
    contact_person VARCHAR(100),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    website VARCHAR(255),
    max_interns INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Sau đó mở rộng bảng `internship_assignments`.

```sql
ALTER TABLE internship_assignments
ADD COLUMN company_id INTEGER NULL;

ALTER TABLE internship_assignments
ADD CONSTRAINT fk_assignment_company
FOREIGN KEY (company_id) REFERENCES companies(company_id);
```

## Entity backend

Tạo package/model tương ứng:

- `model/entity/Company.java`
- `repository/CompanyRepository.java`
- `model/dto/request/CompanyCreateRequest.java`
- `model/dto/request/CompanyUpdateRequest.java`
- `model/dto/response/CompanyResponse.java`
- `model/mapper/CompanyMapper.java`
- `service/CompanyService.java`
- `service/impl/CompanyServiceImpl.java`
- `controller/CompanyController.java`

Entity nên có các field:

```java
private Integer companyId;
private String companyName;
private String taxCode;
private String industry;
private String address;
private String contactPerson;
private String contactEmail;
private String contactPhone;
private String website;
private Integer maxInterns;
private Boolean isActive;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

## Validation request

`CompanyCreateRequest`:

```java
@NotBlank
@Size(max = 150)
private String companyName;

@Email
@Size(max = 100)
private String contactEmail;

@Min(0)
private Integer maxInterns;
```

`CompanyUpdateRequest` tương tự nhưng có thể cho phép nullable nếu muốn partial update. Nếu hệ thống đang dùng PUT đầy đủ, request update nên yêu cầu đủ field cần thiết.

## API đề xuất

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| GET | `/api/companies` | Admin, Mentor, Student | Lấy danh sách công ty active |
| GET | `/api/companies/{company_id}` | Admin, Mentor, Student | Xem chi tiết công ty |
| POST | `/api/companies` | Admin | Tạo công ty |
| PUT | `/api/companies/{company_id}` | Admin | Cập nhật công ty |
| PUT | `/api/companies/{company_id}/status` | Admin | Bật/tắt công ty |
| DELETE | `/api/companies/{company_id}` | Admin | Xóa hoặc soft delete |

Khuyến nghị dùng soft delete qua `isActive = false` thay vì xóa thật, vì assignment có thể đã tham chiếu công ty.

## Logic service

Các rule quan trọng:

- Không cho tạo trùng `companyName`.
- Không cho tạo `maxInterns < 0`.
- Khi deactivate công ty, không nên xóa assignment cũ.
- Khi assign sinh viên vào công ty, kiểm tra số lượng sinh viên active không vượt quá `maxInterns` nếu `maxInterns > 0`.

Ví dụ service method:

```java
CompanyResponse createCompany(CompanyCreateRequest request);
Page<CompanyResponse> getCompanies(Boolean active, Pageable pageable);
CompanyResponse getCompanyById(Integer companyId);
CompanyResponse updateCompany(Integer companyId, CompanyUpdateRequest request);
CompanyResponse updateStatus(Integer companyId, Boolean isActive);
```

## Frontend cần làm

Tạo trang mới `CompaniesView.tsx`.

Các phần UI:

- Header: `Companies / Đối tác thực tập`
- Filter: search theo tên, ngành, email, trạng thái
- Table/card list: tên công ty, ngành, liên hệ, số slot, trạng thái
- Button `Thêm công ty` chỉ hiển thị với Admin
- Modal create/edit công ty
- Empty state khi chưa có dữ liệu
- Error state khi API lỗi

Thêm type:

```ts
export interface Company {
  id: string;
  name: string;
  industry?: string;
  address?: string;
  contactPerson?: string;
  contactEmail?: string;
  contactPhone?: string;
  website?: string;
  maxInterns: number;
  isActive: boolean;
}
```

Thêm service FE:

```ts
export const companyService = {
  getAll: (params?: CompanyQueryParams) => api.get<CompanyDTO[]>('/api/companies?...'),
  getById: (id: number) => api.get<CompanyDTO>(`/api/companies/${id}`),
  create: (body: CompanyCreateDTO) => api.post<CompanyDTO>('/api/companies', body),
  update: (id: number, body: CompanyUpdateDTO) => api.put<CompanyDTO>(`/api/companies/${id}`, body),
  updateStatus: (id: number, isActive: boolean) => api.put<CompanyDTO>(`/api/companies/${id}/status`, { isActive }),
};
```

## Sidebar và role

Thêm menu `Companies` trong nhóm `Internship` hoặc nhóm riêng `Partners`.

Quyền hiển thị đề xuất:

- Admin: xem và quản lý.
- Mentor: xem.
- Student: chỉ xem công ty liên quan hoặc danh sách public active tùy nghiệp vụ.

## Test backend

Các test nên có:

- Admin tạo company thành công.
- Tạo company trùng tên trả `DUPLICATE_RESOURCE`.
- Mentor/Student gọi POST bị `403`.
- GET list trả phân trang đúng.
- Deactivate company không làm mất assignment cũ.

## Rủi ro

- Nếu xóa cứng company, dữ liệu assignment có thể bị mất liên kết.
- Nếu không chuẩn hóa tên công ty, dễ có trùng gần giống: `FPT Software`, `FPT software`, `FPT Software HCM`.
- Nếu không kiểm tra capacity, Admin có thể phân công quá số slot doanh nghiệp nhận.

## GitHub Push Sau Khi Hoàn Thành Task

Sau khi hoàn thành task `01-company-management`, phải commit và push riêng task này lên GitHub trước khi chuyển sang task tiếp theo.

```powershell
git status
npm run lint
npm run build
git add <cac-file-company-management-da-sua>
git commit -m "feat: complete company management"
git push origin <branch-name>
```

Không gom thay đổi của task `02-internship-registration-approval` hoặc các task khác vào commit/push này.
