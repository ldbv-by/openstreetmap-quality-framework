INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51002.A.b.001',
    'attribute-check',
    'AX_BauwerkOderAnlageFuerIndustrieUndGewerbe',
    '{
        "conditions": {
            "not": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1220", "1250", "1251", "1260", "1270", "1280", "1290", "1350"] }
        },
        "checks": { "relation": { "not": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_RelativeHoehe" } } }
    }',
    'Die Relation ''AX_RelativeHoehe'' darf nur bei der ''bauwerksfunktion'' 1220, 1250, 1251, 1260, 1270, 1280, 1290 und 1350 vorkommen.')
ON CONFLICT (id) DO NOTHING;
