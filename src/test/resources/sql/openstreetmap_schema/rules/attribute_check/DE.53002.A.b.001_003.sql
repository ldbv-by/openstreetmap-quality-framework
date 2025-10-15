INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.A.b.001_003',
    'attribute-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "bezeichnung" },
        "checks": { "type": "tag_between", "tag_key": "art", "from_value": "3000", "to_value": "3003" }
    }',
    'Das Tag ''bezeichnung'' darf nur bei der ''art'' 3000 bis 3003 vorkommen.')
ON CONFLICT (id) DO NOTHING;