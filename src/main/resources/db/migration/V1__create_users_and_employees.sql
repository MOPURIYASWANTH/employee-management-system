CREATE TABLE app_users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(190) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_app_users_email UNIQUE (email),
  CONSTRAINT chk_app_users_role CHECK (role IN ('EMPLOYEE', 'ADMIN'))
);

CREATE TABLE employees (
  id BIGINT NOT NULL AUTO_INCREMENT,
  owner_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(190) NOT NULL,
  role VARCHAR(80) NOT NULL,
  department VARCHAR(100) NOT NULL,
  hire_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_employees_owner FOREIGN KEY (owner_id) REFERENCES app_users(id) ON DELETE CASCADE,
  CONSTRAINT uk_employees_owner_email UNIQUE (owner_id, email),
  INDEX idx_employees_owner (owner_id),
  INDEX idx_employees_department (department)
);
