INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42014.A.b.001',
    'attribute-check',
    'AX_Bahnstrecke',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "spurweite", "value": "9997" },
        "checks": { "type": "tag_equals", "tag_key": "bahnkategorie", "value": "1600" }
    }',
    'Das Tag ''spurweite'' mit dem Wert 9997 darf nur bei der ''bahnkategorie'' 1600 vorkommen.')
ON CONFLICT (id) DO NOTHING;

