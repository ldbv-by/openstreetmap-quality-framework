INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40000.G.a.008',
    'geometry-check',
    'AA_Objekt',
    '{
        "conditions": {
            "all": [
                { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrbahnachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_Gewaesserachse"] },
                {
                    "relations": {
                        "loop_info": { "type": "none" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                    }
                }
            ]
        },

        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by_boundary",
                    "min_match_count": "2",
                    "data_set_filter": {
                        "aggregator": "union_split",
                        "criteria": {
                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_Weg", "AX_Wald", "AX_UnlandVegetationsloseFlaeche", "AX_TagebauGrubeSteinbruch", "AX_Sumpf", "AX_Strassenverkehr", "AX_StehendesGewaesser", "AX_SportFreizeitUndErholungsflaeche", "AX_Siedlungsflaeche",
                                                                                    "AX_Schiffsverkehr", "AX_Platz", "AX_Moor", "AX_Meer", "AX_Landwirtschaft", "AX_IndustrieUndGewerbeflaeche", "AX_Heide", "AX_Halde", "AX_Hafenbecken",  "AX_Gebietsgrenze",
                                                                                    "AX_Gehoelz", "AX_Friedhof", "AX_Flugverkehr", "AX_Fliessgewaesser", "AX_FlaecheZurZeitUnbestimmbar", "AX_FlaecheGemischterNutzung", "AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung"]
                        }
                    }
                },
                {
                    "all": [
                        {
                            "type": "spatial_compare",
                            "operator": "equals_topo",
                            "data_set_filter": {
                                "aggregator": "union",
                                "criteria": {
                                    "all": [
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                        { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                    ]
                                }
                            }
                        },
                        {
                            "type": "spatial_compare",
                            "operator": "covered_by_boundary",
                            "data_set_filter": {
                                "aggregator": "union",
                                "criteria": {
                                    "any": [
                                        {
                                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_Weg", "AX_Wald", "AX_UnlandVegetationsloseFlaeche", "AX_TagebauGrubeSteinbruch", "AX_Sumpf", "AX_Strassenverkehr", "AX_StehendesGewaesser", "AX_SportFreizeitUndErholungsflaeche", "AX_Siedlungsflaeche",
                                                                                                    "AX_Schiffsverkehr", "AX_Platz", "AX_Moor", "AX_Meer", "AX_Landwirtschaft", "AX_IndustrieUndGewerbeflaeche", "AX_Heide", "AX_Halde", "AX_Hafenbecken",  "AX_Gebietsgrenze",
                                                                                                    "AX_Gehoelz", "AX_Friedhof", "AX_Flugverkehr", "AX_Fliessgewaesser", "AX_FlaecheZurZeitUnbestimmbar", "AX_FlaecheGemischterNutzung", "AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung"]
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                },
                {
                    "all": [
                        {
                            "type": "spatial_compare",
                            "operator": "touches_endpoint_only",
                            "min_match_count": "1",
                            "max_match_count": "1",
                            "data_set_filter": {
                                "criteria": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrbahnachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_Gewaesserachse"] }
                            }
                        },
                        {
                            "type": "spatial_compare",
                            "operator": "covered_by_boundary",
                            "data_set_filter": {
                                "criteria": {
                                    "any": [
                                        {
                                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_Weg", "AX_Wald", "AX_UnlandVegetationsloseFlaeche", "AX_TagebauGrubeSteinbruch", "AX_Sumpf", "AX_Strassenverkehr", "AX_StehendesGewaesser", "AX_SportFreizeitUndErholungsflaeche", "AX_Siedlungsflaeche",
                                                                                                    "AX_Schiffsverkehr", "AX_Platz", "AX_Moor", "AX_Meer", "AX_Landwirtschaft", "AX_IndustrieUndGewerbeflaeche", "AX_Heide", "AX_Halde", "AX_Hafenbecken",  "AX_Gebietsgrenze",
                                                                                                    "AX_Gehoelz", "AX_Friedhof", "AX_Flugverkehr", "AX_Fliessgewaesser", "AX_FlaecheZurZeitUnbestimmbar", "AX_FlaecheGemischterNutzung", "AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung"]
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }',
    '''AX_Strassenachse'', ''AX_Fahrbahnachse'', ''AX_Fahrwegachse'', ''AX_Bahnstrecke'', ''AX_Gewaesserachse'' muss links und rechts jeweils eine eigenständige TN-Fläche haben.')
ON CONFLICT (id) DO NOTHING;
