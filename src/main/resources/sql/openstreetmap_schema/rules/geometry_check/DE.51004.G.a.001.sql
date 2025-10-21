INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51004.G.a.001',
    'geometry-check',
    'AX_Transportanlage',
    '{
        "checks": {
            "any": [
                { "type": "geom_type", "value": "Point" },
                { "type": "geom_type", "value": "LineString" }
            ]
        }
    }',
    'Als Geometrietypen sind nur Punkt- und Liniengeometrien zugelassen.')
ON CONFLICT (id) DO NOTHING;
