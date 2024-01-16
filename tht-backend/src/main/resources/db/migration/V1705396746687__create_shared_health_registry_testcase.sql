--create SHR testcases.
--
--@author aastha
--@since 2024-01-16

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
        'component.shared.health.record.registry',
        'Shared Health Record registry',
        'Shared Health Record registry Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'component.status.active',
        1,
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
        'specification.shr.shrf.3',
        'SHRF-3',
        'SHRF-3 Specification of the Shared Health Record Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'specification.status.active',
        1,
        false,
        'component.shared.health.record.registry',
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
        'testcase.shr.shrf.3.1',
        'Verify creation and fetch facility using XML and JSON data',
        'Testcase to verify create and fetch resources for specification SHRF3 of the Shared Health Record registry',
        'testcase.status.active',
        1,
        false,
        true,
        'SHRF3TestCase1',
        'specification.shr.shrf.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

INSERT INTO testcase_option (
        id,
        name,
        description,
        state,
        rank,
        is_success,
        testcase_id,
        created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
                 'testcase.shr.shrf.3.1.option.1',
                 'NO',
                 'NO',
                 'testcase.option.status.active',
                 2,
                 false,
                 'testcase.shr.shrf.3.1',
                 'ivasiwala@argusoft.com',
                 'ivasiwala@argusoft.com',
                 Now(),
                 Now(),
                 0
             ),
             (
                 'testcase.shr.shrf.3.1.option.2',
                 'YES',
                 'YES',
                 'testcase.option.status.active',
                 1,
                 true,
                 'testcase.shr.shrf.3.1',
                 'ivasiwala@argusoft.com',
                 'ivasiwala@argusoft.com',
                 Now(),
                 Now(),
                 0
             );