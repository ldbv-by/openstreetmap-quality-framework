INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75012.A.b.002',
    'attribute-check',
    'AX_KommunalesTeilgebiet',
    '{
        "checks": { "type": "tag_exists", "tag_key": "kennzeichen:gemeindeteil" }
    }',
    'Das Tag ''kennzeichen:gemeindeteil'' muss bei einem Gemeindeteil belegt sein.')
ON CONFLICT (id) DO NOTHING;
