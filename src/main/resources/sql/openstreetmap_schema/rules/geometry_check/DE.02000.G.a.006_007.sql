INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.02000.G.a.006_007',
    'geometry-check',
    'AA_REO',
    '{
        "checks": {
            "type": "geom_check", "min_lat": "5239956.14", "max_lat": "6117957.42", "min_lon": "262967.13", "max_lon": "955227.0"
        }
    }',
    'Geometrie liegt außerhalb des gültigen Koordinatenbereichs.')
ON CONFLICT (id) DO NOTHING;
