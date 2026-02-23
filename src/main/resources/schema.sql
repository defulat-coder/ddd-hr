CREATE TABLE IF NOT EXISTS employees (
    id VARCHAR(64) PRIMARY KEY,
    employee_number VARCHAR(64) NOT NULL UNIQUE,
    first_name VARCHAR(64),
    last_name VARCHAR(64),
    id_card_number VARCHAR(64),
    birth_date DATE,
    gender VARCHAR(32),
    email VARCHAR(128),
    phone_number VARCHAR(32),
    address VARCHAR(255),
    emergency_contact VARCHAR(64),
    emergency_phone VARCHAR(32),
    department_id VARCHAR(64),
    position_id VARCHAR(64),
    status VARCHAR(32),
    hire_date DATE,
    probation_end_date DATE,
    resign_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
);

CREATE TABLE IF NOT EXISTS departments (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    code VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    parent_id VARCHAR(64),
    manager_id VARCHAR(64),
    description VARCHAR(255),
    active BOOLEAN,
    positions_json CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
);

CREATE TABLE IF NOT EXISTS goals (
    id VARCHAR(64) PRIMARY KEY,
    employee_id VARCHAR(64) NOT NULL,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    start_date DATE,
    end_date DATE,
    registration_deadline DATE,
    status VARCHAR(32),
    objectives_json CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
);

CREATE TABLE IF NOT EXISTS benefits (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(32) NOT NULL,
    employer_cost DECIMAL(19,2),
    employee_cost DECIMAL(19,2),
    active BOOLEAN,
    eligibility_criteria VARCHAR(500),
    enrollments_json CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
);

CREATE TABLE IF NOT EXISTS culture_activities (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(32) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    registration_deadline TIMESTAMP,
    location VARCHAR(255),
    organizer_id VARCHAR(64),
    max_participants INT,
    budget DECIMAL(19,2),
    status VARCHAR(32),
    participations_json CLOB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
);
