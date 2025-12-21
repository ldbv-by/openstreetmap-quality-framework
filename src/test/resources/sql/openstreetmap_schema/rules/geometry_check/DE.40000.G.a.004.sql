INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40000.G.a.004',
    'geometry-check',
    'AA_hatDirektUnten',
    '{
        "checks": {
            "relation_members": {
                "role": "over",
                "checks": {
                    "not": {
                        "type": "spatial_compare",
                        "operator": "overlaps",
                        "data_set_filter": {
                            "criteria": {
                                "all": [
                                    {
                                        "type": "tag_in", "tag_key": "object_type", "values": ["AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung",
                                                                                               "AX_FlaecheGemischterNutzung", "AX_Fliessgewaesser", "AX_Flugverkehr", "AX_Friedhof", "AX_Gehoelz",
                                                                                               "AX_Hafenbecken", "AX_Halde", "AX_Heide", "AX_IndustrieUndGewerbeflaeche", "AX_Landwirtschaft",
                                                                                               "AX_Meer", "AX_Moor", "AX_Platz", "AX_Schiffsverkehr", "AX_Siedlungsflaeche", "AX_SportFreizeitUndErholungsflaeche",
                                                                                               "AX_StehendesGewaesser", "AX_Strassenverkehr", "AX_Sumpf", "AX_TagebauGrubeSteinbruch",
                                                                                               "AX_UnlandVegetationsloseFlaeche", "AX_Wald", "AX_Weg", "AX_Wohnbauflaeche"]
                                    },
                                    {
                                        "type": "relation_exists",
                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                        "relation_members": {
                                            "criteria": { "type": "tag_equals", "tag_key": "identifikator:UUID", "value": "current:identifikator:UUID" }
                                        }

                                    }
                                ]
                            }
                        }
                    }
                }
            }
        }
    }',
    'Members auf einem Bauwerk im Verkehrsbereich oder Gewässerbereich müssen überschneidungsfrei sein.')
ON CONFLICT (id) DO NOTHING;
