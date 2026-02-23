CREATE TABLE IF NOT EXISTS departments (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    parent_department_id VARCHAR(64),
    level INT NOT NULL,
    leader VARCHAR(64) NOT NULL,
    staffing_quota INT NOT NULL,
    effective_date DATE NOT NULL,
    CONSTRAINT fk_department_parent FOREIGN KEY (parent_department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS employees (
    id VARCHAR(64) PRIMARY KEY,
    employee_no VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(16) NOT NULL,
    birth_date DATE NOT NULL,
    id_card_no VARCHAR(32) NOT NULL UNIQUE,
    phone VARCHAR(32) NOT NULL,
    department_id VARCHAR(64) NOT NULL,
    position VARCHAR(64) NOT NULL,
    onboarding_date DATE NOT NULL,
    status VARCHAR(16) NOT NULL,
    contract_type VARCHAR(32) NOT NULL,
    contract_start_date DATE NOT NULL,
    contract_end_date DATE NOT NULL,
    probation_months INT NOT NULL,
    contract_signed_date DATE NOT NULL,
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS leave_applications (
    id VARCHAR(64) PRIMARY KEY,
    employee_id VARCHAR(64) NOT NULL,
    leave_type VARCHAR(32) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(16) NOT NULL,
    reject_reason VARCHAR(255),
    CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS leave_balances (
    employee_id VARCHAR(64) NOT NULL,
    leave_type VARCHAR(32) NOT NULL,
    balance_days INT NOT NULL,
    PRIMARY KEY (employee_id, leave_type),
    CONSTRAINT fk_leave_balance_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS salary_profiles (
    employee_id VARCHAR(64) PRIMARY KEY,
    base_salary NUMERIC(12,2) NOT NULL,
    position_salary NUMERIC(12,2) NOT NULL,
    performance_salary NUMERIC(12,2) NOT NULL,
    allowance NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_salary_profile_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS payroll_records (
    id VARCHAR(64) PRIMARY KEY,
    employee_id VARCHAR(64) NOT NULL,
    period VARCHAR(7) NOT NULL,
    gross_salary NUMERIC(12,2) NOT NULL,
    deduction NUMERIC(12,2) NOT NULL,
    social_security NUMERIC(12,2) NOT NULL,
    tax NUMERIC(12,2) NOT NULL,
    net_salary NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE INDEX IF NOT EXISTS idx_payroll_period ON payroll_records(period);

CREATE TABLE IF NOT EXISTS attendance_locks (
    period VARCHAR(7) PRIMARY KEY,
    locked_at TIMESTAMP NOT NULL DEFAULT NOW()
);
