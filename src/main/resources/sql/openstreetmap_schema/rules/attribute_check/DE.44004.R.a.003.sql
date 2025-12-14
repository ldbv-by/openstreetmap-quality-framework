INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44004.R.a.003',
    'attribute-check',
    'AX_Gewaesserachse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "funktion", "value": "8300" },
        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Kanal" }
            }
        }
    }',
    'Gewässerachse hat keine Relation zu Kanal.')
ON CONFLICT (id) DO NOTHING;