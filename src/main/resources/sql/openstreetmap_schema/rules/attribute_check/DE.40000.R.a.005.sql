INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40000.R.a.005',
    'attribute-check',
    'AA_hatDirektUnten',
    '{
        "checks": {
            "not": {
                "all": [
                    {
                        "relation_members": {
                            "loop_info": { "type": "count", "minCount": "1" },
                            "role": "over",
                            "checks": {
                                "type": "tag_in", "tag_key": "object_type", "values": ["AX_Bahnstrecke",
                                                                                       "AX_Bahnverkehr",
                                                                                       "AX_Bergbaubetrieb",
                                                                                       "AX_Fahrbahnachse",
                                                                                       "AX_Fahrwegachse",
                                                                                       "AX_FlaecheBesondererFunktionalerPraegung",
                                                                                       "AX_FlaecheGemischterNutzung",
                                                                                       "AX_Fliessgewaesser",
                                                                                       "AX_Flugverkehr",
                                                                                       "AX_Friedhof",
                                                                                       "AX_Gehoelz",
                                                                                       "AX_Gewaesserachse",
                                                                                       "AX_Hafenbecken",
                                                                                       "AX_Halde",
                                                                                       "AX_Heide",
                                                                                       "AX_IndustrieUndGewerbeflaeche",
                                                                                       "AX_Landwirtschaft",
                                                                                       "AX_Meer",
                                                                                       "AX_Moor",
                                                                                       "AX_Platz",
                                                                                       "AX_Schiffsverkehr",
                                                                                       "AX_Siedlungsflaeche",
                                                                                       "AX_SportFreizeitUndErholungsflaeche",
                                                                                       "AX_StehendesGewaesser",
                                                                                       "AX_Strassenachse",
                                                                                       "AX_Strassenverkehr",
                                                                                       "AX_Sumpf",
                                                                                       "AX_TagebauGrubeSteinbruch",
                                                                                       "AX_UnlandVegetationsloseFlaeche",
                                                                                       "AX_Wald",
                                                                                       "AX_Weg",
                                                                                       "AX_Wohnbauflaeche"]
                            }
                        }
                    },

                    {
                        "relation_members": {
                            "loop_info": { "type": "count", "minCount": "1" },
                            "role": "under",
                            "checks": {
                                "type": "tag_in", "tag_key": "object_type", "values": ["AX_Bahnstrecke",
                                                                                       "AX_Bahnverkehr",
                                                                                       "AX_Bergbaubetrieb",
                                                                                       "AX_Fahrbahnachse",
                                                                                       "AX_Fahrwegachse",
                                                                                       "AX_FlaecheBesondererFunktionalerPraegung",
                                                                                       "AX_FlaecheGemischterNutzung",
                                                                                       "AX_Fliessgewaesser",
                                                                                       "AX_Flugverkehr",
                                                                                       "AX_Friedhof",
                                                                                       "AX_Gehoelz",
                                                                                       "AX_Gewaesserachse",
                                                                                       "AX_Hafenbecken",
                                                                                       "AX_Halde",
                                                                                       "AX_Heide",
                                                                                       "AX_IndustrieUndGewerbeflaeche",
                                                                                       "AX_Landwirtschaft",
                                                                                       "AX_Meer",
                                                                                       "AX_Moor",
                                                                                       "AX_Platz",
                                                                                       "AX_Schiffsverkehr",
                                                                                       "AX_Siedlungsflaeche",
                                                                                       "AX_SportFreizeitUndErholungsflaeche",
                                                                                       "AX_StehendesGewaesser",
                                                                                       "AX_Strassenachse",
                                                                                       "AX_Strassenverkehr",
                                                                                       "AX_Sumpf",
                                                                                       "AX_TagebauGrubeSteinbruch",
                                                                                       "AX_UnlandVegetationsloseFlaeche",
                                                                                       "AX_Wald",
                                                                                       "AX_Weg",
                                                                                       "AX_Wohnbauflaeche"]
                            }
                        }
                    }
                ]
            }
        }
    }',
    'Ein Objekt aus dem Objektartenbereich "Tatsächliche Nutzung" darf keine Relation hatDirektUnten zu einem anderen Objekt aus dem Objektartenbereich "Tatsächliche Nutzung" haben.')
ON CONFLICT (id) DO NOTHING;
