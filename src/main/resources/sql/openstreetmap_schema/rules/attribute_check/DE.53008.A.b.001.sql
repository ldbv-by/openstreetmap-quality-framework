INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.A.b.001',
    'attribute-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "kilometerangabe" },
        "checks": { "type": "tag_equals", "tag_key": "art", "value": "1430" }
    }',
    'Das Tag ''kilometerangabe'' darf nur bei der ''art'' 1430 vorkommen.')
ON CONFLICT (id) DO NOTHING;