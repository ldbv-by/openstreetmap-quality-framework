INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44001.A.b.002',
    'attribute-check',
    'AX_Fliessgewaesser',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "hydrologischesMerkmal" },
        "checks": { "not": { "type": "tag_equals", "tag_key": "funktion", "value": "8300" } }
    }',
    'Das Tag ''hydrologischesMerkmal'' darf nicht bei der ''funktion'' 8300 vorkommen.')
ON CONFLICT (id) DO NOTHING;