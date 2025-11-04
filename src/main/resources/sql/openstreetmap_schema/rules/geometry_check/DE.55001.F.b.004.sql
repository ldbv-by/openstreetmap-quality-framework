INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.F.b.004',
    'geometry-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "art", "value": "1620" },
                { "type": "geom_type", "value": "Point" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                        {
                            "any": [
                                { "not": { "type": "tag_equals", "tag_key": "funktion", "value": "8300" } },
                                { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein Objekt ''AX_Gewaessermerkmal'' mit der ''art'' 1620 muss bei punkförmiger Modellierung auf ''AX_Gewaesserachse'' ohne ''funktion'' 8300 liegen.')
ON CONFLICT (id) DO NOTHING;