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
        true,
        true,
        'component.shared.health.record',
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
            'specification.shr.shrf.5',
            'SHRF-5',
            'SHRF-5 Specification of the Shared health record Registry Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
            'specification.status.active',
            4,
            false,
            true,
            'component.shared.health.record.registry',
            'ivasiwala@argusoft.com',
            'ivasiwala@argusoft.com',
            Now(),
            Now(),
            0
        );

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
    );


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