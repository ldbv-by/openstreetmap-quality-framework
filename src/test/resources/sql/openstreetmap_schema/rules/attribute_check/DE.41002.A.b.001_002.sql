INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41002.A.b.001_002',
    'attribute-check',
    'AX_IndustrieUndGewerbeflaeche',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "foerdergut" },
        "checks": { "type": "tag_in", "tag_key": "funktion", "values": ["2510", "2700"] }
    }',
    'Das Tag ''foerdergut'' darf nur bei der ''funktion'' 2510 und 2700 vorkommen.')
ON CONFLICT (id) DO NOTHING;