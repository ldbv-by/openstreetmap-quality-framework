INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.31002.G.c.001',
    'geometry-check',
    'AX_Bauteil',
    '{
        "conditions": { "not": { "type": "tag_equals", "tag_key": "lageZurErdoberflaeche", "value": "1200" } },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Gebaeude" } } }
        }
    }',
    'Ein Objekt das nicht ''lageZurErdoberflaeche'' 1200 hat, liegt immer auf einem Objekt ''AX_Gebaeude''.')
ON CONFLICT (id) DO NOTHING;
