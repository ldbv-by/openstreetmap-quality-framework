INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.A.b.001',
    'attribute-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "funktion", "value": "1200" },
        "checks": { "type": "tag_exists", "tag_key": "istWeitereNutzung" }
    }',
    'Das Tag ''istWeitereNutzung'' muss belegt sein, sobald ''funktion'' den Wert 1200 hat.')
ON CONFLICT (id) DO NOTHING;

