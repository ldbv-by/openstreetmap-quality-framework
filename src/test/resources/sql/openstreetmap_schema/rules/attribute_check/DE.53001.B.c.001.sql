INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.B.c.001',
    'attribute-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "checks": {
            "relations": {
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_objekthoehe" },
                "checks": { "type": "number_compare", "tag_key": "hoehe", "operator": ">", "compare_value": "0" }
            }
        }
    }',
    'Das Tag ''hoehe'' der Relation ''AX_objekthoehe'' muss größer Null sein.')
ON CONFLICT (id) DO NOTHING;
