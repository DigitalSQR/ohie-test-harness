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
    ) VALUES    (
                  'specification.cr.crf.3',
                  'CRF-3',
                  'CRF-3 Specification of the Client Repository Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/client-registry)',
                  'specification.status.active',
                  6,
                  true,
                  'component.client.repository',
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
    ) VALUES(
                               'testcase.cr.crf.3.1',
                               'Verify inbound/outbound transaction',
                                'Test case verify the ability to keep record of both message it receives and the messages it sends and can be reviewed/monitored',
                                'testcase.status.active',
                                 1,
                                 false,
                                 false,
                                 'CRF3TestCase1',
                                 'specification.cr.crf.3',
                                 'ivasiwala@argusoft.com',
                                 'ivasiwala@argusoft.com',
                                 Now(),
                                 Now(),
                                 0
    );

