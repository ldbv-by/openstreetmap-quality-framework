INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.R.a.001',
    'attribute-check',
    'AA_hatDirektUnten',
    '{
        "conditions": {
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

        "checks": {
            "relation_members": {
                "loop_info": { "type": "count", "minCount": "1", "maxCount": "1" },
                "role": "under",
                "checks": {
                    "any": [
                        {
                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich",
                                                                                   "AX_EinrichtungenFuerDenSchiffsverkehr",
                                                                                   "AX_BauwerkImGewaesserbereich",
                                                                                   "AX_Gebaeude"]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_DammWallDeich" },
                                { "type": "tag_in", "tag_key": "funktion", "values": ["3002", "3003", "3004"] }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Die Relation hatDirektUnten darf nur zu AX_BauwerkImVerkehrsbereich, AX_EinrichtungenFuerDenSchiffsverkehr, AX_BauwerkImGewaesserbereich, AX_Gebaeude oder AX_DammWallDeich mit FKT 3002, 3003 und 3004 vorhanden sein.')
ON CONFLICT (id) DO NOTHING;
