INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51003.G.a.001',
    'geometry-check',
    'AX_VorratsbehaelterSpeicherbauwerk',
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
