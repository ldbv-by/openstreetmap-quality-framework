INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71012.A.c.001',
    'attribute-check',
    'AX_Schutzzone',
    '{
        "checks": { "not": { "type": "tag_equals", "tag_key": "zone", "value": "1090" } }
    }',
    'Das Tag ''zone'' darf nicht mit dem Wert 1090 belegt werden.')
ON CONFLICT (id) DO NOTHING;
