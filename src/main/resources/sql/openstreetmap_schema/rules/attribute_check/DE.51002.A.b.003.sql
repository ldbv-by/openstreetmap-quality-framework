INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51002.A.b.003',
    'attribute-check',
    'AX_BauwerkOderAnlageFuerIndustrieUndGewerbe',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "zustand", "value": "2200" },
        "checks": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1310", "1320"] }
    }',
    'Das Tag ''zustand'' mit der Werteart 2200 darf nur bei der ''bauwerksfunktion'' 1310 und 1320 vorkommen.')
ON CONFLICT (id) DO NOTHING;
