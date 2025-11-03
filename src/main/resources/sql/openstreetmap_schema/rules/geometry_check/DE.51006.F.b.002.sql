INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.F.b.002',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "1440", "to_value": "1442" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_FlaecheBesondererFunktionalerPraegung" },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SportFreizeitUndErholungsflaeche" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "4100" }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Die Wertearten mit der ''bauwerksfunktion'' 1440 bis 1442 müssen ''AX_FlaecheBesondererFunktionalerPraegung'' oder ''AX_SportFreizeitUndErholungsflaeche'' mit ''funktion'' 4100 überlagern.')
ON CONFLICT (id) DO NOTHING;
