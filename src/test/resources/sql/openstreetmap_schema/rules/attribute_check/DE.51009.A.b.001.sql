INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51009.A.b.001',
    'attribute-check',
    'AX_SonstigesBauwerkOderSonstigeEinrichtung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "hydrologischesMerkmal" },
        "checks": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1780", "1781"] }
    }',
    'Das Tag ''hydrologischesMerkmal'' darf nur bei der ''bauwerksfunktion'' 1780 und 1781 vorkommen.')
ON CONFLICT (id) DO NOTHING;