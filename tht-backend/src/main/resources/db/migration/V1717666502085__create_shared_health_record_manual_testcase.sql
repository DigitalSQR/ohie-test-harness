

INSERT INTO specification (id,
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
                           version)
VALUES ('specification.shr.shrf.9',
        'SHRF-9',
        'SHRF-9 Specification of the Shared Health Record Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'specification.status.active',
        11,
        true,
        false,
        'component.shared.health.record',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('specification.shr.shrf.10',
        'SHRF-10',
        'SHRF-10 Specification of the Shared health record Registry Component (https://guides.ohie.org/arch-spec/introduction/shared-health-record/query-patient-level-clinical-data-workflow)',
        'specification.status.active',
        12,
        true,
        false,
        'component.shared.health.record',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('specification.shr.shrf.11',
        'SHRF-11',
        'SHRF-11 Specification of the Shared Health Record Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-shared-health-record-shr)',
        'specification.status.active',
        13,
        true,
        false,
        'component.shared.health.record',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);

INSERT INTO testcase (id,
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
                      version)
VALUES ('testcase.shr.shrf.9.1',
        'When using the healthcare system (SHR), does it check if the entered data is sensible?  For example if a person enters a very high temperature, the system might alert that it''s unusually high and ask you to double-check.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should provide interfaces at various stages of the data lifecycle to allow for semantic validation or simple decision support to meet this specification.',
        'specification.shr.shrf.9',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.9.2',
        'When using the healthcare system (SHR), does it offer guidance for decision making? If doctor is prescribing a medication, the system might suggest a lower dosage if it notices a potential interaction with other drugs the patient is taking. These helpful tips or alerts are like having simple decision support, where the system assists the user in making informed choices based on the data being entered.',
        'Instructions to be added',
        'testcase.status.active',
        2,
        true,
        null,
        'SINGLE_SELECT',
        'The system should provide interfaces at various stages of the data lifecycle to allow for semantic validation or simple decision support to meet this specification.',
        'specification.shr.shrf.9',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.10.1',
        'While using the healthcare system (SHR), can you confirm if the system allows you to define and retrieve basic privacy rules? For instance, setting rules to restrict certain users to access only specific parts of a patient''s information, or limiting access to the entire patient record?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow for storage and retrieval of basic privacy/policy constraints to meet this specification.',
        'specification.shr.shrf.10',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.11.1',
        'While using SHR, are you able to store observational data in standardized format for easy interpretation and retrieval?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should be able to store identified observational data in standardized format to meet this specification.',
        'specification.shr.shrf.11',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);


INSERT INTO testcase_option (id,
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
                             version)
VALUES ('testcase.shr.shrf.9.1.option.1',
        'Yes, it checks for sensible data during entry and processing.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.shr.shrf.9.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.9.1.option.2',
        'No, it doesn''t check for sensible data during entry and processing.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.shr.shrf.9.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.9.2.option.1',
        'Yes, it guides in decision making.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.shr.shrf.9.2',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.9.2.option.2',
        'No, it doesn''t guide in decision making.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.shr.shrf.9.2',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.10.1.option.1',
        'Yes, the system supports defining rules to restrict access, such as allowing only certain users to view specific parts of a patient''s information or restricting access to the entire patient record.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.shr.shrf.10.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.10.1.option.2',
        'No, the system does not provide the capability to define and retrieve basic privacy rules, limiting control over user access to patient information.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.shr.shrf.10.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.11.1.option.1',
        'Yes, it stores observational data in standardized format',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.shr.shrf.11.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0),
       ('testcase.shr.shrf.11.1.option.2',
        'No, it doesn''t observational store data in standardized format',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.shr.shrf.11.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0);