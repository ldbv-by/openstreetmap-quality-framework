INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42015.A.b.001',
    'attribute-check',
    'AX_Flugverkehr',
    '{
        "conditions": { "not": { "type": "tag_equals", "tag_key": "funktion", "value": "1200" } },
        "checks": { "type": "tag_exists", "tag_key": "art" }
    }',
    'Das Tag ''art'' muss belegt sein, außer bei der ''funktion'' 1200.')
ON CONFLICT (id) DO NOTHING;
