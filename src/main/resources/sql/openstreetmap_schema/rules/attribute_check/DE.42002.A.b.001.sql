INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.A.b.001',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "internationaleBedeutung", "value": "2001" },
        "checks": { "type": "tag_exists", "tag_key": "bezeichnung" }
    }',
    'Das Tag ''bezeichnung'' muss belegt sein, wenn die ''internationaleBedeutung'' 2001 gesetzt ist.')
ON CONFLICT (id) DO NOTHING;