INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51001.F.c.001',
    'geometry-check',
    'AX_Turm',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1004" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Flugverkehr" } } }
        }
    }',
    'Ein ''AX_Turm'' mit ''bauwerksfunktion'' 1004 muss innerhalb von ''AX_Flugverkehr'' liegen.')
ON CONFLICT (id) DO NOTHING;