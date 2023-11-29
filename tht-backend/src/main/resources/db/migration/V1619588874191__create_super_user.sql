--create User with super admin.
--
--@author dhruv
--@since 2023-09-13

DELETE FROM role_tht_user;
DELETE FROM role;
DELETE FROM tht_user;

INSERT INTO role (
   	    id,
    	name
    ) VALUES (
        'role.admin',
        'Admin'
    ), (
        'role.tester',
        'Tester'
    ), (
        'role.assessee',
        'Assessee'
    );

INSERT INTO tht_user (
    	id,
    	email,
    	name,
    	state,
    	password,
    	created_by,
    	updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        'SYSTEM_USER',
        'ivasiwala@argusoft.com',
        'Istyak Ahmed Vasiwala',
	    'user.status.active',
	    'password',
        'SYSTEM_USER',
        'SYSTEM_USER',
	    Now(),
	    Now(),
	    0
    );

INSERT INTO role_tht_user (
        user_id,
        role_id
     ) VALUES (
        'SYSTEM_USER',
        'role.admin'
     );