INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.52001.F.b.001',
    'geometry-check',
    'AX_Ortslage',
    '{
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "overlaps",
                "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Ortslage" } }
            }
        }
    }',
    '''AX_Ortslage'' darf sich nicht gegenseitig überlagern.')
ON CONFLICT (id) DO NOTHING;
