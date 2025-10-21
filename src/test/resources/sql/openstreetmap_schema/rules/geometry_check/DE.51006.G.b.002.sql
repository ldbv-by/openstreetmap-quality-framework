INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.002',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1441", "1442"] },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_FlaecheBesondererFunktionalerPraegung" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche",
                                                                      "funktion": "4100|4140|4170|4320" } } }
                }
            ]
        }
    }',
    'Die Wertearten mit der ''bauwerksfunktion'' 1441 und 1442 müssen ''AX_FlaecheBesondererFunktionalerPraegung'' oder ''AX_SportFreizeitUndErholungsflaeche'' mit ''funktion'' 4100, 4140, 4170 oder 4320 überlagern.')
ON CONFLICT (id) DO NOTHING;