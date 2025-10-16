INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51004.A.b.002',
    'attribute-check',
    'AX_Transportanlage',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "lageZurErdoberflaeche" },
        "checks": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1101", "1102"] }
    }',
    'Das Tag ''lageZurErdoberflaeche'' darf nur bei der ''bauwerksfunktion'' 1101 und 1102 vorkommen.')
ON CONFLICT (id) DO NOTHING;
