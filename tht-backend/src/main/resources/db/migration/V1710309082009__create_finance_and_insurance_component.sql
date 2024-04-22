--create Finance and insurance component.
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
        'component.finance.and.insurance.service',
        'Finance and Insurance Service (FIS)',
        'Finance and Insurance Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-finance-and-insurance-service)',
        'component.status.inactive',
        3,
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0
    );
