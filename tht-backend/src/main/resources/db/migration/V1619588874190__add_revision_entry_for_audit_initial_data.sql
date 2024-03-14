--Added entry for the revision table to initiate audit table data.
--
--@author dhruv
--@since 2023-09-13


INSERT INTO revision(revision_number, revision_timestamp)
VALUES (1,
        EXTRACT(
                EPOCH
                FROM
                CURRENT_TIMESTAMP
        ) :: BIGINT * 1000);