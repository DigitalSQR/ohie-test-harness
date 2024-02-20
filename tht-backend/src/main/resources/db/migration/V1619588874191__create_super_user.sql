--create User with super admin.
--
--@author dhruv
--@since 2023-09-13\


INSERT INTO
    revinfo(rev, revtstmp)
VALUES
    (
        1,
        EXTRACT(
            EPOCH
            FROM
                CURRENT_TIMESTAMP
        ) :: BIGINT * 1000
    );

INSERT INTO
    role (id, name)
VALUES
    ('role.admin', 'Admin'),
    ('role.tester', 'Tester'),
    (
        'role.assessee',
        'Assessee'
    );

INSERT INTO
    tht_user (
        id,
        email,
        name,
        company_name,
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
        'ivasiwala@argusoft.com',
        'Istyak Ahmed Vasiwala',
        'Argusoft.Path',
        'user.status.active',
        'password',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

INSERT INTO
    role_tht_user (user_id, role_id)
VALUES
    (
        'SYSTEM_USER',
        'role.admin'
    ),
    (
        'SYSTEM_USER',
        'role.assessee'
    );