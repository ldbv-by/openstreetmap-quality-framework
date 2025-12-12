INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.004_F.b.004',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1490" },
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
                                        { "type": "tag_equals", "tag_key": "funktion", "value": "1150" },
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                                    ]
                                }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SportFreizeitUndErholungsflaeche" },
                                { "type": "tag_in", "tag_key": "funktion", "values": [ "4200", "4400", "4420" ] }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1490 muss ein Objekt ''AX_FlaecheBesondererFunktionalerPraegung'' ohne ''funktion'' oder mit ''funktion'' 1150 oder ''AX_SportFreizeitUndErholungsflaeche'' mit der ''funktion'' 4200, 4400, oder 4420 überlagern.')
ON CONFLICT (id) DO NOTHING;