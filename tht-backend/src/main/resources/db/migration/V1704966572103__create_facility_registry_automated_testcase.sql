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
                'specification.fr.frwf.1',
                'FRWF-1',
                'FRWF-1 Specification of the Facility Registry (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/query-health-worker-and-or-facility-records-workflow)',
                'specification.status.active',
                1,
                false,
                true,
                'component.facility.registry',
                'ivasiwala@argusoft.com',
                'ivasiwala@argusoft.com',
                Now(),
                Now(),
                0
        ),
        (
                'specification.fr.frf.9',
                'FRF-9',
                'FRF-9 Specification of the Facility Registry (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-facility-registry-fr)',
                'specification.status.active',
                10,
                false,
                false,
                'component.facility.registry',
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
                'testcase.fr.frwf.1.1',
                'Verify Create Organization and Location',
                'Testcase to verify create Organization and Location for specification FRWF1 of the fr repository',
                'testcase.status.active',
                1,
                false,
                'FRWF1TestCase1',
                'specification.fr.frwf.1',
                'ivasiwala@argusoft.com',
                'ivasiwala@argusoft.com',
                Now(),
                Now(),
                0
        ),
        (
                'testcase.fr.frf.9.1',
                'Search facilities by attribute',
                'Test case to verify the ability to  search for facilities by attribute.',
                'testcase.status.active',
                1,
                false,
                'FRF9TestCase1',
                'specification.fr.frf.9',
                'ivasiwala@argusoft.com',
                'ivasiwala@argusoft.com',
                Now(),
                Now(),
                0
        );


