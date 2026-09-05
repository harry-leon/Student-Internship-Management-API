ROLLBACK;
BEGIN;


TRUNCATE TABLE
    public.assessment_results,
    public.round_criteria,
    public.assessment_rounds,
    public.internship_assignments,
    public.evaluation_criteria,
    public.internship_phases,
    public.mentors,
    public.students,
    public.users
    RESTART IDENTITY CASCADE;

-- =========================================================
-- 1. users
-- =========================================================
INSERT INTO users
(username, password_hash, full_name, email, phone_number, role, is_active, created_at, updated_at)
VALUES
    ('admin01',   '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Quản trị hệ thống', 'admin@internship.local',       '0901000001', 'ADMIN',   true, '2026-05-01 08:00:00', '2026-05-01 08:00:00'),
    ('mentor01',  '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Nguyễn Minh Anh',   'minhanh@internship.local',     '0902000001', 'MENTOR',  true, '2026-05-01 08:05:00', '2026-05-01 08:05:00'),
    ('mentor02',  '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Trần Quốc Bảo',    'quocbao@internship.local',     '0902000002', 'MENTOR',  true, '2026-05-01 08:10:00', '2026-05-01 08:10:00'),
    ('student01', '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Nguyễn Văn An',    'an.nguyen@student.local',      '0911000001', 'STUDENT', true, '2026-05-01 08:15:00', '2026-05-01 08:15:00'),
    ('student02', '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Trần Thị Bình',    'binh.tran@student.local',      '0911000002', 'STUDENT', true, '2026-05-01 08:20:00', '2026-05-01 08:20:00'),
    ('student03', '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Lê Minh Châu',     'chau.le@student.local',        '0911000003', 'STUDENT', true, '2026-05-01 08:25:00', '2026-05-01 08:25:00'),
    ('student04', '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Phạm Gia Huy',     'huy.pham@student.local',       '0911000004', 'STUDENT', true, '2026-05-01 08:30:00', '2026-05-01 08:30:00'),
    ('student05', '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Võ Khánh Linh',    'linh.vo@student.local',        '0911000005', 'STUDENT', true, '2026-05-01 08:35:00', '2026-05-01 08:35:00'),
    ('student06', '$2y$10$6qp.ySftwfjLwbGVmlyW9eqhSW/kll0Z5ts4R2WTpFAt9DQdJouZO', 'Đặng Quốc Nam',    'nam.dang@student.local',       '0911000006', 'STUDENT', true, '2026-05-01 08:40:00', '2026-05-01 08:40:00');

-- =========================================================
-- 2. students
-- =========================================================
INSERT INTO students
(studentid, student_code, major, class, date_of_birth, address, created_at, updated_at)
SELECT
    u.userid,
    v.student_code,
    v.major,
    v.class,
    v.date_of_birth,
    v.address,
    v.created_at,
    v.updated_at
FROM (
         VALUES
             ('student01', 'SE181001', 'Kỹ thuật phần mềm', 'SE18A', DATE '2004-01-15', 'TP. Hồ Chí Minh', TIMESTAMP '2026-05-01 08:15:00', TIMESTAMP '2026-05-01 08:15:00'),
             ('student02', 'SE181002', 'Kỹ thuật phần mềm', 'SE18A', DATE '2004-04-22', 'Bình Dương', TIMESTAMP '2026-05-01 08:20:00', TIMESTAMP '2026-05-01 08:20:00'),
             ('student03', 'SE181003', 'Kỹ thuật phần mềm', 'SE18B', DATE '2004-07-09', 'Đồng Nai', TIMESTAMP '2026-05-01 08:25:00', TIMESTAMP '2026-05-01 08:25:00'),
             ('student04', 'SE181004', 'Kỹ thuật phần mềm', 'SE18B', DATE '2004-09-18', 'TP. Hồ Chí Minh', TIMESTAMP '2026-05-01 08:30:00', TIMESTAMP '2026-05-01 08:30:00'),
             ('student05', 'SE181005', 'Hệ thống thông tin', 'IS18A', DATE '2004-11-03', 'Long An', TIMESTAMP '2026-05-01 08:35:00', TIMESTAMP '2026-05-01 08:35:00'),
             ('student06', 'SE181006', 'Kỹ thuật phần mềm', 'SE18C', DATE '2004-12-25', 'Tây Ninh', TIMESTAMP '2026-05-01 08:40:00', TIMESTAMP '2026-05-01 08:40:00')
     ) AS v(username, student_code, major, class, date_of_birth, address, created_at, updated_at)
         JOIN users u ON u.username = v.username;

-- =========================================================
-- 3. mentors
-- =========================================================
INSERT INTO mentors
(mentorid, department, academic_rank, created_at, updated_at)
SELECT
    u.userid,
    v.department,
    v.academic_rank,
    v.created_at,
    v.updated_at
FROM (
         VALUES
             ('mentor01', 'Khoa Công nghệ Thông tin', 'ThS', TIMESTAMP '2026-05-01 08:05:00', TIMESTAMP '2026-05-01 08:05:00'),
             ('mentor02', 'Khoa Công nghệ Thông tin', 'TS', TIMESTAMP '2026-05-01 08:10:00', TIMESTAMP '2026-05-01 08:10:00')
     ) AS v(username, department, academic_rank, created_at, updated_at)
         JOIN users u ON u.username = v.username;
-- =========================================================
-- 4. internship_phases
-- =========================================================
INSERT INTO internship_phases
(phase_name, start_date, end_date, description, created_at, updated_at)
VALUES
    ('Thực tập cơ sở Summer 2026', '2026-05-04', '2026-07-31', 'Giai đoạn thực tập cơ sở giúp sinh viên làm quen với quy trình làm việc và báo cáo tiến độ.', '2026-05-01 09:00:00', '2026-05-01 09:00:00'),
    ('Thực tập tốt nghiệp Fall 2026', '2026-08-17', '2026-12-11', 'Giai đoạn thực tập tốt nghiệp tập trung vào công việc thực tế, chuyên môn và đánh giá cuối kỳ.', '2026-08-01 09:00:00', '2026-08-01 09:00:00');

-- =========================================================
-- 5. evaluation_criteria
-- =========================================================
INSERT INTO evaluation_criteria
(criterion_name, description, max_score, created_at, updated_at)
VALUES
    ('Thái độ làm việc', 'Đánh giá tính chủ động, trách nhiệm, kỷ luật và thái độ trong quá trình thực tập.', 10.00, '2026-05-01 09:10:00', '2026-05-01 09:10:00'),
    ('Kiến thức chuyên môn', 'Đánh giá khả năng áp dụng kiến thức chuyên môn để giải quyết công việc được giao.', 10.00, '2026-05-01 09:11:00', '2026-05-01 09:11:00'),
    ('Tiến độ và chất lượng công việc', 'Đánh giá mức độ hoàn thành nhiệm vụ đúng thời hạn và chất lượng sản phẩm.', 10.00, '2026-05-01 09:12:00', '2026-05-01 09:12:00'),
    ('Kỹ năng giao tiếp và báo cáo', 'Đánh giá khả năng trao đổi với mentor, làm việc nhóm và trình bày báo cáo.', 10.00, '2026-05-01 09:13:00', '2026-05-01 09:13:00');

-- =========================================================
-- 6. assessment_rounds
-- =========================================================
INSERT INTO assessment_rounds
(phaseid, round_name, start_date, end_date, description, is_active, created_at, updated_at)
SELECT p.phaseid, v.round_name, v.start_date, v.end_date, v.description, v.is_active, v.created_at, v.updated_at
FROM (
         VALUES
             ('Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', '2026-06-08'::date, '2026-06-14'::date, 'Đánh giá tiến độ giữa giai đoạn thực tập cơ sở.', false, '2026-06-01 08:00:00'::timestamp, '2026-06-01 08:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', '2026-07-20'::date, '2026-07-26'::date, 'Đánh giá tổng kết giai đoạn thực tập cơ sở.', false, '2026-07-01 08:00:00'::timestamp, '2026-07-01 08:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', '2026-08-24'::date, '2026-08-30'::date, 'Đánh giá mức độ thích nghi và tiến độ ban đầu của sinh viên.', true, '2026-08-20 08:00:00'::timestamp, '2026-08-20 08:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá giữa kỳ', '2026-10-05'::date, '2026-10-11'::date, 'Đánh giá tiến độ giữa giai đoạn thực tập tốt nghiệp.', false, '2026-09-20 08:00:00'::timestamp, '2026-09-20 08:00:00'::timestamp)
     ) AS v(phase_name, round_name, start_date, end_date, description, is_active, created_at, updated_at)
         JOIN internship_phases p ON p.phase_name = v.phase_name;

-- =========================================================
-- 7. round_criteria
-- =========================================================
INSERT INTO round_criteria
(roundid, criterionid, weight, created_at, updated_at)
SELECT
    ar.roundid,
    ec.criterionid,
    v.weight,
    v.created_at,
    v.updated_at
FROM (
         VALUES
             ('Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Thái độ làm việc', 0.20, '2026-06-01 09:00:00'::timestamp, '2026-06-01 09:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kiến thức chuyên môn', 0.35, '2026-06-01 09:00:00'::timestamp, '2026-06-01 09:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Tiến độ và chất lượng công việc', 0.30, '2026-06-01 09:00:00'::timestamp, '2026-06-01 09:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kỹ năng giao tiếp và báo cáo', 0.15, '2026-06-01 09:00:00'::timestamp, '2026-06-01 09:00:00'::timestamp),

             ('Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Thái độ làm việc', 0.20, '2026-07-01 09:00:00'::timestamp, '2026-07-01 09:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kiến thức chuyên môn', 0.35, '2026-07-01 09:00:00'::timestamp, '2026-07-01 09:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Tiến độ và chất lượng công việc', 0.30, '2026-07-01 09:00:00'::timestamp, '2026-07-01 09:00:00'::timestamp),
             ('Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kỹ năng giao tiếp và báo cáo', 0.15, '2026-07-01 09:00:00'::timestamp, '2026-07-01 09:00:00'::timestamp),

             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Thái độ làm việc', 0.25, '2026-08-20 09:00:00'::timestamp, '2026-08-20 09:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kiến thức chuyên môn', 0.30, '2026-08-20 09:00:00'::timestamp, '2026-08-20 09:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Tiến độ và chất lượng công việc', 0.25, '2026-08-20 09:00:00'::timestamp, '2026-08-20 09:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kỹ năng giao tiếp và báo cáo', 0.20, '2026-08-20 09:00:00'::timestamp, '2026-08-20 09:00:00'::timestamp),

             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá giữa kỳ', 'Thái độ làm việc', 0.20, '2026-09-20 09:00:00'::timestamp, '2026-09-20 09:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá giữa kỳ', 'Kiến thức chuyên môn', 0.35, '2026-09-20 09:00:00'::timestamp, '2026-09-20 09:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá giữa kỳ', 'Tiến độ và chất lượng công việc', 0.30, '2026-09-20 09:00:00'::timestamp, '2026-09-20 09:00:00'::timestamp),
             ('Thực tập tốt nghiệp Fall 2026', 'Đánh giá giữa kỳ', 'Kỹ năng giao tiếp và báo cáo', 0.15, '2026-09-20 09:00:00'::timestamp, '2026-09-20 09:00:00'::timestamp)
     ) AS v(phase_name, round_name, criterion_name, weight, created_at, updated_at)
         JOIN internship_phases p ON p.phase_name = v.phase_name
         JOIN assessment_rounds ar ON ar.phaseid = p.phaseid AND ar.round_name = v.round_name
         JOIN evaluation_criteria ec ON ec.criterion_name = v.criterion_name;

-- =========================================================
-- 8. internship_assignments
-- =========================================================
INSERT INTO internship_assignments
(studentid, mentorid, phaseid, assigned_date, status, created_at, updated_at)
SELECT
    s.studentid,
    m.mentorid,
    p.phaseid,
    v.assigned_date,
    v.status,
    v.created_at,
    v.updated_at
FROM (
         VALUES
             ('student01', 'mentor01', 'Thực tập cơ sở Summer 2026', '2026-05-01 09:00:00'::timestamp, 'COMPLETED',   '2026-05-01 09:00:00'::timestamp, '2026-07-26 10:00:00'::timestamp),
             ('student02', 'mentor01', 'Thực tập cơ sở Summer 2026', '2026-05-01 09:10:00'::timestamp, 'COMPLETED',   '2026-05-01 09:10:00'::timestamp, '2026-07-26 10:00:00'::timestamp),
             ('student03', 'mentor02', 'Thực tập cơ sở Summer 2026', '2026-05-01 09:20:00'::timestamp, 'COMPLETED',   '2026-05-01 09:20:00'::timestamp, '2026-07-26 10:00:00'::timestamp),

             ('student01', 'mentor01', 'Thực tập tốt nghiệp Fall 2026', '2026-08-14 08:30:00'::timestamp, 'IN_PROGRESS', '2026-08-14 08:30:00'::timestamp, '2026-08-14 08:30:00'::timestamp),
             ('student02', 'mentor01', 'Thực tập tốt nghiệp Fall 2026', '2026-08-14 08:35:00'::timestamp, 'IN_PROGRESS', '2026-08-14 08:35:00'::timestamp, '2026-08-14 08:35:00'::timestamp),
             ('student03', 'mentor02', 'Thực tập tốt nghiệp Fall 2026', '2026-08-14 08:40:00'::timestamp, 'IN_PROGRESS', '2026-08-14 08:40:00'::timestamp, '2026-08-14 08:40:00'::timestamp),
             ('student04', 'mentor02', 'Thực tập tốt nghiệp Fall 2026', '2026-08-14 08:45:00'::timestamp, 'IN_PROGRESS', '2026-08-14 08:45:00'::timestamp, '2026-08-14 08:45:00'::timestamp),
             ('student05', 'mentor01', 'Thực tập tốt nghiệp Fall 2026', '2026-08-14 08:50:00'::timestamp, 'PENDING',     '2026-08-14 08:50:00'::timestamp, '2026-08-14 08:50:00'::timestamp),
             ('student06', 'mentor02', 'Thực tập tốt nghiệp Fall 2026', '2026-08-14 08:55:00'::timestamp, 'IN_PROGRESS', '2026-08-14 08:55:00'::timestamp, '2026-08-14 08:55:00'::timestamp)
     ) AS v(student_username, mentor_username, phase_name, assigned_date, status, created_at, updated_at)
         JOIN users su ON su.username = v.student_username
         JOIN students s ON s.studentid = su.userid
         JOIN users mu ON mu.username = v.mentor_username
         JOIN mentors m ON m.mentorid = mu.userid
         JOIN internship_phases p ON p.phase_name = v.phase_name;

-- =========================================================
-- 9. assessment_results
-- =========================================================
INSERT INTO assessment_results
(assignmentid, roundid, criterionid, score, comments, evaluated_by, evaluation_date, created_at, updated_at)
SELECT
    ia.assignmentid,
    ar.roundid,
    ec.criterionid,
    v.score,
    v.comments,
    evaluator.userid,
    v.evaluation_date,
    v.created_at,
    v.updated_at
FROM (
         VALUES
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Thái độ làm việc', 8.50, 'Có tinh thần trách nhiệm và chủ động trong công việc.', 'mentor01', '2026-06-13 14:00:00'::timestamp, '2026-06-13 14:00:00'::timestamp, '2026-06-13 14:00:00'::timestamp),
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kiến thức chuyên môn', 8.00, 'Nắm kiến thức nền tảng tốt, cần cải thiện xử lý các tình huống phức tạp.', 'mentor01', '2026-06-13 14:05:00'::timestamp, '2026-06-13 14:05:00'::timestamp, '2026-06-13 14:05:00'::timestamp),
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Tiến độ và chất lượng công việc', 8.20, 'Hoàn thành phần lớn nhiệm vụ đúng tiến độ.', 'mentor01', '2026-06-13 14:10:00'::timestamp, '2026-06-13 14:10:00'::timestamp, '2026-06-13 14:10:00'::timestamp),
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kỹ năng giao tiếp và báo cáo', 7.80, 'Báo cáo rõ ràng, cần trao đổi thường xuyên hơn với nhóm.', 'mentor01', '2026-06-13 14:15:00'::timestamp, '2026-06-13 14:15:00'::timestamp, '2026-06-13 14:15:00'::timestamp),

             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Thái độ làm việc', 9.00, 'Duy trì thái độ làm việc tích cực và chuyên nghiệp.', 'mentor01', '2026-07-25 14:00:00'::timestamp, '2026-07-25 14:00:00'::timestamp, '2026-07-25 14:00:00'::timestamp),
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kiến thức chuyên môn', 8.80, 'Khả năng chuyên môn tiến bộ rõ rệt.', 'mentor01', '2026-07-25 14:05:00'::timestamp, '2026-07-25 14:05:00'::timestamp, '2026-07-25 14:05:00'::timestamp),
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Tiến độ và chất lượng công việc', 9.00, 'Hoàn thành tốt nhiệm vụ và đảm bảo chất lượng.', 'mentor01', '2026-07-25 14:10:00'::timestamp, '2026-07-25 14:10:00'::timestamp, '2026-07-25 14:10:00'::timestamp),
             ('student01', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.50, 'Giao tiếp và trình bày báo cáo tốt.', 'mentor01', '2026-07-25 14:15:00'::timestamp, '2026-07-25 14:15:00'::timestamp, '2026-07-25 14:15:00'::timestamp),

             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Thái độ làm việc', 8.00, 'Thái độ nghiêm túc và tuân thủ quy định.', 'mentor01', '2026-06-13 15:00:00'::timestamp, '2026-06-13 15:00:00'::timestamp, '2026-06-13 15:00:00'::timestamp),
             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kiến thức chuyên môn', 7.50, 'Kiến thức chuyên môn đạt yêu cầu.', 'mentor01', '2026-06-13 15:05:00'::timestamp, '2026-06-13 15:05:00'::timestamp, '2026-06-13 15:05:00'::timestamp),
             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Tiến độ và chất lượng công việc', 7.80, 'Tiến độ ổn định nhưng cần chủ động hơn.', 'mentor01', '2026-06-13 15:10:00'::timestamp, '2026-06-13 15:10:00'::timestamp, '2026-06-13 15:10:00'::timestamp),
             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.20, 'Giao tiếp tốt và phối hợp khá hiệu quả.', 'mentor01', '2026-06-13 15:15:00'::timestamp, '2026-06-13 15:15:00'::timestamp, '2026-06-13 15:15:00'::timestamp),

             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Thái độ làm việc', 8.70, 'Có cải thiện tốt về tính chủ động.', 'mentor01', '2026-07-25 15:00:00'::timestamp, '2026-07-25 15:00:00'::timestamp, '2026-07-25 15:00:00'::timestamp),
             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kiến thức chuyên môn', 8.20, 'Áp dụng kiến thức tốt hơn vào công việc thực tế.', 'mentor01', '2026-07-25 15:05:00'::timestamp, '2026-07-25 15:05:00'::timestamp, '2026-07-25 15:05:00'::timestamp),
             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Tiến độ và chất lượng công việc', 8.50, 'Hoàn thành nhiệm vụ đúng hạn và ít lỗi.', 'mentor01', '2026-07-25 15:10:00'::timestamp, '2026-07-25 15:10:00'::timestamp, '2026-07-25 15:10:00'::timestamp),
             ('student02', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.60, 'Báo cáo rõ ràng và phối hợp tốt với mentor.', 'mentor01', '2026-07-25 15:15:00'::timestamp, '2026-07-25 15:15:00'::timestamp, '2026-07-25 15:15:00'::timestamp),

             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Thái độ làm việc', 9.00, 'Rất chủ động và có trách nhiệm.', 'mentor02', '2026-06-14 09:00:00'::timestamp, '2026-06-14 09:00:00'::timestamp, '2026-06-14 09:00:00'::timestamp),
             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kiến thức chuyên môn', 8.70, 'Kiến thức chuyên môn tốt và học nhanh.', 'mentor02', '2026-06-14 09:05:00'::timestamp, '2026-06-14 09:05:00'::timestamp, '2026-06-14 09:05:00'::timestamp),
             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Tiến độ và chất lượng công việc', 8.80, 'Hoàn thành nhiệm vụ đúng tiến độ với chất lượng tốt.', 'mentor02', '2026-06-14 09:10:00'::timestamp, '2026-06-14 09:10:00'::timestamp, '2026-06-14 09:10:00'::timestamp),
             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá giữa kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.50, 'Giao tiếp tốt và tích cực trao đổi.', 'mentor02', '2026-06-14 09:15:00'::timestamp, '2026-06-14 09:15:00'::timestamp, '2026-06-14 09:15:00'::timestamp),

             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Thái độ làm việc', 9.30, 'Duy trì thái độ làm việc rất tốt.', 'mentor02', '2026-07-26 09:00:00'::timestamp, '2026-07-26 09:00:00'::timestamp, '2026-07-26 09:00:00'::timestamp),
             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kiến thức chuyên môn', 9.10, 'Có khả năng xử lý tốt các yêu cầu chuyên môn.', 'mentor02', '2026-07-26 09:05:00'::timestamp, '2026-07-26 09:05:00'::timestamp, '2026-07-26 09:05:00'::timestamp),
             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Tiến độ và chất lượng công việc', 9.20, 'Chất lượng công việc tốt và ổn định.', 'mentor02', '2026-07-26 09:10:00'::timestamp, '2026-07-26 09:10:00'::timestamp, '2026-07-26 09:10:00'::timestamp),
             ('student03', 'Thực tập cơ sở Summer 2026', 'Đánh giá cuối kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.90, 'Báo cáo đầy đủ, rõ ràng và đúng trọng tâm.', 'mentor02', '2026-07-26 09:15:00'::timestamp, '2026-07-26 09:15:00'::timestamp, '2026-07-26 09:15:00'::timestamp),

             ('student01', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Thái độ làm việc', 8.80, 'Thích nghi nhanh với môi trường thực tập mới.', 'mentor01', '2026-08-29 10:00:00'::timestamp, '2026-08-29 10:00:00'::timestamp, '2026-08-29 10:00:00'::timestamp),
             ('student01', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kiến thức chuyên môn', 8.50, 'Kiến thức đáp ứng tốt công việc ban đầu.', 'mentor01', '2026-08-29 10:05:00'::timestamp, '2026-08-29 10:05:00'::timestamp, '2026-08-29 10:05:00'::timestamp),
             ('student01', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Tiến độ và chất lượng công việc', 8.40, 'Tiến độ tốt, cần tiếp tục duy trì.', 'mentor01', '2026-08-29 10:10:00'::timestamp, '2026-08-29 10:10:00'::timestamp, '2026-08-29 10:10:00'::timestamp),
             ('student01', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.70, 'Chủ động trao đổi với mentor và thành viên nhóm.', 'mentor01', '2026-08-29 10:15:00'::timestamp, '2026-08-29 10:15:00'::timestamp, '2026-08-29 10:15:00'::timestamp),

             ('student02', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Thái độ làm việc', 8.20, 'Có tinh thần học hỏi và thái độ tích cực.', 'mentor01', '2026-08-29 11:00:00'::timestamp, '2026-08-29 11:00:00'::timestamp, '2026-08-29 11:00:00'::timestamp),
             ('student02', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kiến thức chuyên môn', 7.90, 'Cần củng cố thêm một số kiến thức chuyên môn.', 'mentor01', '2026-08-29 11:05:00'::timestamp, '2026-08-29 11:05:00'::timestamp, '2026-08-29 11:05:00'::timestamp),
             ('student02', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Tiến độ và chất lượng công việc', 8.10, 'Hoàn thành các nhiệm vụ ban đầu đúng hạn.', 'mentor01', '2026-08-29 11:10:00'::timestamp, '2026-08-29 11:10:00'::timestamp, '2026-08-29 11:10:00'::timestamp),
             ('student02', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.30, 'Giao tiếp tốt, phản hồi công việc đầy đủ.', 'mentor01', '2026-08-29 11:15:00'::timestamp, '2026-08-29 11:15:00'::timestamp, '2026-08-29 11:15:00'::timestamp),

             ('student03', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Thái độ làm việc', 9.10, 'Chủ động, nghiêm túc và có trách nhiệm.', 'mentor02', '2026-08-30 09:00:00'::timestamp, '2026-08-30 09:00:00'::timestamp, '2026-08-30 09:00:00'::timestamp),
             ('student03', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kiến thức chuyên môn', 8.90, 'Nắm bắt yêu cầu chuyên môn nhanh.', 'mentor02', '2026-08-30 09:05:00'::timestamp, '2026-08-30 09:05:00'::timestamp, '2026-08-30 09:05:00'::timestamp),
             ('student03', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Tiến độ và chất lượng công việc', 8.80, 'Tiến độ và chất lượng công việc tốt.', 'mentor02', '2026-08-30 09:10:00'::timestamp, '2026-08-30 09:10:00'::timestamp, '2026-08-30 09:10:00'::timestamp),
             ('student03', 'Thực tập tốt nghiệp Fall 2026', 'Đánh giá đầu kỳ', 'Kỹ năng giao tiếp và báo cáo', 8.60, 'Phối hợp tốt với mentor và nhóm.', 'mentor02', '2026-08-30 09:15:00'::timestamp, '2026-08-30 09:15:00'::timestamp, '2026-08-30 09:15:00'::timestamp)
     ) AS v(student_username, phase_name, round_name, criterion_name, score, comments, evaluator_username, evaluation_date, created_at, updated_at)
         JOIN users su ON su.username = v.student_username
         JOIN students s ON s.studentid = su.userid
         JOIN internship_phases p ON p.phase_name = v.phase_name
         JOIN internship_assignments ia ON ia.studentid = s.studentid AND ia.phaseid = p.phaseid
         JOIN assessment_rounds ar ON ar.phaseid = p.phaseid AND ar.round_name = v.round_name
         JOIN evaluation_criteria ec ON ec.criterion_name = v.criterion_name
         JOIN users evaluator ON evaluator.username = v.evaluator_username;

-- =========================================================
-- 10. student_submissions (Task 07)
-- =========================================================
CREATE TABLE IF NOT EXISTS student_submissions (
    submission_id SERIAL PRIMARY KEY,
    assignment_id INTEGER NOT NULL,
    round_id INTEGER NULL,
    submitted_by INTEGER NOT NULL,
    submission_type VARCHAR(20) NOT NULL,
    github_url VARCHAR(500) NULL,
    original_file_name VARCHAR(255) NULL,
    stored_file_name VARCHAR(255) NULL,
    file_size_bytes BIGINT NULL,
    content_type VARCHAR(100) NULL,
    note TEXT NULL,
    version_no INTEGER NOT NULL DEFAULT 1,
    is_latest BOOLEAN NOT NULL DEFAULT TRUE,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_assignment FOREIGN KEY (assignment_id) REFERENCES internship_assignments(assignment_id),
    CONSTRAINT fk_submission_round FOREIGN KEY (round_id) REFERENCES assessment_rounds(round_id),
    CONSTRAINT fk_submission_user FOREIGN KEY (submitted_by) REFERENCES users(userid),
    CONSTRAINT ck_submission_type CHECK (submission_type IN ('GITHUB', 'ZIP'))
);

CREATE INDEX IF NOT EXISTS idx_submission_assignment_round ON student_submissions(assignment_id, round_id);
CREATE INDEX IF NOT EXISTS idx_submission_latest ON student_submissions(assignment_id, round_id, is_latest);
CREATE INDEX IF NOT EXISTS idx_submission_submitted_by ON student_submissions(submitted_by);

COMMIT;

SELECT * FROM users;
SELECT * FROM students;
SELECT * FROM mentors;
SELECT * FROM internship_phases;
SELECT * FROM evaluation_criteria;
SELECT * FROM assessment_rounds;
SELECT * FROM round_criteria;
SELECT * FROM internship_assignments;
SELECT * FROM assessment_results;
SELECT * FROM student_submissions;