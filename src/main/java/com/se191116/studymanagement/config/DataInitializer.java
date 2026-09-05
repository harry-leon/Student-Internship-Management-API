package com.se191116.studymanagement.config;

import com.se191116.studymanagement.model.entity.*;
import com.se191116.studymanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final CompanyRepository companyRepository;
    private final InternshipPhaseRepository phaseRepository;
    private final InternshipAssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final SystemFeatureRepository systemFeatureRepository;
    private final RoleFeatureRepository roleFeatureRepository;

    @Override
    public void run(String... args) {
        seedRbacAndFeatureFlags();
        seedUserIfNotFound("admin", "admin@fpt.edu.vn", "admin123", "He Thong Admin", UserRole.ADMIN);
        seedDashboardDemoData();
    }

    private void seedDashboardDemoData() {
        Map<Integer, Mentor> mentors = seedMentors();
        Map<Integer, Student> students = seedStudents();
        Map<String, Company> companies = seedCompanies();
        InternshipPhase phase = seedPhase();

        List<AssignmentSeed> assignments = List.of(
                new AssignmentSeed(1, 1, "FPT Software"),
                new AssignmentSeed(2, 1, "FPT Software"),
                new AssignmentSeed(3, 1, "Viettel Telecom"),
                new AssignmentSeed(4, 1, "VNG Corporation"),
                new AssignmentSeed(5, 2, "FPT Software"),
                new AssignmentSeed(6, 2, "FPT Software"),
                new AssignmentSeed(7, 2, "Viettel Telecom"),
                new AssignmentSeed(8, 2, "Rikkeisoft"),
                new AssignmentSeed(9, 3, "FPT Software"),
                new AssignmentSeed(10, 3, "Viettel Telecom"),
                new AssignmentSeed(11, 3, "VNG Corporation"),
                new AssignmentSeed(12, 4, "FPT Software"),
                new AssignmentSeed(13, 4, "Viettel Telecom"),
                new AssignmentSeed(14, 4, "VNG Corporation"),
                new AssignmentSeed(15, 4, "Rikkeisoft"),
                new AssignmentSeed(16, 4, "Rikkeisoft")
        );

        assignments.forEach(seed -> seedAssignment(
                students.get(seed.studentIndex()),
                mentors.get(seed.mentorIndex()),
                phase,
                companies.get(seed.companyName())
        ));
    }

    private Map<Integer, Mentor> seedMentors() {
        Map<Integer, Mentor> mentors = new LinkedHashMap<>();
        mentors.put(1, seedMentor("mentor1", "mentor1@fpt.edu.vn", "mentor123", "Dr. Le Thi B", "Software Engineering", "Doctor"));
        mentors.put(2, seedMentor("mentor2", "mentor2@fpt.edu.vn", "mentor123", "Prof. Tran Van C", "Information Systems", "Professor"));
        mentors.put(3, seedMentor("mentor3", "mentor3@fpt.edu.vn", "mentor123", "MSc. Pham Hoang D", "Cyber Security", "Master"));
        mentors.put(4, seedMentor("mentor4", "mentor4@fpt.edu.vn", "mentor123", "Dr. Nguyen Van E", "Artificial Intelligence", "Doctor"));
        return mentors;
    }

    private Map<Integer, Student> seedStudents() {
        Map<Integer, Student> students = new LinkedHashMap<>();
        for (int index = 1; index <= 16; index++) {
            String username = "student" + index;
            String email = username + "@fpt.edu.vn";
            String code = String.format("SE17%04d", index);
            String fullName = "Student Demo " + String.format("%02d", index);
            students.put(index, seedStudent(username, email, "student123", fullName, code));
        }
        return students;
    }

    private Map<String, Company> seedCompanies() {
        Map<String, Company> companies = new LinkedHashMap<>();
        companies.put("FPT Software", seedCompany("FPT Software", "Software Outsourcing", "fpt-software.example.com", 80));
        companies.put("Viettel Telecom", seedCompany("Viettel Telecom", "Telecommunication", "viettel.example.com", 40));
        companies.put("VNG Corporation", seedCompany("VNG Corporation", "Digital Product", "vng.example.com", 30));
        companies.put("Rikkeisoft", seedCompany("Rikkeisoft", "Software Development", "rikkeisoft.example.com", 30));
        return companies;
    }

    private InternshipPhase seedPhase() {
        return phaseRepository.findByPhaseName("Spring 2026 Batch A").orElseGet(() -> {
            InternshipPhase phase = new InternshipPhase();
            phase.setPhaseName("Spring 2026 Batch A");
            phase.setStartDate(LocalDate.of(2026, 1, 5));
            phase.setEndDate(LocalDate.of(2026, 5, 31));
            phase.setDescription("Demo phase for admin dashboard analytics.");
            InternshipPhase saved = phaseRepository.save(phase);
            log.info("Dashboard demo phase created: {}", saved.getPhaseName());
            return saved;
        });
    }

    private Mentor seedMentor(String username, String email, String password, String fullName, String department, String academicRank) {
        User user = seedUserIfNotFound(username, email, password, fullName, UserRole.MENTOR);
        Mentor mentor = mentorRepository.findById(user.getUserId()).orElseGet(() -> {
            Mentor created = new Mentor();
            created.setUser(user);
            return created;
        });
        mentor.setDepartment(department);
        mentor.setAcademicRank(academicRank);
        return mentorRepository.save(mentor);
    }

    private Student seedStudent(String username, String email, String password, String fullName, String studentCode) {
        User user = seedUserIfNotFound(username, email, password, fullName, UserRole.STUDENT);
        Student student = studentRepository.findById(user.getUserId()).orElseGet(() -> Student.builder()
                .user(user)
                .studentCode(studentCode)
                .build());
        student.setUser(user);
        student.setStudentCode(studentCode);
        student.setMajor("Software Engineering");
        student.setClassName("SE17-Demo");
        return studentRepository.save(student);
    }

    private Company seedCompany(String companyName, String industry, String website, int maxInterns) {
        Company company = companyRepository.findByCompanyName(companyName).orElseGet(() -> Company.builder()
                .companyName(companyName)
                .build());
        company.setIndustry(industry);
        company.setWebsite("https://" + website);
        company.setAddress("Ho Chi Minh City");
        company.setContactPerson("HR Department");
        company.setContactEmail("hr@" + website);
        company.setContactPhone("0900000000");
        company.setMaxInterns(maxInterns);
        company.setIsActive(true);
        return companyRepository.save(company);
    }

    private void seedAssignment(Student student, Mentor mentor, InternshipPhase phase, Company company) {
        InternshipAssignment assignment = assignmentRepository
                .findByStudentStudentIdAndPhasePhaseId(student.getStudentId(), phase.getPhaseId())
                .orElseGet(InternshipAssignment::new);
        assignment.setStudent(student);
        assignment.setMentor(mentor);
        assignment.setPhase(phase);
        assignment.setCompany(company);
        assignment.setStatus(AssignmentStatus.IN_PROGRESS);
        assignmentRepository.save(assignment);
    }

    private User seedUserIfNotFound(String username, String email, String password, String fullName, UserRole role) {
        return userRepository.findByUsername(username).map(user -> {
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(role);
            user.setIsActive(true);
            User saved = userRepository.save(user);
            log.info("Default {} account updated: username='{}'", role, username);
            return saved;
        }).orElseGet(() -> {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .fullName(fullName)
                    .role(role)
                    .isActive(true)
                    .build();
            User saved = userRepository.save(user);
            log.info("Default {} account created: username='{}'", role, username);
            return saved;
        });
    }

    private void seedRbacAndFeatureFlags() {
        Role adminRole = seedRoleIfNotFound("ADMIN", "Administrator", "System Administrator with full access", true);
        Role mentorRole = seedRoleIfNotFound("MENTOR", "Mentor", "Internship Mentor managing assigned students and grading", true);
        Role studentRole = seedRoleIfNotFound("STUDENT", "Student", "Internship Student participating in internship programs", true);

        // Permissions
        List<PermissionSeed> permissionSeeds = List.of(
                // User
                new PermissionSeed("USER_VIEW", "USER", "VIEW", "View users list and detail"),
                new PermissionSeed("USER_CREATE", "USER", "CREATE", "Create new user accounts"),
                new PermissionSeed("USER_UPDATE", "USER", "UPDATE", "Update existing user accounts"),
                new PermissionSeed("USER_DELETE", "USER", "DELETE", "Delete user accounts"),
                new PermissionSeed("USER_CHANGE_STATUS", "USER", "CHANGE_STATUS", "Activate or deactivate user accounts"),
                new PermissionSeed("USER_CHANGE_ROLE", "USER", "CHANGE_ROLE", "Change user system roles"),
                // Student
                new PermissionSeed("STUDENT_VIEW", "STUDENT", "VIEW", "View students list"),
                new PermissionSeed("STUDENT_CREATE", "STUDENT", "CREATE", "Create student profiles"),
                new PermissionSeed("STUDENT_UPDATE", "STUDENT", "UPDATE", "Update student profiles"),
                new PermissionSeed("STUDENT_DELETE", "STUDENT", "DELETE", "Delete student profiles"),
                new PermissionSeed("STUDENT_VIEW_DETAIL", "STUDENT", "VIEW_DETAIL", "View detailed student profile"),
                // Mentor
                new PermissionSeed("MENTOR_VIEW", "MENTOR", "VIEW", "View mentors list"),
                new PermissionSeed("MENTOR_CREATE", "MENTOR", "CREATE", "Create mentor profiles"),
                new PermissionSeed("MENTOR_UPDATE", "MENTOR", "UPDATE", "Update mentor profiles"),
                new PermissionSeed("MENTOR_DELETE", "MENTOR", "DELETE", "Delete mentor profiles"),
                new PermissionSeed("MENTOR_VIEW_DETAIL", "MENTOR", "VIEW_DETAIL", "View detailed mentor profile"),
                // Company
                new PermissionSeed("COMPANY_VIEW", "COMPANY", "VIEW", "View companies list and detail"),
                new PermissionSeed("COMPANY_CREATE", "COMPANY", "CREATE", "Create company records"),
                new PermissionSeed("COMPANY_UPDATE", "COMPANY", "UPDATE", "Update company records"),
                new PermissionSeed("COMPANY_DELETE", "COMPANY", "DELETE", "Delete company records"),
                new PermissionSeed("COMPANY_CHANGE_STATUS", "COMPANY", "CHANGE_STATUS", "Change company active status"),
                // Phase
                new PermissionSeed("PHASE_VIEW", "PHASE", "VIEW", "View internship phases"),
                new PermissionSeed("PHASE_CREATE", "PHASE", "CREATE", "Create internship phases"),
                new PermissionSeed("PHASE_UPDATE", "PHASE", "UPDATE", "Update internship phases"),
                new PermissionSeed("PHASE_DELETE", "PHASE", "DELETE", "Delete internship phases"),
                new PermissionSeed("PHASE_CHANGE_STATUS", "PHASE", "CHANGE_STATUS", "Change internship phase status"),
                // Assignment
                new PermissionSeed("ASSIGNMENT_VIEW", "ASSIGNMENT", "VIEW", "View internship assignments"),
                new PermissionSeed("ASSIGNMENT_CREATE", "ASSIGNMENT", "CREATE", "Create internship assignments"),
                new PermissionSeed("ASSIGNMENT_UPDATE", "ASSIGNMENT", "UPDATE", "Update internship assignments"),
                new PermissionSeed("ASSIGNMENT_DELETE", "ASSIGNMENT", "DELETE", "Delete internship assignments"),
                new PermissionSeed("ASSIGNMENT_CHANGE_STATUS", "ASSIGNMENT", "CHANGE_STATUS", "Change internship assignment status"),
                // Submission
                new PermissionSeed("SUBMISSION_VIEW", "SUBMISSION", "VIEW", "View submissions"),
                new PermissionSeed("SUBMISSION_CREATE", "SUBMISSION", "CREATE", "Submit assignment github or zip"),
                new PermissionSeed("SUBMISSION_UPDATE", "SUBMISSION", "UPDATE", "Update submissions"),
                new PermissionSeed("SUBMISSION_DELETE", "SUBMISSION", "DELETE", "Delete submissions"),
                new PermissionSeed("SUBMISSION_DOWNLOAD", "SUBMISSION", "DOWNLOAD", "Download submitted zip files"),
                new PermissionSeed("SUBMISSION_OPEN_LINK", "SUBMISSION", "OPEN_LINK", "Open submission github link"),
                // Assessment
                new PermissionSeed("ASSESSMENT_VIEW", "ASSESSMENT", "VIEW", "View assessment criteria, rounds, and results"),
                new PermissionSeed("ASSESSMENT_CREATE", "ASSESSMENT", "CREATE", "Create assessment rounds or criteria"),
                new PermissionSeed("ASSESSMENT_UPDATE", "ASSESSMENT", "UPDATE", "Update assessment rounds or criteria"),
                new PermissionSeed("ASSESSMENT_DELETE", "ASSESSMENT", "DELETE", "Delete assessment rounds or criteria"),
                new PermissionSeed("ASSESSMENT_SCORE", "ASSESSMENT", "SCORE", "Grade and score student assessments"),
                new PermissionSeed("ASSESSMENT_PUBLISH", "ASSESSMENT", "PUBLISH", "Publish assessment results to students"),
                // Group
                new PermissionSeed("GROUP_VIEW", "GROUP", "VIEW", "View mentor groups"),
                new PermissionSeed("GROUP_CREATE", "GROUP", "CREATE", "Create mentor groups"),
                new PermissionSeed("GROUP_UPDATE", "GROUP", "UPDATE", "Update mentor groups"),
                new PermissionSeed("GROUP_DELETE", "GROUP", "DELETE", "Delete mentor groups"),
                new PermissionSeed("GROUP_MEMBER_ADD", "GROUP", "MEMBER_ADD", "Add members to mentor group"),
                new PermissionSeed("GROUP_MEMBER_REMOVE", "GROUP", "MEMBER_REMOVE", "Remove members from mentor group"),
                new PermissionSeed("GROUP_JOIN", "GROUP", "JOIN", "Join mentor group by join code"),
                // System Config
                new PermissionSeed("ROLE_PERMISSION_VIEW", "SYSTEM_CONFIG", "VIEW", "View role permissions and feature flags"),
                new PermissionSeed("ROLE_PERMISSION_UPDATE", "SYSTEM_CONFIG", "UPDATE", "Update role permissions"),
                new PermissionSeed("FEATURE_FLAG_VIEW", "SYSTEM_CONFIG", "VIEW", "View system feature flags"),
                new PermissionSeed("FEATURE_FLAG_UPDATE", "SYSTEM_CONFIG", "UPDATE", "Update system feature flags")
        );

        Map<String, Permission> permissionMap = new HashMap<>();
        for (PermissionSeed ps : permissionSeeds) {
            Permission p = permissionRepository.findByPermissionCode(ps.code())
                    .map(existing -> {
                        existing.setModuleCode(ps.module());
                        existing.setActionCode(ps.action());
                        existing.setDescription(ps.desc());
                        return permissionRepository.save(existing);
                    })
                    .orElseGet(() -> permissionRepository.save(Permission.builder()
                            .permissionCode(ps.code())
                            .moduleCode(ps.module())
                            .actionCode(ps.action())
                            .description(ps.desc())
                            .isActive(true)
                            .build()));
            permissionMap.put(p.getPermissionCode(), p);
        }

        // Grant to ADMIN: ALL
        for (Permission p : permissionMap.values()) {
            grantPermissionIfNotFound(adminRole, p, true);
        }

        // Grant to MENTOR
        Set<String> mentorPerms = Set.of(
                "STUDENT_VIEW", "STUDENT_VIEW_DETAIL",
                "MENTOR_VIEW", "MENTOR_VIEW_DETAIL",
                "COMPANY_VIEW",
                "PHASE_VIEW",
                "ASSIGNMENT_VIEW",
                "SUBMISSION_VIEW", "SUBMISSION_DOWNLOAD", "SUBMISSION_OPEN_LINK",
                "ASSESSMENT_VIEW", "ASSESSMENT_SCORE",
                "GROUP_VIEW", "GROUP_CREATE", "GROUP_UPDATE", "GROUP_MEMBER_ADD", "GROUP_MEMBER_REMOVE"
        );
        for (Permission p : permissionMap.values()) {
            grantPermissionIfNotFound(mentorRole, p, mentorPerms.contains(p.getPermissionCode()));
        }

        // Grant to STUDENT
        Set<String> studentPerms = Set.of(
                "STUDENT_VIEW_DETAIL",
                "COMPANY_VIEW",
                "PHASE_VIEW",
                "SUBMISSION_VIEW", "SUBMISSION_CREATE", "SUBMISSION_UPDATE", "SUBMISSION_DOWNLOAD", "SUBMISSION_OPEN_LINK",
                "ASSESSMENT_VIEW",
                "GROUP_VIEW", "GROUP_JOIN"
        );
        for (Permission p : permissionMap.values()) {
            grantPermissionIfNotFound(studentRole, p, studentPerms.contains(p.getPermissionCode()));
        }

        // System Features
        List<FeatureSeed> featureSeeds = List.of(
                new FeatureSeed("STUDENT_PROFILE_UPDATE_ENABLED", "STUDENT", "Cập nhật hồ sơ sinh viên", "Cho phép sinh viên cập nhật thông tin cá nhân", true, Set.of("ADMIN", "STUDENT")),
                new FeatureSeed("STUDENT_SUBMISSION_ENABLED", "SUBMISSION", "Cổng nộp bài tập / đồ án", "Cho phép sinh viên nộp bài / cập nhật link / file", true, Set.of("ADMIN", "STUDENT")),
                new FeatureSeed("STUDENT_VIEW_SCORE_ENABLED", "ASSESSMENT", "Sinh viên xem điểm đánh giá", "Cho phép sinh viên xem điểm các vòng đánh giá", true, Set.of("ADMIN", "STUDENT")),
                new FeatureSeed("STUDENT_JOIN_GROUP_ENABLED", "GROUP", "Tham gia nhóm mentor bằng mã", "Cho phép sinh viên nhập mã tham gia nhóm", true, Set.of("ADMIN", "STUDENT")),
                new FeatureSeed("MENTOR_SCORING_ENABLED", "ASSESSMENT", "Mentor chấm điểm / đánh giá", "Cho phép mentor lưu nháp và submit điểm rubric", true, Set.of("ADMIN", "MENTOR")),
                new FeatureSeed("MENTOR_GROUP_ENABLED", "GROUP", "Mentor quản lý nhóm hướng dẫn", "Cho phép mentor tạo nhóm và quản lý thành viên", true, Set.of("ADMIN", "MENTOR")),
                new FeatureSeed("WEEKLY_REPORT_SUBMISSION_ENABLED", "REPORT", "Nộp báo cáo tiến độ tuần", "Cho phép sinh viên tạo và nộp báo cáo tuần", true, Set.of("ADMIN", "STUDENT")),
                new FeatureSeed("APPLICATION_REGISTRATION_ENABLED", "APPLICATION", "Đăng ký ứng tuyển thực tập", "Cho phép sinh viên tạo đơn ứng tuyển", true, Set.of("ADMIN", "STUDENT")),
                new FeatureSeed("ASSESSMENT_RESULT_PUBLISHING_ENABLED", "ASSESSMENT", "Công bố điểm đánh giá", "Cho phép Admin công bố điểm cho toàn thể sinh viên", true, Set.of("ADMIN"))
        );

        Map<String, Role> roleMap = Map.of(
                "ADMIN", adminRole,
                "MENTOR", mentorRole,
                "STUDENT", studentRole
        );

        for (FeatureSeed fs : featureSeeds) {
            SystemFeature feature = systemFeatureRepository.findByFeatureCode(fs.code())
                    .map(existing -> {
                        existing.setModuleCode(fs.module());
                        existing.setFeatureName(fs.name());
                        existing.setDescription(fs.desc());
                        existing.setEnabled(fs.defaultEnabled());
                        return systemFeatureRepository.save(existing);
                    })
                    .orElseGet(() -> systemFeatureRepository.save(SystemFeature.builder()
                            .featureCode(fs.code())
                            .moduleCode(fs.module())
                            .featureName(fs.name())
                            .description(fs.desc())
                            .enabled(fs.defaultEnabled())
                            .isRuntimeConfigurable(true)
                            .build()));

            for (Map.Entry<String, Role> entry : roleMap.entrySet()) {
                String rCode = entry.getKey();
                Role r = entry.getValue();
                boolean enabledForRole = fs.allowedRoles().contains(rCode);
                roleFeatureRepository.findByRoleAndFeature(r, feature)
                        .orElseGet(() -> roleFeatureRepository.save(RoleFeature.builder()
                                .role(r)
                                .feature(feature)
                                .enabled(enabledForRole)
                                .build()));
            }
        }
        log.info("Dynamic RBAC and Feature Flags seeded successfully");
    }

    private Role seedRoleIfNotFound(String code, String name, String desc, boolean isSystem) {
        return roleRepository.findByRoleCode(code)
                .map(r -> {
                    r.setRoleName(name);
                    r.setDescription(desc);
                    return roleRepository.save(r);
                })
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleCode(code)
                        .roleName(name)
                        .description(desc)
                        .isSystem(isSystem)
                        .isActive(true)
                        .build()));
    }

    private void grantPermissionIfNotFound(Role role, Permission permission, boolean granted) {
        rolePermissionRepository.findByRoleAndPermission(role, permission)
                .ifPresentOrElse(
                        rp -> {},
                        () -> rolePermissionRepository.save(RolePermission.builder()
                                .role(role)
                                .permission(permission)
                                .granted(granted)
                                .build())
                );
    }

    private record PermissionSeed(String code, String module, String action, String desc) {}
    private record FeatureSeed(String code, String module, String name, String desc, boolean defaultEnabled, Set<String> allowedRoles) {}

    private record AssignmentSeed(int studentIndex, int mentorIndex, String companyName) {
    }
}
