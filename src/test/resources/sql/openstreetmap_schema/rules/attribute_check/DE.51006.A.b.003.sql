INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.A.b.003',
    'attribute-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "breiteDesObjekts" },
        "checks": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1420", "1470", "1490"] }
    }',
    'Das Tag ''breiteDesObjekts'' darf nur bei der ''bauwerksfunktion'' 1420, 1470 und 1490 vorkommen.')
ON CONFLICT (id) DO NOTHING;

