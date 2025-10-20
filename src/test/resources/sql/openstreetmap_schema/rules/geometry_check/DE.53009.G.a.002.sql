INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.a.002',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "2120" },
        "checks": { "type": "geom_type", "value": "Point" }
    }',
    'Ein Pegel darf nur punktförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
