
INSERT INTO gender (gender)
values
    ('male'),
    ('female')
ON conflict(gender)
    DO NOTHING;
COMMIT;