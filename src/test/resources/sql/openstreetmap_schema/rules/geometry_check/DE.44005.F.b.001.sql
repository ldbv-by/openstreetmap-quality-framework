INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44005.F.b.001',
    'geometry-check',
    'AX_Hafenbecken',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "aggregator": "union",
            "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Hafen" } }
        }
    }',
    'Ein Objekt ''AX_Hafenbecken'' wird immer von ''AX_Hafen'' überlagert.')
ON CONFLICT (id) DO NOTHING;