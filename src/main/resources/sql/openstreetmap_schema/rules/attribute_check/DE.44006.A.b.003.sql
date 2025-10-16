INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44006.A.b.003',
    'attribute-check',
    'AX_StehendesGewaesser',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "zustand" },
        "checks": { "type": "tag_in", "tag_key": "funktion", "values": ["8630", "8631", "8640"] }
    }',
    'Das Tag ''zustand'' darf nur bei der ''funktion'' 8630, 8631 und 8640 vorkommen.')
ON CONFLICT (id) DO NOTHING;
