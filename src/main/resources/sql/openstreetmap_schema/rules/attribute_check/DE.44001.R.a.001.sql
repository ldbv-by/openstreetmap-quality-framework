INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44001.R.a.001',
    'attribute-check',
    'AX_Fliessgewaesser',
    '{
        "conditions": { "not": { "type": "tag_exists", "tag_key": "funktion" } },
        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Wasserlauf" }
            }
        }
    }',
    'Fließgewässer hat keine Relation zu Wasserlauf.')
ON CONFLICT (id) DO NOTHING;