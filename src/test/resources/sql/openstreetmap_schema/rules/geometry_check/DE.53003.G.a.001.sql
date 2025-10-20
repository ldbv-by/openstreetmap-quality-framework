INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53003.G.a.001',
    'geometry-check',
    'AX_WegPfadSteig',
    '{
        "checks": { "type": "geom_type", "value": "LineString" }
    }',
    'Als Geometrietyp ist nur Liniengeometrie zugelassen.')
ON CONFLICT (id) DO NOTHING;