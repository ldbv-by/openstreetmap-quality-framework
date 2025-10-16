INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.A.b.001',
    'attribute-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "durchfahrtshoehe" },
        "checks": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1900" }
    }',
    'Das Tag ''durchfahrtshoehe'' darf nur bei der ''bauwerksfunktion'' 1900 vorkommen.')
ON CONFLICT (id) DO NOTHING;