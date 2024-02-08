
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
    'component.health.worker.registry',
    'Health Worker Registry',
    'Health Worker Registry Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-worker-registry-hwr)',
    'component.status.active',
    5,
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
        is_required,
        component_id,
        created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
    'specification.hwr.hwwf.1',
            'HWWF-1',
            'HWWF-1 Specification of the Health Worker Repository Component (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/query-health-worker-and-or-facility-records-workflow)',
            'specification.status.active',
            1,
            false,
            true,
            'component.health.worker.registry',
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
            'specification.hw.hwwf.3',
            'HWWF-3',
            'HWWF-3 Specification of the HealthWorker Registry Component (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/search-care-services-workflow)',
            'specification.status.active',
            3,
            false,
            true,
            'component.healthworker.registry',
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
        'specification.hw.hwwf.2',
        'HWWF-2',
        'HWWF-2 Specification of the HealthWorker Registry Component (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/query-care-services-records-workflow)',
        'specification.status.active',
        2,
        false,
        true,
        'component.healthworker.registry',
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
            bean_name,
            specification_id,
            created_by,
            updated_by,
            created_at,
            updated_at,
            version
        ) VALUES (
                'testcase.hwr.hwwf.1.1',
                'Verify Create Practitioner',
                'Testcase to verify create practitioner for specification HWWF1 of the healthworker repository',
                'testcase.status.active',
                1,
                false,
                'HWWF1TestCase1',
                'specification.hwr.hwwf.1',
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
    'testcase.hw.hwwf.3.1',
    'Search Care Services ',
    'Testcase to search care Service for specification HWWF3 of the HealthWorker Registry',
    'testcase.status.active',
    1,
    false,
    'HWWF3TestCase1',
    'specification.hw.hwwf.3',
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
              'testcase.hw.hwwf.2.1',
              'Query Care Services Records Workflow ',
              'Testcase to Query Care Services Records Workflow for specification HWWF2 of the HealthWorker Registry',
              'testcase.status.active',
              1,
              false,
              'HWWF2TestCase1',
              'specification.hw.hwwf.2',
              'ivasiwala@argusoft.com',
              'ivasiwala@argusoft.com',
              Now(),
              Now(),
              0
          );