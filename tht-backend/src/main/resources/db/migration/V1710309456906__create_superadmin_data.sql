--Create data for the superadmin.
--
--@author hardik

INSERT INTO role (id, name)
VALUES ('role.superadmin', 'Super Admin');

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
VALUES ('SYSTEM_SUPER_ADMIN',
        'testharnesstoolsuperadmin@gmail.com',
        'Testing Harness Tool - SuperAdmin',
        'Argusoft.Path',
        'user.status.active',
        '$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);

INSERT INTO role_tht_user (user_id, role_id)
VALUES ('SYSTEM_SUPER_ADMIN',
        'role.superadmin'),
        ('SYSTEM_SUPER_ADMIN',
                'role.admin');

--audit data
INSERT INTO role_aud (id, rev, revtype, name)
VALUES ('role.superadmin', 1, 0, 'Super Admin');

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
VALUES ('SYSTEM_SUPER_ADMIN',
        1,
        0,
        'testharnesstoolsuperadmin@gmail.com',
        'Testing Harness Tool - SuperAdmin',
        'user.status.active',
        '$2a$10$9Z2hq91BCBxqodRc82UedO.BaxXch2U6nmtyz2KkHmTZhlDTbjjWe',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);

INSERT INTO role_tht_user_aud (rev, user_id, role_id, revtype)
VALUES (1,
        'SYSTEM_SUPER_ADMIN',
        'role.superadmin',
        0), (1,
        'SYSTEM_SUPER_ADMIN',
        'role.admin',
        0);

