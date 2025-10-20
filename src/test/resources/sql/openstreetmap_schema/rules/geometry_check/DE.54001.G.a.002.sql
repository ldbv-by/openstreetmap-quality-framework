INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.G.a.002',
    'geometry-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "type": "tag_in", "tag_key": "bewuchs", "values": ["1021", "1022", "1023", "1250", "1260", "1500", "1900"] },
        "checks": { "type": "geom_type", "value": "Polygon" }
    }',
    'Ein ''AX_Vegetationsmerkmal'' mit ''bewuchs'' 1021, 1022, 1023, 1250, 1260, 1500 oder 1900 darf nur flächenförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;