INSERT INTO app_user (id, username, passwordHash, email, enabled)
VALUES (1, 'admin', '$2y$10$5AbjUykf3iuiqExjhJ37j.evoqK8jRw5CITkd8sDEk05m/gxjoTYG', 'admin@reflecta.hu', true);

INSERT INTO app_user_roles (user_id, role)
VALUES
    (1, 'ADMIN'),
    (1, 'USER');