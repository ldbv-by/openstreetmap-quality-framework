INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.G.a.001',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "1470" },
        "checks": { "type": "geom_type", "value": "Polygon" }
    }',
    'Ein Wasserliegeplatz darf nur flächenförmig modelliert werden.')
ON CONFLICT (id) DO NOTHING;
