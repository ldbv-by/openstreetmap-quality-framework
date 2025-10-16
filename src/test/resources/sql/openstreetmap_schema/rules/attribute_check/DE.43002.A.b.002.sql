INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.43002.A.b.002',
    'attribute-check',
    'AX_Wald',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "nutzung", "value": "3000" },
        "checks": { "type": "tag_exists", "tag_key": "name" }
    }',
    'Das Tag ''name'' muss belegt sein, wenn das Tag ''nutzung'' den Wert 3000 hat.')
ON CONFLICT (id) DO NOTHING;

