INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.F.b.001',
    'attribute-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": { "type": "tag_in", "tag_key": "art", "values": ["3001", "3002", "3003"] },
        "checks": { "type": "tag_exists", "tag_key": "name" }
    }',
    'Das Tag ''name'' muss belegt sein, wenn die ''art'' 3001, 3002 oder 3003 ist.')
ON CONFLICT (id) DO NOTHING;