INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.62030.R.a.001',
    'attribute-check',
    'AX_Strukturlinie3D',
    '{
        "conditions": { "type": "tag_between", "tag_key": "art", "from_value": "1210", "to_value": "1250" },
        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BoeschungKliff" }
            }
        }
    }',
    'Strukturlinie 3D hat keine Relation zu Böschung, Kliff.')
ON CONFLICT (id) DO NOTHING;
