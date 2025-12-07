INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.002',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1441", "1442"] },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "any": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_FlaecheBesondererFunktionalerPraegung" },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SportFreizeitUndErholungsflaeche" },
                                { "type": "tag_in", "tag_key": "funktion", "values": [ "4100", "4140", "4170" ] }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SportFreizeitUndErholungsflaeche" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "4320" },
                                {
                                    "type": "relation_exists",
                                    "criteria": {
                                        "all": [
                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AA_zeigtAufExternes" },
                                            { "type": "tag_regex_match", "tag_key": "art", "pattern": "^urn:[A-Za-z]+:fdv:2600$" },
                                            { "type": "tag_equals", "tag_key": "fachdatenobjekt:name", "value": "221430 LN_Sportanlage SPO1040 Schwimmen" }
                                        ]
                                    }
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein Stadion darf sich nur auf Fläche besonderer funktionaler Prägung oder Sport-, Freizeit- und Erholungsfläche mit den Funktionen Sportanlage, Reitsport, Tennis, Schwimmen (mit FDV zum Leistungsschwimmen) befinden.')
ON CONFLICT (id) DO NOTHING;