INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.F.b.002',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "1440", "to_value": "1442" },
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
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche", "funktion": "4100" } } }
                }
            ]
        }
    }',
    'Die Wertearten mit der ''bauwerksfunktion'' 1440 bis 1442 müssen ''AX_FlaecheBesondererFunktionalerPraegung'' oder ''AX_SportFreizeitUndErholungsflaeche'' mit ''funktion'' 4100 überlagern.')
ON CONFLICT (id) DO NOTHING;
