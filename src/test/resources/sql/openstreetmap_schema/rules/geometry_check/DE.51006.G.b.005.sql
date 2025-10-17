INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.005',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1480" },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_FlaecheBesondererFunktionalerPraegung",
                                                                      "funktion": "1170|not_exists" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche",
                                                                      "funktion": "4100" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_SonstigesRecht",
                                                                      "artDerFestlegung": "4720" } } }
                }
            ]
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1480 muss ein Objekt ''AX_FlaecheBesondererFunktionalerPraegung'' ohne ''funktion'' oder mit ''funktion'' 1170 oder ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4100 oder ''AX_SonstigesRecht'' mit ''artDerFestlegung'' 4720 überlagern.')
ON CONFLICT (id) DO NOTHING;
