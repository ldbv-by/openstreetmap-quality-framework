INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51009.A.b.005',
    'attribute-check',
    'AX_SonstigesBauwerkOderSonstigeEinrichtung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "bezeichnung" },
        "checks": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1781" }
    }',
    'Das Tag ''bezeichnung'' darf nur bei der ''bauwerksfunktion'' 1781 vorkommen.')
ON CONFLICT (id) DO NOTHING;