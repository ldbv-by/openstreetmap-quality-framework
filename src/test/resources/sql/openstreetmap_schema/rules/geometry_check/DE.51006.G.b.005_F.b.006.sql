INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.005_F.b.006',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1480" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "any": [
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_FlaecheBesondererFunktionalerPraegung" },
                                {
                                    "any": [
                                        { "type": "tag_equals", "tag_key": "funktion", "value": "1170" },
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                                    ]
                                }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SportFreizeitUndErholungsflaeche" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "4100" }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SonstigesRecht" },
                                { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "4720" }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1480 muss ein Objekt ''AX_FlaecheBesondererFunktionalerPraegung'' ohne ''funktion'' oder mit ''funktion'' 1170 oder ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4100 oder ''AX_SonstigesRecht'' mit ''artDerFestlegung'' 4720 überlagern.')
ON CONFLICT (id) DO NOTHING;
