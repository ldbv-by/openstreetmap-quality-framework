INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.G.a.004',
    'geometry-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "type": "tag_in", "tag_key": "zustand", "values": ["5000", "6100"] },
        "checks": { "type": "geom_type", "value": "Polygon" }
    }',
    'Ein ''AX_Vegetationsmerkmal'' mit ''zustand'' 5000 oder 6100 darf nur flächenförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
