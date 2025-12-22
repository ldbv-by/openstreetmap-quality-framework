INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.R.b.001',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '
    {
        "checks": {
            "any": [
                {
                    "all": [
                        { "not": { "type": "tag_equals", "tag_key": "art", "value": "1460" } },
                        {
                            "relations": {
                                "loop_info": { "type": "none" },
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                            }
                        }
                    ]
                },
                {
                    "relations": {
                        "loop_info": { "type": "all" },
                        "conditions": {
                            "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten"
                        },
                        "checks": {
                            "relation_members": {
                                "loop_info": { "type": "all" },
                                "checks": {
                                    "any": [
                                        {
                                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrwegachse", "AX_Fahrbahnachse", "AX_WegPfadSteig", "AX_Bahnstrecke", "AX_Gleis",
                                                                                                    "AX_Gewaesserachse", "AX_Strassenverkehr", "AX_Bahnverkehr", "AX_Fliessgewaesser", "Wohnbauflaeche",
                                                                                                    "AX_IndustrieUndGewerbeflaeche", "AX_FlaecheGemischterNutzung", "AX_FlaecheBesondererFunktionalerPraegung",
                                                                                                    "AX_Platz", "AX_Schiffsverkehr"]
                                        },
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_EinrichtungenFuerDenSchiffsverkehr" },
                                                { "type": "tag_equals", "tag_key": "art", "value": "1460" }
                                            ]
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            ]
        }
    }',
    'Der Anleger wird nicht korrekt referenziert.')
ON CONFLICT (id) DO NOTHING;