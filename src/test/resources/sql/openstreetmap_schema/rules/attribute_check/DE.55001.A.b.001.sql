INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.A.b.001',
    'attribute-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "objekthoehe" },
        "checks": { "type": "tag_equals", "tag_key": "art", "value": "1620" }
    }',
    'Das Tag ''objekthoehe'' darf nur bei der ''art'' 1620 vorkommen.')
ON CONFLICT (id) DO NOTHING;

