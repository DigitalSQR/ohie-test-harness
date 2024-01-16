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
             'component.facility.registry',
             'Facility Registry',
             'Facility Registry Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-facility-registry-fr)',
             'component.status.active',
             2,
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
    component_id,
    created_by,
    updated_by,
    created_at,
    updated_at,
    version
) VALUES (
             'specification.fr.frf.9',
             'FRF-9',
             'FRF-9 Specification of the Facility Registry (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-facility-registry-fr)',
             'specification.status.active',
             1,
             false,
             'component.facility.registry',
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
    is_required,
    bean_name,
    specification_id,
    created_by,
    updated_by,
    created_at,
    updated_at,
    version
) VALUES (
             'testcase.fr.frf.9.1',
             'Search facilities by attribute',
             'Test case to verify the ability to  search for facilities by attribute.',
             'testcase.status.active',
             1,
             false,
             false,
             'FRF9TestCase1',
             'specification.fr.frf.9',
             'ivasiwala@argusoft.com',
             'ivasiwala@argusoft.com',
             Now(),
             Now(),
             0
         );
