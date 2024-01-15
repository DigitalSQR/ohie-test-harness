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
        'component.fr.repository',
        'FR Repository',
        'FR Repository Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-facility-registry-fr)',
        'component.status.active',
        2,
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
        'specification.fr.frwf.1',
        'FRWF-1',
        'FRWF-1 Specification of the FR Repository Component (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/query-health-worker-and-or-facility-records-workflow)',
        'specification.status.active',
        1,
        false,
        'component.fr.repository',
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
        'testcase.fr.frwf.1.1',
        'Verify Create Organization and Location',
        'Testcase to verify create Organization and Location for specification FRWF1 of the fr repository',
        'testcase.status.active',
        1,
        false,
        true,
        'FRWF1TestCase1',
        'specification.fr.frwf.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );
