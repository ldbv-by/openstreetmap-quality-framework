INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71006.F.b.001',
    'attribute-check',
    'AX_NaturUmweltOderBodenschutzrecht',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "1621" },
        "checks": { "type": "tag_exists", "tag_key": "name" }
    }',
    'Das Tag ''name'' muss belegt sein, wenn ''artDerFestlegung'' 1621 ist.')
ON CONFLICT (id) DO NOTHING;
