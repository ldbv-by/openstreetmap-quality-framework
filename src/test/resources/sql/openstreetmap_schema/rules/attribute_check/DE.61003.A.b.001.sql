INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61003.A.b.001',
    'attribute-check',
    'AX_DammWallDeich',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "ergebnisDerUeberpruefung" },
        "checks": { "type": "tag_in", "tag_key": "funktion", "values": ["3001", "3003", "3004"] }
    }',
    'Das Tag ''ergebnisDerUeberpruefung'' darf nur bei der ''funktion'' 3001, 3003 und 3004 vorkommen.')
ON CONFLICT (id) DO NOTHING;
