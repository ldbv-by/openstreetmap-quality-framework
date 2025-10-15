INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51009.A.b.002',
    'attribute-check',
    'AX_SonstigesBauwerkOderSonstigeEinrichtung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "funktion" },
        "checks": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1700" }
    }',
    'Das Tag ''funktion'' darf nur bei der ''bauwerksfunktion'' 1700 vorkommen.')
ON CONFLICT (id) DO NOTHING;