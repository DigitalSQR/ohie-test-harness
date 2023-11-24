--create User with super admin.
--
--@author dhruv
--@since 2023-09-13

DELETE FROM tht_user;

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