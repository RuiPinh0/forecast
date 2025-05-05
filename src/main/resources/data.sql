INSERT INTO location (id, name, street, latitude, longitude, created_date, updated_date)
VALUES 
(1, 'Skoyen Park', '123 Skoyen Street', '59.9225', '10.6789', '2025-05-01T10:00:00Z', '2025-05-01T10:00:00Z'),
(2, 'Oslo Arena', '456 Oslo Street', '59.9139', '10.7522', '2025-05-02T10:00:00Z', '2025-05-02T10:00:00Z'),
(3, 'Handball Court', '789 Handball Street', '59.9100', '10.7500', '2025-05-03T10:00:00Z', '2025-05-03T10:00:00Z');

INSERT INTO event (id, name, created_date, event_start_date, event_end_date, location_id, updated_by, updated_date, status)
VALUES 
(1, 'football in skoyen', '2025-05-05T00:00:00Z', '2025-05-06T10:00:00Z', '2025-05-06T12:00:00Z', 1, 'Rui', '2025-05-01T10:00:00Z', 'scheduled'),
(2, 'football in skoyen', '2025-05-05T00:00:00Z', '2025-05-15T10:00:00Z', '2025-05-15T12:00:00Z', 2, 'Rui', '2025-05-02T10:00:00Z', 'scheduled'),
(3, 'handball', '2025-05-05T00:00:00Z', '2025-05-08T10:00:00Z', '2025-05-08T12:00:00Z', 3, 'Rui', '2025-05-03T10:00:00Z', 'scheduled');