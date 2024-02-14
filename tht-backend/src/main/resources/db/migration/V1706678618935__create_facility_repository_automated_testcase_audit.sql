INSERT INTO
        component_aud (
                id,
                rev,
                revtype,
                created_at,
                created_by,
                updated_at,
                updated_by,
                description,
                name,
                state,
                rank,
                version
        )
VALUES
        (
                'component.facility.registry',
                1,
                0,
                Now(),
                'ivasiwala@argusoft.com',
                Now(),
                'ivasiwala@argusoft.com',
                'Facility Registry Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-facility-registry-fr)',
                'Facility Registry',
                'component.status.active',
                2,
                0
        );

INSERT INTO
        specification_aud (
                id,
                rev,
                revtype,
                created_at,
                created_by,
                updated_at,
                updated_by,
                description,
                name,
                state,
                is_functional,
                is_required,
                rank,
                version
        )
VALUES
        (
                'specification.fr.frwf.1',
                1,
                0,
                Now(),
                'ivasiwala@argusoft.com',
                Now(),
                'ivasiwala@argusoft.com',
                'FRWF-1 Specification of the Facility Registry (https://guides.ohie.org/arch-spec/introduction/care-services-discovery/query-health-worker-and-or-facility-records-workflow)',
                'FRWF-1',
                'specification.status.active',
                false,
                true,
                1,
                0
        ),
        (
                'specification.fr.frf.9',
                1,
                0,
                Now(),
                'ivasiwala@argusoft.com',
                Now(),
                'ivasiwala@argusoft.com',
                'FRF-9 Specification of the Facility Registry (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-facility-registry-fr)',
                'FRF-9',
                'specification.status.active',
                false,
                false,
                10,
                0
        );

INSERT INTO
        testcase_aud (
                id,
                rev,
                revtype,
                bean_name,
                is_manual,
                rank,
                specification_id,
                created_at,
                created_by,
                updated_at,
                updated_by,
                description,
                name,
                state,
                version
        )
VALUES
        (
                'testcase.fr.frwf.1.1',
                1,
                0,
                'FRWF1TestCase1',
                false,
                1,
                'specification.fr.frwf.1',
                NOW(),
                'ivasiwala@argusoft.com',
                NOW(),
                'ivasiwala@argusoft.com',
                'Testcase to verify create Organization and Location for specification FRWF1 of the fr repository',
                'Verify Create Organization and Location',
                'testcase.status.active',
                0
        ),
        (
                'testcase.fr.frf.9.1',
                1,
                0,
                'FRF9TestCase1',
                false,
                1,
                'specification.fr.frf.9',
                NOW(),
                'ivasiwala@argusoft.com',
                NOW(),
                'ivasiwala@argusoft.com',
                'Test case to verify the ability to search for facilities by attribute.',
                'Search facilities by attribute',
                'testcase.status.active',
                0
        );

