INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.52001.F.b.001',
    'geometry-check',
    'AX_Ortslage',
    '{
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "overlaps",
                "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Ortslage" } } }
            }
        }
    }',
    '''AX_Ortslage'' darf sich nicht gegenseitig überlagern.')
ON CONFLICT (id) DO NOTHING;
