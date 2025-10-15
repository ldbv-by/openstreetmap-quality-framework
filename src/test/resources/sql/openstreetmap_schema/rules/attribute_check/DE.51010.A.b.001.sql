INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51010.A.b.001',
    'attribute-check',
    'AX_EinrichtungInOeffentlichenBereichen',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "kilometerangabe" },
        "checks": { "type": "tag_equals", "tag_key": "art", "value": "1410" }
    }',
    'Das Tag ''kilometerangabe'' darf nur bei der ''art'' 1410 vorkommen.')
ON CONFLICT (id) DO NOTHING;