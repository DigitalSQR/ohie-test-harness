--Add Entry for the tht oauth client
--
--@author dhruv
--@since 2023-09-13

INSERT INTO
    oauth_client_details(
        client_id,
        resource_ids,
        client_secret,
        scope,
        authorized_grant_types,
        web_server_redirect_uri,
        authorities,
        access_token_validity,
        refresh_token_validity,
        additional_information,
        autoapprove
    )
VALUES
    (
        'tht',
        'resource_id',
        '$2a$10$rulzZ1jIpBZIzwCuWpS1.uD1MKlZuZDT02bqvwQeb3E4YhErUCAji',
        'write',
        'password,authorization_code,refresh_token,implicit',
        null,
        null,
        60,
        300,
        null,
        true
    );