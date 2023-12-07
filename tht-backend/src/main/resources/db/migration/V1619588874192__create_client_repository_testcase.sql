--create CR testcases.
--
--@author dhruv
--@since 2023-09-13

DELETE FROM testcase;
DELETE FROM specification;
DELETE FROM component;

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
        'component.client.repository',
        'Client Repository',
        'Client Repository Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/client-registry)',
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
        'specification.cr.crwf.1',
        'CRWF-1',
        'CRWF-1 Specification of the Client Repository Component (https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1)',
        'specification.status.active',
        1,
        false,
        'component.client.repository',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ) , (
        'specification.cr.crwf.2',
        'CRWF-2',
        'CRWF-2 Specification of the Client Repository Component (https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/update-patient-demographic-record-workflow)',
        'specification.status.active',
        2,
        false,
        'component.client.repository',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ), (
       'specification.cr.crwf.3',
       'CRWF-3',
       'CRWF-3 Specification of the Client Repository Component (https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/query-patient-demographic-records-by-identifier-workflow)',
       'specification.status.active',
       3,
       false,
       'component.client.repository',
       'ivasiwala@argusoft.com',
       'ivasiwala@argusoft.com',
       Now(),
       Now(),
       0
   ), (
       'specification.cr.crwf.4',
       'CRWF-4',
       'CRWF-4 Specification of the Client Repository Component (https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow)',
       'specification.status.active',
       4,
       false,
       'component.client.repository',
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
        'testcase.cr.crwf.1.1',
        'Verify Create Patient',
        'Testcase to verify create patient for specification CRWF1 of the client repository',
        'testcase.status.active',
        1,
        false,
        true,
        'CRWF1TestCase1',
        'specification.cr.crwf.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ) , (
        'testcase.cr.crwf.1.2',
        'Verify Avoid Duplicate Patient Creation',
        'Testcase to verify create duplication patient is getting avoided for specification CRWF1 of the client repository',
        'testcase.status.active',
        2,
        false,
        true,
        'CRWF1TestCase2',
        'specification.cr.crwf.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ) , (
         'testcase.cr.crwf.2.1',
         'Verify Update Patient',
         'Testcase Verify Update Patient for Specification CRWF2 of the Client Repository',
         'testcase.status.active',
         1,
         false,
         true,
         'CRWF2TestCase1',
         'specification.cr.crwf.2',
         'ivasiwala@argusoft.com',
         'ivasiwala@argusoft.com',
         Now(),
         Now(),
         0
    ) , (
         'testcase.cr.crwf.3.1',
         'Verify Fetch Patient By Identifier',
         'Testcase Fetch Patient By Identifier for Specification CRWF3 of the Client Repository',
         'testcase.status.active',
         1,
         false,
         true,
         'CRWF3TestCase1',
         'specification.cr.crwf.3',
         'ivasiwala@argusoft.com',
         'ivasiwala@argusoft.com',
         Now(),
         Now(),
         0
    ) , (
         'testcase.cr.crwf.4.1',
         'Verify Fetch Patient By Demographic And/Or Identifier',
         'Testcase Verify Fetch Patient By Demographic And/Or Identifier for Specification CRWF4 of the Client Repository',
         'testcase.status.active',
         1,
         false,
         true,
         'CRWF4TestCase1',
         'specification.cr.crwf.4',
         'ivasiwala@argusoft.com',
         'ivasiwala@argusoft.com',
         Now(),
         Now(),
         0
    );