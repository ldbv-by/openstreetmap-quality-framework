INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.73006.A.b.001',
    'attribute-check',
    'AX_Gemeindeteil',
    '{
        "checks": { "type": "tag_exists", "tag_key": "gemeindekennzeichen:gemeindeteil" }
    }',
    'Das Tag ''gemeindekennzeichen:gemeindeteil'' muss bei einem Gemeindeteil belegt sein.')
ON CONFLICT (id) DO NOTHING;
