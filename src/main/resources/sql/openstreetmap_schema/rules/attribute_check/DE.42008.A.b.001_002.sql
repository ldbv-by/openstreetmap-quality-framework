INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42008.A.b.001_002',
    'attribute-check',
    'AX_Fahrwegachse',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "befestigung" },
        "checks": { "type": "tag_equals", "tag_key": "funktion", "value": "5212" }
    }',
    'Das Tag ''befestigung'' darf nur bei der ''funktion'' 5212 vorkommen.')
ON CONFLICT (id) DO NOTHING;
