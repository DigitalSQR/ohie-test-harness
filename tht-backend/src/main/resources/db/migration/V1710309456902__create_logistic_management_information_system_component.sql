--create Logistic management information system component.
--
--@author aastha
--@since 2024-03-13
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
        'component.logistic.management.information.system',
        'Logistic Management Information System',
        'Logistic management information system Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-logistics-management-information-system-lmis)',
        'component.status.inactive',
        7,
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0
    );
