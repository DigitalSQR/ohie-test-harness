--create User with super admin.
--
--@author dhruv
--@since 2023-09-13

DELETE FROM tht_user;

INSERT INTO tht_user (
    	id,
    	email,
    	name,
    	password,
    	created_by,
    	updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        '1',
        'ivasiwala@argusoft.com',
        'Istyak Ahmed Vasiwala',
	    'argusadmin',
        'THT Application',
        'THT Application',
	    Now(),
	    Now(),
	    0
    );