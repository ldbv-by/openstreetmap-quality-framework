INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75012.G.a.003_G.b.001',
    'geometry-check',
    'AX_KommunalesTeilgebiet',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by_multiline_as_polygon",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesGebiet" },
                        { "not": { "type": "tag_exists", "tag_key": "gemeindekennzeichen:gemeindeteil" } }
                    ]
                }
            }
        }
    }',
    'Gemeindeteil liegt nicht auf Kommunalem Gebiet oder wird darin bereits verwendet.')
ON CONFLICT (id) DO NOTHING;


