INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51004.A.b.001',
    'attribute-check',
    'AX_Transportanlage',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "produkt" },
        "checks": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1101", "1103"] }
    }',
    'Das Tag ''produkt'' darf nur bei der ''bauwerksfunktion'' 1101 und 1103 vorkommen.')
ON CONFLICT (id) DO NOTHING;
