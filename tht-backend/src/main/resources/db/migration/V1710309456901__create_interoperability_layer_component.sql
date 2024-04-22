--create Interoperability layer component.
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
        'component.interoperability.layer',
        'Interoperability layer (IOL)',
        'Interoperability layer Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-interoperability-layer-iol)',
        'component.status.inactive',
        6,
        'SYSTEM_USER',
        'SYSTEM_USER',
        Now(),
        Now(),
        0
    );
