INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.A.b.002',
    'attribute-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "strassenschluessel" },
        "checks": { "type": "tag_equals", "tag_key": "art", "value": "4000" }
    }',
    'Das Tag ''strassenschluessel'' darf nur bei der ''art'' 4000 vorkommen.')
ON CONFLICT (id) DO NOTHING;