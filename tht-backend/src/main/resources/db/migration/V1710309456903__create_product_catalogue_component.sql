--create Product catalogue component.
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
        'component.product.catalogue',
        'Product Catalogue',
        'Product Catalogue Component (https://guides.ohie.org/arch-spec/openhie-component-specifications-1/openhie-product-catalogue-pc)',
        'component.status.inactive',
        8,
        'ivasiwala@argusoft.com',
        'ivasiwala@argusoft.com',
        Now(),
        Now(),
        0
    );
