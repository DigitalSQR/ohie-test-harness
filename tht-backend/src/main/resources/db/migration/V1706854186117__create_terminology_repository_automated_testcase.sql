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
            'component.terminology.service',
            'Terminology Service',
            'Terminology Service Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-terminology-service-ts)',
            'component.status.active',
            10,
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
        	is_required,
        	component_id,
        	created_by,
            updated_by,
            created_at,
            updated_at,
            version
        ) VALUES (
            'specification.ts.tswf.1',
            'TSWF-1',
            'TSWF-1 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/introduction/terminology-service-workflow/verify-code-existence)',
            'specification.status.active',
            1,
            false,
            true,
            'component.terminology.service',
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
        	bean_name,
        	specification_id,
        	created_by,
            updated_by,
            created_at,
            updated_at,
            version
        ) VALUES (
            'testcase.ts.tswf.1.1',
            'Verify Code Existence',
            'Testcase to Verify Code Existence for specification TSWF1 of the terminology service',
            'testcase.status.active',
            1,
            false,
            'TSWF1TestCase1',
            'specification.ts.tswf.1',
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
    	is_required,
    	component_id,
    	created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        'specification.ts.tswf.2',
        'TSWF-2',
        'TSWF-2 Specification of the Terminology Service Component (https://guides.ohie.org/arch-spec/introduction/terminology-service-workflow/verify-code-membership)',
        'specification.status.active',
        1,
        false,
        true,
        'component.terminology.service',
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
    	bean_name,
    	specification_id,
    	created_by,
        updated_by,
        created_at,
        updated_at,
        version
    ) VALUES (
        'testcase.ts.tswf.2.1',
        'Verify Code Membership',
        'Testcase to verify code membership for specification TSWF2 of the terminology repository',
        'testcase.status.active',
        1,
        false,
        'TSWF2TestCase1',
        'specification.ts.tswf.2',
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );
