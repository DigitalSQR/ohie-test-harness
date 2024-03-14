--create yser with super admin.
--
--@author dhruv
--@since 2023-09-13

INSERT INTO role (id, name)
VALUES ('role.admin', 'Admin'),
       ('role.tester', 'Tester'),
       ('role.assessee', 'Assessee');

INSERT INTO tht_user (id,
                      email,
                      name,
                      company_name,
                      state,
                      password,
                      created_by,
                      updated_by,
                      created_at,
                      updated_at,
                      version)
VALUES ('SYSTEM_USER',
        'noreplytestharnesstool@gmail.com',
        'Testing Harness Tool',
        'Argusoft.Path',
        'user.status.active',
        '$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);

INSERT INTO role_tht_user (user_id, role_id)
VALUES ('SYSTEM_USER',
        'role.admin');


--audit data
INSERT INTO role_aud (id, rev, revtype, name)
VALUES ('role.admin', 1, 0, 'Admin'),
       ('role.tester', 1, 0, 'Tester'),
       ('role.assessee', 1, 0, 'Assessee');

INSERT INTO tht_user_aud(id,
                         rev,
                         revtype,
                         email,
                         name,
                         state,
                         password,
                         created_by,
                         updated_by,
                         created_at,
                         updated_at,
                         version)
VALUES ('SYSTEM_USER',
        1,
        0,
        'noreplytestharnesstool@gmail.com',
        'Testing Harness Tool',
        'user.status.active',
        '$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);

INSERT INTO role_tht_user_aud (rev, user_id, role_id, revtype)
VALUES (1,
        'SYSTEM_USER',
        'role.admin',
        0);
