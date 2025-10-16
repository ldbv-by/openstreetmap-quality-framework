INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.A.b.003',
    'attribute-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "hydrologischesMerkmal" },
        "checks": { "type": "tag_equals", "tag_key": "art", "value": "1610" }
    }',
    'Das Tag ''hydrologischesMerkmal'' darf nur bei der ''art'' 1610 vorkommen.')
ON CONFLICT (id) DO NOTHING;
