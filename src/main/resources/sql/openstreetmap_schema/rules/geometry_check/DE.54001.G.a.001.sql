INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.G.a.001',
    'geometry-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "type": "tag_in", "tag_key": "bewuchs", "values": ["1210", "1220", "1230"] },
        "checks": { "type": "geom_type", "value": "LineString" }
    }',
    'Ein ''AX_Vegetationsmerkmal'' mit ''bewuchs'' 1210, 1220 oder 1230 darf nur linienförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
