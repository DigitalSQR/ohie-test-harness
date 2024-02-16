INSERT INTO
    role_aud (id, rev, revtype, name)
VALUES
    ('role.admin', 1, 0, 'Admin'),
    ('role.tester', 1, 0, 'Tester'),
    ('role.assessee', 1, 0, 'Assessee');

INSERT INTO
    tht_user_aud(
        id,
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
        version
    )
VALUES
    (
        'SYSTEM_USER',
        1,
        0,
        'ivasiwala@argusoft.com',
        'Istyak Ahmed Vasiwala',
        'user.status.active',
        'password',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

INSERT INTO
    role_tht_user_aud (rev, user_id, role_id, revtype)
VALUES
    (
        1,
        'SYSTEM_USER',
        'role.admin',
        0
    );
