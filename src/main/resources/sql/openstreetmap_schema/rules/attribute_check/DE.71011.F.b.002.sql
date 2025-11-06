INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71011.F.b.002',
    'attribute-check',
    'AX_SonstigesRecht',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "4720" },
        "checks": { "type": "tag_exists", "tag_key": "name" }
    }',
    'Das Tag ''name'' muss belegt sein, wenn ''artDerFestlegung'' 4720 ist.')
ON CONFLICT (id) DO NOTHING;