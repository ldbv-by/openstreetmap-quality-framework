INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61003.G.a.001',
    'geometry-check',
    'AX_DammWallDeich',
    '{
        "checks": {
            "any": [
                { "type": "geom_type", "value": "LineString" },
                { "type": "geom_type", "value": "Polygon" }
            ]
        }
    }',
    'Als Geometrietypen sind nur Linien- und Flächengeometrien zugelassen.')
ON CONFLICT (id) DO NOTHING;
