INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.006',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1470" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche",
                                                                      "funktion": "4100" } } }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1470 muss ein Objekt ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4100 überlagern.')
ON CONFLICT (id) DO NOTHING;