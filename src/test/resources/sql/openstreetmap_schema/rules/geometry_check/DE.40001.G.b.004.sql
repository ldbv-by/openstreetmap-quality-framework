INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.G.b.004',
    'geometry-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": {
            "all": [
                {   "type": "tag_equals", "tag_key": "istWeitereNutzung", "value": "1000" },
                {   "type": "tag_equals", "tag_key": "funktion", "value": "1200" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "current:object_type" } }
        }
    }',
    'Das Objekt liegt nicht auf einer Fläche der gleichen Objektart.')
ON CONFLICT (id) DO NOTHING;
