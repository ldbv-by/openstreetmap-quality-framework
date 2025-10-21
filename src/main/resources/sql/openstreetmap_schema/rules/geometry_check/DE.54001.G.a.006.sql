INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.G.a.006',
    'geometry-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "type": "tag_in", "tag_key": "bewuchs", "values": ["1011", "1012"] },
        "checks": { "type": "geom_type", "value": "Point" }
    }',
    'Ein ''AX_Vegetationsmerkmal'' mit ''bewuchs'' 1011 oder 1012 darf nur punktförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
