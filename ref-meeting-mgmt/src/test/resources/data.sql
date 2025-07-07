INSERT INTO app_user (id, username, password_hash, role)
VALUES (1, 'manager', 'hash1', 'MANAGER'),
       (2, 'employee', 'hash2', 'EMPLOYEE');

INSERT INTO meeting (id, title, start_date_time, end_date_time, is_finalized, manager_id, employee_id)
VALUES (10, 'Test Meeting', '2025-07-01T10:00:00', '2025-07-01T11:00:00', false, 1, 2);