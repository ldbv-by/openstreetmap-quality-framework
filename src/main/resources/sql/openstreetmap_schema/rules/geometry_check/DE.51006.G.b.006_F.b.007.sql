INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.006_F.b.007',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1470" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SportFreizeitUndErholungsflaeche" },
                        { "type": "tag_equals", "tag_key": "funktion", "value": "4100" }
                    ]
                }
            }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1470 muss ein Objekt ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4100 überlagern.')
ON CONFLICT (id) DO NOTHING;