--create default grade data.
--
--@author dhruv
--@since 2023-09-13
INSERT INTO grade(id, created_at, created_by, updated_at, updated_by, version, grade, percentage)
VALUES
('1',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'A',90),
('2',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'B',80),
('3',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'C',70),
('4',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'D',0);

--audit data
INSERT INTO grade_aud(id, created_at, created_by, updated_at, updated_by, version, grade, percentage, rev, revtype)
VALUES
('1',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'A',90, 1, 0),
('2',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'B',80, 1, 0),
('3',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'C',70, 1, 0),
('4',Now(),'SYSTEM_USER',Now(),'SYSTEM_USER',0,'D',0, 1, 0);