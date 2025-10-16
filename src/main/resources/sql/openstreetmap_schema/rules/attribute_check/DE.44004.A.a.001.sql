INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44004.A.a.001',
    'attribute-check',
    'AX_Gewaesserachse',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "hydrologischesMerkmal" },
        "checks": { "not": { "type": "tag_equals", "tag_key": "funktion", "value": "8300" } }
    }',
    'Das Tag ''hydrologischesMerkmal'' darf nicht bei der ''funktion'' 8300 vorkommen.')
ON CONFLICT (id) DO NOTHING;