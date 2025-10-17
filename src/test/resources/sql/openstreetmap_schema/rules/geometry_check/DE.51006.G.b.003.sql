INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.003',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1450" },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_FlaecheBesondererFunktionalerPraegung" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche",
                                                                      "funktion": "4100|4200|4320|4330" } } }
                }
            ]
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1450 muss ein Objekt ''AX_FlaecheBesondererFunktionalerPraegung'' oder ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4100, 4200, 4320 oder 4330 überlagern.')
ON CONFLICT (id) DO NOTHING;
