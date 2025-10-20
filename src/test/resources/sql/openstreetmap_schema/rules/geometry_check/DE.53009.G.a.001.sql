INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.a.001',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "2020" },
        "checks": { "type": "geom_type", "value": "Polygon" }
    }',
    'Ein Rückhaltebecken darf nur flächenförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
