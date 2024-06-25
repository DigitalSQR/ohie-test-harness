

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
VALUES ('specification.ts.tsf.1',
        'TSF-1',
        'TSF-1 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        9,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.3',
        'TSF-3',
        'TSF-3 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        11,
        true,
        false,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.4',
        'TSF-4',
        'TSF-4 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        12,
        true,
        false,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.5',
        'TSF-5',
        'TSF-5 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        13,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.6',
        'TSF-6',
        'TSF-6 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        14,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.7',
        'TSF-7',
        'TSF-7 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        15,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.8',
        'TSF-8',
        'TSF-8 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        16,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.9',
        'TSF-9',
        'TSF-9 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        17,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.10',
        'TSF-10',
        'TSF-10 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        18,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.11',
        'TSF-11',
        'TSF-11 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        19,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.12',
        'TSF-12',
        'TSF-12 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        20,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('specification.ts.tsf.13',
        'TSF-13',
        'TSF-13 Specification of the Terminology Service7 Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
        'specification.status.active',
        21,
        true,
        true,
        'component.terminology.service',
        'SYSTEM_USER',
        'SYSTEM_USER',
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
                      failure_message,
                      specification_id,
                      created_by,
                      updated_by,
                      created_at,
                      updated_at,
                      version,
                      rev,
                      revtype)
VALUES ('testcase.ts.tsf.1.1',
        'Does the system allow to import and manage local codes, ensuring compatibility with the local healthcare practices? An example can be, Blood Glucose Test with Local Code: XYZ-1234. This is a local code specific to a particular healthcare institution or system. It could be a code assigned internally for a blood glucose test conducted within that specific facility.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should support import of local (e.g., local lab codes)  and standard code systems (e.g., LOINC) to meet this specification.',
        'specification.ts.tsf.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.1.2',
        'Does the system support importing of standardized codes such as LOINC codes used for identifying laboratory tests? An example can be, LOINC Code: 2345-7 (Glucose [Mass/volume] in Blood). This is a LOINC code that represents a standardized way of identifying the same or similar concept, in this case, glucose concentration in blood.',
        'Instructions to be added',
        'testcase.status.active',
        2,
        true,
        null,
        'SINGLE_SELECT',
        'The system should support import of local (e.g., local lab codes)  and standard code systems (e.g., LOINC) to meet this specification.',
        'specification.ts.tsf.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.3.1',
        'When testing the system, can it keep track of different versions of code systems?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should support versioning of code systems - storing and making multiple versions of a code system to meet this specification.',
        'specification.ts.tsf.3',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.4.1',
        'Does the system support versioning of value sets (maybe because of updated medical standards or new terminology) - storing and making multiple versions of a value set available via terminology services?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should support versioning of value sets - storing and making multiple versions of a value set to meet this specification.',
        'specification.ts.tsf.4',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.5.1',
        'Can the system allow import of value set definitions? For example, these sets might come in a simple text file with codes written in it, or they could be in a more structured format like XML or JSON, which are common ways to organize data.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow import of value set definitions to meet this specification. The import format may vary from a text file to FHIR Value Set resource in XML or JSON format.',
        'specification.ts.tsf.5',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.6.1',
        'Can the system allow export of value set definitions? This exporting ability ensures that the lists of medical terms or codes can be shared or used in different systems when needed.',
        'This interface should suggest potential matches, ask your opinion to merge records that matches and can look something like this:<br/> Review potential matches:<br/>Operation 1: John Doe (January 15, 1980):<br/>Record 1:  Address: 123 Main Street, (555) 123-4567<br/>Record 2:  Address: 456 Elm Street, (555) 987-6543',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow for export of value sets definitions to meet this specification. The export format may vary from a text file to FHIR Value Set resource in XML or JSON format to meet this specification.',
        'specification.ts.tsf.6',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.7.1',
        'Can the system allow import of value set expansions? For example, let these list be catalogs of specific medical terms used by doctors and healthcare systems. Sometimes, these lists need to be expanded or updated.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow for the import of value set expansions to meet this specification. The import format may vary from a text file to FHIR Value Set resource in XML or JSON format.',
        'specification.ts.tsf.7',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.8.1',
        'Can the system allow export of value set expansions? This flexibility allows to store these lists in formats that are easy to use and share, whether it''s a basic text file or a structured digital file such as XML or JSON format.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow for export of value sets expansions to meet this specification. The export format may vary from a text file to FHIR Value Set resource in XML or JSON format.',
        'specification.ts.tsf.8',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.9.1',
        'Can the system allow for the import of relationships between codes? These connections are represented as "concept maps," which show how one medical code (the source) relates to another code. The system can understand and incorporate these relationships, whether they are presented in a simple text file or a structured digital file like json or xml.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow for the import of relationships between codes (i.e., concept maps) to meet this specification. The import format may vary from a text file to FHIR Concept Map resource in XML or JSON format.',
        'specification.ts.tsf.9',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.10.1',
        'Can the system allow for the export of relationships between codes? These connections show how one code relates to another in the medical field. The copies can be made in different ways: it could be a text file listing the connections between codes, or digital format like XML or JSON.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should allow for the export of relationships between codes (i.e., concept maps) to meet this specification. The export format may vary from a text file to FHIR Concept Map resource in XML or JSON format.',
        'specification.ts.tsf.10',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.11.1',
        'Can the system let you search for a code and give additional information? When you search for this code, the service should not only give you the code itself but also provide extra details about it. These details might include what the code means (its definition) and whether it''s currently active or not (its status).',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should expose services that allow for the retrieval of a code, and additional information about the code such as definition and status to meet this specification.',
        'specification.ts.tsf.11',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.12.1',
        'Can the system allow you to validate a code (i.e., does the code exist) against a particular code system? For example: If a specific code, like a medical term or a number used in healthcare, actually exists in a particular system.',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should expose services that allow for the validation of a code against a particular code system to meet this specification.',
        'specification.ts.tsf.12',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.13.1',
        'Can the system use concept maps to find a corresponding code when given a specific code?',
        'Instructions to be added',
        'testcase.status.active',
        1,
        true,
        null,
        'SINGLE_SELECT',
        'The system should expose services that utilize concept maps to retrieve a target code given a source code to meet this specification.',
        'specification.ts.tsf.13',
        'SYSTEM_USER',
        'SYSTEM_USER',
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
VALUES ('testcase.ts.tsf.1.1.option.1',
        'Yes, it imports and validates local codes correctly.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.1.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.1.1.option.2',
        'No, it doesn''t allow importing of local codes properly.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.1.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.1.2.option.1',
        'Yes, it allows import of standardized codes such as LOINC codes.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.1.2',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.1.2.option.2',
        'No, it doesn''t allow import of any standardized codes.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.1.2',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.3.1.option.1',
        'Yes, it can track different versions of code systems.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.3.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.3.1.option.2',
        'No, it can''t track different versions of code systems.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.3.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.4.1.option.1',
        'Yes, it supports versioning of value sets.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.4.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.4.1.option.2',
        'No, it doesn''t support versioning of value sets.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.4.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.5.1.option.1',
        'Yes, it allows import of value set definitions.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.5.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.5.1.option.2',
        'No, it doesn''t allow import of value set definitions.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.5.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.6.1.option.1',
        'Yes, system allows to export definitions in different formats like text, XML, or JSON.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.6.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.6.1.option.2',
        'No, system doesn''t allow to export value set definitions.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.6.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.7.1.option.1',
        'Yes, the system allow import of value set expansions in different formats like text, XML, or JSON.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.7.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.7.1.option.2',
        'No, the system doesn''t allow import of value set expansions.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.7.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.8.1.option.1',
        'Yes, the system allow export of value set expansions.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.8.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.8.1.option.2',
        'No, the system doesn''t allow export of value set expansions.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.8.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.9.1.option.1',
        'Yes, it can import of relationships between codes from text files, XML, or JSON.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.9.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.9.1.option.2',
        'No, it doesn''t allow import of relationships between codes.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.9.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.10.1.option.1',
        'Yes, it can export relationships between codes in text files, XML, or JSON.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.10.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.10.1.option.2',
        'No, it doesn''t allow export of relationships between codes.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.10.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.11.1.option.1',
        'Yes, it provides extra details when you look up a code.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.11.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.11.1.option.2',
        'No, it doesn''t give extra details when you look up a code.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.11.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.12.1.option.1',
        'Yes, the system allows validation of codes.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.12.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.12.1.option.2',
        'No, the system doesn''t allow validation of codes',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.12.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.13.1.option.1',
        'Yes, it can find a target code using concept maps.',
        'Criteria to be added',
        'testcase.option.status.active',
        1,
        true,
        'testcase.ts.tsf.13.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0),
       ('testcase.ts.tsf.13.1.option.2',
        'No, it can''t find a target code using concept maps.',
        'Criteria to be added',
        'testcase.option.status.active',
        2,
        false,
        'testcase.ts.tsf.13.1',
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0,
        1,
        0);