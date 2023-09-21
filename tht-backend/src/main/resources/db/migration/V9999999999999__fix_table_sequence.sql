--fix table indexes for mockdata.
--
--@author dhruv
--@since 2023-09-13

--do
--$$
--declare
--   counter    integer := 0;
--   newval    integer := 0;
--begin
--    SELECT max(id) from {{table_name}} into counter;
--	SELECT setval('{{table_name}}_seq', counter + 1, false) into newval;
--end
--$$;

