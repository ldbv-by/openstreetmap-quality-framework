INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41002.A.b.004',
    'attribute-check',
    'AX_IndustrieUndGewerbeflaeche',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "primaerenergie" },
        "checks": { "type": "tag_in", "tag_key": "funktion", "values": ["2500", "2530", "2570"] }
    }',
    'Das Tag ''primaerenergie'' darf nur bei der ''funktion'' 2500, 2530 und 2570 vorkommen.')
ON CONFLICT (id) DO NOTHING;