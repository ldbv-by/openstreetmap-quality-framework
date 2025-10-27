INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.004_F.b.004',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1490" },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_FlaecheBesondererFunktionalerPraegung",
                                                                      "funktion": "1150|not_exists" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche",
                                                                      "funktion": "4200|4400|4420" } } }
                }
            ]
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1490 muss ein Objekt ''AX_FlaecheBesondererFunktionalerPraegung'' ohne ''funktion'' oder mit ''funktion'' 1150 oder ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4200, 4400, oder 4420 überlagern.')
ON CONFLICT (id) DO NOTHING;