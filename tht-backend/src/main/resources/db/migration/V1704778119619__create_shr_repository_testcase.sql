INSERT INTO component (
   	    id,
    	name,
    	description,
    	state,
    	rank,
    	created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        'component.shr.repository',
        'SHR Repository',
        'SHR Repository Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'component.status.active',
        9,
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

INSERT INTO specification (
   	    id,
    	name,
    	description,
    	state,
    	rank,
    	is_functional,
    	component_id,
    	created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        'specification.shr.shrf.8',
        'SHRF-8',
        'SHRF-8 Specification of the SHR Repository Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'specification.status.active',
        1,
        false,
        'component.shr.repository',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

INSERT INTO testcase (
   	    id,
    	name,
    	description,
    	state,
    	rank,
    	is_manual,
    	is_required,
    	bean_name,
    	specification_id,
    	created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        'testcase.shr.shrf.8.1',
        'Verify If Patient Data Is In FHIR format',
        'Testcase to verify patient data format for specification SHRF8 of the shr repository',
        'testcase.status.active',
        9,
        false,
        true,
        'SHRF8TestCase1',
        'specification.shr.shrf.8',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

