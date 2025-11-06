INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42016.F.b.001',
    'geometry-check',
    'AX_Schiffsverkehr',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "aggregator": "union",
            "data_set_filter": {
                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Schiffsverkehr" }
            }
        }
    }',
    'Das Objekt ''AX_Schiffsverkehr'' mit ''istWeitereNutzung'' muss ein oder mehrere Objekte ''AX_Schiffsverkehr'' überlagern.')
ON CONFLICT (id) DO NOTHING;
