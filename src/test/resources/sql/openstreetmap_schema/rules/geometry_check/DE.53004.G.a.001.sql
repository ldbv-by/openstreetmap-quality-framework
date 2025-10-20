INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53004.G.a.001',
    'geometry-check',
    'AX_Bahnverkehrsanlage',
    '{
        "checks": {
            "any": [
                { "type": "geom_type", "value": "Point" },
                { "type": "geom_type", "value": "Polygon" }
            ]
        }
    }',
    'Als Geometrietypen sind nur Punkt- und Flächengeometrien zugelassen.')
ON CONFLICT (id) DO NOTHING;