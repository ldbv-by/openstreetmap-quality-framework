INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.A.b.001_002',
    'attribute-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "sportart" },
        "checks": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1410", "1420", "1440", "1441", "1442"] }
    }',
    'Das Tag ''sportart'' darf nur bei der ''bauwerksfunktion'' 1410, 1420, 1440, 1441 und 1442 vorkommen.')
ON CONFLICT (id) DO NOTHING;