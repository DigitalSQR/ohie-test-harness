INSERT INTO
    component (
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
    )
VALUES
    (
        'component.shared.health.record',
        'Shared Health Record',
        'Shared Health Record Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'component.status.active',
        9,
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );

INSERT INTO
    specification (
        id,
        name,
        description,
        state,
        rank,
        is_functional,
        is_required,
        component_id,
        created_by,
        updated_by,
        created_at,
        updated_at,
        version
    )
VALUES
    (
        'specification.shr.shrwf.1',
        'SHRWF-1',
        'SHRWF-1 Specification of the Shared Health Record Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'specification.status.active',
        1,
        false,
        true,
        'component.shared.health.record',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ),
    (
        'specification.shr.shrwf.2',
        'SHRWF-2',
        'SHRWF-2 Specification of the Shared health record Registry Component (https://guides.ohie.org/arch-spec/introduction/shared-health-record/query-patient-level-clinical-data-workflow)',
        'specification.status.active',
        2,
        false,
        true,
        'component.shared.health.record',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ),
--    (
--        'specification.shr.shrf.3',
--        'SHRF-3',
--        'SHRF-3 Specification of the Shared Health Record Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
--        'specification.status.active',
--        1,
--        true,
--        false,
--        'component.shared.health.record',
--        'ivasiwala@argusoft.com',
--        'ivasiwala@argusoft.com',
--        Now(),
--        Now(),
--        0
--    ),
(
    'specification.shr.shrf.5',
    'SHRF-5',
    'SHRF-5 Specification of the Shared health record Registry Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
    'specification.status.active',
    4,
    true,
    true,
    'component.shared.health.record',
    'ivasiwala@argusoft.com',
    'ivasiwala@argusoft.com',
    Now(),
    Now(),
    0
);
--    ,(
--        'specification.shr.shrf.8',
--        'SHRF-8',
--        'SHRF-8 Specification of the SHR Repository Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
--        'specification.status.active',
--        1,
--        true,
--        false,
--        'component.shared.health.record',
--        'ivasiwala@argusoft.com',
--        'ivasiwala@argusoft.com',
--        Now(),
--        Now(),
--        0
--    );



INSERT INTO
    testcase (
        id,
        name,
        description,
        state,
        rank,
        is_manual,
        bean_name,
        specification_id,
        created_by,
        updated_by,
        created_at,
        updated_at,
        version
    )
VALUES
    (
        'testcase.shr.shrwf.1.1',
        'Verify Encounter has correct practitioner and patient IDs',
        'Testcase to verify practitioner and patient IDs in encounter for specification SHRWF1 of the shared health record',
        'testcase.status.active',
        1,
        false,
        'SHRWF1TestCase1',
        'specification.shr.shrwf.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ),
    (
        'testcase.shr.shrwf.2.1',
        'Verify fetch patient level clinical data from Shared Health Record',
        'Testcase to verify fetch patient level clinical data from Shared Health Record for specification SHRWF1 of the shared health record registry',
        'testcase.status.active',
        1,
        false,
        'SHRWF2TestCase1',
        'specification.shr.shrwf.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    ),
--    (
--        'testcase.shr.shrf.3.1',
--        'Verify creation and fetch facility using XML and JSON data',
--        'Testcase to verify create and fetch resources for specification SHRF3 of the Shared Health Record registry',
--        'testcase.status.inactive',
--        1,
--        false,
--        'SHRF3TestCase1',
--        'specification.shr.shrf.3',
--        'ivasiwala@argusoft.com',
--        'ivasiwala@argusoft.com',
--        Now(),
--        Now(),
--        0
--    ),
        (
            'testcase.shr.shrf.5.1',
            'Verify that it can return a specific known document from Shared Health Record',
            'Testcase to verify that it can return a specific known document',
            'testcase.status.active',
            1,
            false,
            'SHRF5TestCase1',
            'specification.shr.shrf.5',
            'ivasiwala@argusoft.com',
            'ivasiwala@argusoft.com',
            Now(),
            Now(),
            0
        ),
        (
            'testcase.shr.shrf.5.2',
            'Verify that the system should also be able to offer a list of documents related to a patient from Shared Health Record',
            'Testcase to verify that the system should also be able to offer a list of documents related to a patient.',
            'testcase.status.active',
            2,
            false,
            'SHRF5TestCase2',
            'specification.shr.shrf.5',
            'ivasiwala@argusoft.com',
            'ivasiwala@argusoft.com',
            Now(),
            Now(),
            0
        ),
        (
            'testcase.shr.shrf.5.3',
            'Verify that ff someone asks for specific information like test results, the system can filter out and provide only the relevant data from Shared Health Record',
            'Testcase to verify that If someone asks for specific information like test results, the system can filter out and provide only the relevant data.',
            'testcase.status.active',
            3,
            false,
            'SHRF5TestCase3',
            'specification.shr.shrf.5',
            'ivasiwala@argusoft.com',
            'ivasiwala@argusoft.com',
            Now(),
            Now(),
            0
        ),
        (
            'testcase.shr.shrf.5.4',
            'Verify that system should give a summary of everything it knows about a patient from Shared Health Record',
            'Testcase to verify that system should give a summary of everything it knows about a patient.',
            'testcase.status.active',
            4,
            false,
            'SHRF5TestCase4',
            'specification.shr.shrf.5',
            'ivasiwala@argusoft.com',
            'ivasiwala@argusoft.com',
            Now(),
            Now(),
            0
        );
--    ,(
--        'testcase.shr.shrf.8.1',
--        'Verify If Patient Data Is In FHIR format',
--        'Testcase to verify patient data format for specification SHRF8 of the shr repository',
--        'testcase.status.inactive',
--        9,
--        false,
--        'SHRF8TestCase1',
--        'specification.shr.shrf.8',
--        'ivasiwala@argusoft.com',
--        'ivasiwala@argusoft.com',
--        Now(),
--        Now(),
--        0
--    );