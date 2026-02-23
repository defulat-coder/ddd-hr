INSERT INTO departments(id, name, parent_department_id, level, leader, staffing_quota, effective_date)
VALUES
('dept-root', '总部', NULL, 1, 'CEO', 20, '2026-01-01'),
('dept-hr', '人力资源部', 'dept-root', 2, '张敏', 10, '2026-01-01'),
('dept-tech', '技术部', 'dept-root', 2, '李强', 50, '2026-01-01')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employees(
    id, employee_no, name, gender, birth_date, id_card_no, phone,
    department_id, position, onboarding_date, status,
    contract_type, contract_start_date, contract_end_date, probation_months, contract_signed_date
) VALUES
('emp-1001', 'EMP202602230001', '王丽', 'FEMALE', '1995-06-15', '320101199506155678', '13800000001',
 'dept-hr', 'HR专员', '2026-02-01', 'ACTIVE',
 'FULL_TIME', '2026-02-01', '2029-01-31', 3, '2026-01-25'),
('emp-1002', 'EMP202602230002', '赵峰', 'MALE', '1993-03-20', '320101199303207654', '13800000002',
 'dept-tech', '后端工程师', '2026-02-01', 'PROBATION',
 'FULL_TIME', '2026-02-01', '2029-01-31', 3, '2026-01-25')
ON CONFLICT (id) DO NOTHING;

INSERT INTO leave_balances(employee_id, leave_type, balance_days)
VALUES
('emp-1001', 'ANNUAL', 10),
('emp-1001', 'SICK', 5),
('emp-1002', 'ANNUAL', 8),
('emp-1002', 'SICK', 5)
ON CONFLICT (employee_id, leave_type) DO NOTHING;

INSERT INTO salary_profiles(employee_id, base_salary, position_salary, performance_salary, allowance)
VALUES
('emp-1001', 12000.00, 3000.00, 2000.00, 800.00),
('emp-1002', 15000.00, 4000.00, 2500.00, 1000.00)
ON CONFLICT (employee_id) DO NOTHING;

INSERT INTO attendance_locks(period)
VALUES ('2026-01')
ON CONFLICT (period) DO NOTHING;
