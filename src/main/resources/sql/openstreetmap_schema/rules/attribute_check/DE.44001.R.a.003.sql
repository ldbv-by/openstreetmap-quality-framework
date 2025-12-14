INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44001.R.a.003',
    'attribute-check',
    'AX_Fliessgewaesser',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "funktion", "value": "8300" },
        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Kanal" }
            }
        }
    }',
    'Fließgewässer hat keine Relation zu Kanal.')
ON CONFLICT (id) DO NOTHING;