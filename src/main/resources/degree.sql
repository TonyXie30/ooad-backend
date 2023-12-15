
INSERT INTO degree (degree)
values
    ('postgraduate'),
    ('doctorate')
ON conflict(degree)
    DO NOTHING;
COMMIT;