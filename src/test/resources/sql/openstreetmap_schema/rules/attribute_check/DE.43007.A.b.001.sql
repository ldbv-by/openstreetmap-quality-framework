INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.43007.A.b.001',
    'attribute-check',
    'AX_UnlandVegetationsloseFlaeche',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "oberflaechenmaterial" },
        "checks": { "type": "tag_equals", "tag_key": "funktion", "value": "1000" }
    }',
    'Das Tag ''oberflaechenmaterial'' darf nur bei der ''funktion'' 1000 vorkommen.')
ON CONFLICT (id) DO NOTHING;