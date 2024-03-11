--create HMIS manual testcases audit.
--
--@author dhruv
--@since 2023-09-13

INSERT INTO specification_aud (id,
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
                           revtype)
VALUES ('specification.hmis.hmisf.2',
        'HMISF-2',
        'HMISF-2 Specification of the Health Management Information System (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-management-information-system-hmis)',
        'specification.status.active',
        3,
        true,
        false,
        'component.health.management.information.system',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.hmis.hmisf.3',
        'HMISF-3',
        'HMISF-3 Specification of the Health Management Information System (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-management-information-system-hmis)',
        'specification.status.active',
        4,
        true,
        false,
        'component.health.management.information.system',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.hmis.hmisf.4',
        'HMISF-4',
        'HMISF-4 Specification of the Health Management Information System (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-management-information-system-hmis)',
        'specification.status.active',
        5,
        true,
        false,
        'component.health.management.information.system',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.hmis.hmisf.5',
        'HMISF-5',
        'HMISF-5 Specification of the Health Management Information System (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-management-information-system-hmis)',
        'specification.status.active',
        6,
        true,
        false,
        'component.health.management.information.system',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.hmis.hmisf.6',
        'HMISF-6',
        'HMISF-6 Specification of the Health Management Information System (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-management-information-system-hmis)',
        'specification.status.active',
        7,
        true,
        false,
        'component.health.management.information.system',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.hmis.hmisf.7',
        'HMISF-7',
        'HMISF-7 Specification of the Health Management Information System (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-health-management-information-system-hmis)',
        'specification.status.active',
        8,
        true,
        false,
        'component.health.management.information.system',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0);

INSERT INTO testcase_aud (id,
                      name,
                      description,
                      state,
                      rank,
                      is_manual,
                      bean_name,
                      question_type,
                      specification_id,
                      created_by,
                      updated_by,
                      created_at,
                      updated_at,
                      version,
                      rev,
                      revtype)
VALUES ('testcase.hmis.hmisf.2.1',
        'Can you see an interface that allows you to add patient information in the HMIS?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.1',
        'In this interface that allows data entry, can you see proper validation messages in case of incorrect data entry? For instance: if there is a field that takes phone number and you provide less than expected number of digites then does the system guide you and indicate that the phone number is invalid?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.2',
        'In this interface, are there tooltips to guide you. Tooltips can look like an info icon placed near a question and when you hover your cursor over that info icon, it should show you more information about that question.',
        'Instructions to be added',
        'testcase.status.active',
        2,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.3',
        'In this interface, is there conditional logic to show only relevant questions based on conditions? For instance, If a patient''s age is less than 18 then any question for a patient with age more than 18 should not be asked and should conditionally hide.',
        'Instructions to be added',
        'testcase.status.active',
        3,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.4.1',
        'In HMIS, is there any interface in the system that allows you to import data from other systems (e.g., ADX, FHIR)?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.4',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.5.1',
        'Does the system give correct details about hospitals and clinics, showing where they are on a map and telling about their administrative distribution like national or state level?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.5',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.6.1',
        'Is there any user interfaces in the system that allows you to share health facility information with other systems, such as a facility registry?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.6',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.1',
        'Is there an interface in the system that allows you to analyze the data. For instance, can the system automatically create an annual healthcare report by combining monthly data from different regions?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.7',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.2',
        'Is there an interface in the system that allows you to query the data across different dimensions. For instance, can you easily query health data in the system based on factors like location, time, or other dimensions?',
        'Instructions to be added',
        'testcase.status.active',
        2,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.7',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.3',
        'Is there an interface in the system that allows you to visualize the data by providing customizable charts and graphs. For instance, can you customize the graphs or charts in the system by adjusting filter parameters such as to view data for a particular country?',
        'Instructions to be added',
        'testcase.status.active',
        3,
        true,
        null,
        'SINGLE_SELECT',
        'specification.hmis.hmisf.7',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0);

INSERT INTO testcase_option_aud (id,
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
                             revtype)
VALUES ('testcase.hmis.hmisf.2.1.option.1',
        'Yes, the HMIS has an interface for data entry.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.2.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.2.1.option.2',
        'No, the HMIS lacks a dedicated interface for data entry.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.2.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.1.option.1',
        'Yes, the system shows proper validation message if data entry is incorrect',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.3.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.1.option.2',
        'No, the system doesn''t show proper validation message if data entry is incorrect',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.3.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.2.option.1',
        'Yes, the system has tooltips.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.3.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.2.option.2',
        'No, the system lacks tooltips.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.3.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.3.option.1',
        'Yes, the HMIS uses conditional logic.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.3.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.3.3.option.2',
        'No, the HMIS doesn''t utilize conditional logic.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.3.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.4.1.option.1',
        'Yes, there is an interface to import data from other systems',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.4.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.4.1.option.2',
        'No, there is no interface to import data from other systems',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.4.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.5.1.option.1',
        'Yes, the system provides correct details, shows locations on a map, and explains administrative distribution.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.5.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.5.1.option.2',
        'No, the system doesn''t provide correct details, doesn''t show locations on a map, and doesn''t explain administrative distribution.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.5.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.6.1.option.1',
        'Yes, the HMIS provides interfaces for sharing health facility information with other systems',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.6.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.6.1.option.2',
        'No, the HMIS lacks the capability to provide interfaces for sharing health facility information with other systems',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.6.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.1.option.1',
        'Yes, the system allows to analyze the data',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.7.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.1.option.2',
        'No, the system doesn''t allow to analyze the data',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.7.1',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.2.option.1',
        'Yes, the system has an interface for querying health data.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.7.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.2.option.2',
        'No, the system lacks an interface for querying health data.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.7.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.3.option.1',
        'Yes, the system allows users to generate customized visualizations',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.hmis.hmisf.7.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.hmis.hmisf.7.3.option.2',
        'No, the system does not provide the option to customize graphs or charts based on filter parameters.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.hmis.hmisf.7.3',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0,
        1,
        0);