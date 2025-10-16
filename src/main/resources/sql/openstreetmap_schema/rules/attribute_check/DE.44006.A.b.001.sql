INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44006.A.b.001',
    'attribute-check',
    'AX_StehendesGewaesser',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "bezeichnung" },
        "checks": { "type": "tag_equals", "tag_key": "funktion", "value": "8640" }
    }',
    'Das Tag ''bezeichnung'' darf nur bei der ''funktion'' 8640 vorkommen.')
ON CONFLICT (id) DO NOTHING;