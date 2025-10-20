INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.G.a.001',
    'geometry-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": { "type": "tag_in", "tag_key": "art", "values": ["1630", "1640", "1650", "1660", "1700"] },
        "checks": { "type": "geom_type", "value": "Polygon" }
    }',
    'Ein ''AX_Gewaessermerkmal'' mit ''art'' 1630, 1640, 1650, 1660 oder 1700 darf nur flächenförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
