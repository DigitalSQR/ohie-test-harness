INSERT INTO specification_aud (
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
    version,
    rev,
    revtype
)
VALUES (
    'specification.hwr.hwrf.3',
    'HWRF-3',
    'HWRF-3 Specification of the Health Worker Repository Component (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/query-health-worker-and-or-facility-records-workflow)',
    'specification.status.active',
    7,
    false,
    true,
    'component.health.worker.registry',
    'SYSTEM_USER',
    'SYSTEM_USER',
    Now(),
    Now(),
    0,
    1,
    0
);

INSERT INTO testcase_aud (
    id,
    name,
    description,
    state,
    rank,
    is_manual,
    bean_name,
    question_type,
    failure_message,
    specification_id,
    created_by,
    updated_by,
    created_at,
    updated_at,
    version,
    rev,
    revtype
)
VALUES (
    'testcase.hwr.hwrf.3.1',
    'As a user, does the system provide the capability to query the health worker data stored in its database? For instance, if you want to view information about healthcare worker''s qualifications or contact details, can the system provide you with the information?',
    'Instructions to be added',
    'testcase.status.active',
    1,
    true,
    null,
    'SINGLE_SELECT',
    'The system should support the ability to respond to queries on health worker data to meet this specification.',
    'specification.hwr.hwrf.3',
    'SYSTEM_USER',
    'SYSTEM_USER',
    Now(),
    Now(),
    0,
    1,
    0
);

INSERT INTO testcase_option_aud (
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
    version,
    rev,
    revtype
)
VALUES
    (
        'testcase.hwr.hwrf.3.1.option.1',
        'Yes, the system provides the ability to query the health worker data to get the required details.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hwr.hwrf.3.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0
    ),
    (
        'testcase.hwr.hwrf.3.1.option.2',
        'No, the system does not have the capability to query the health worker data.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hwr.hwrf.3.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0
    );
