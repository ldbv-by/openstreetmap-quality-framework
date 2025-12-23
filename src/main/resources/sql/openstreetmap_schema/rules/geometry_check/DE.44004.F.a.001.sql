INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44004.F.a.001',
    'geometry-check',
    'AX_Gewaesserachse',
    '{
        "conditions": {
            "type": "spatial_compare",
            "operator": "crosses",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        {
                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Bahnstrecke", "AX_Fahrbahnachse",
                                                                                   "AX_Fahrwegachse", "AX_WegPfadSteig", "AX_Gleis"]
                        },
                        {
                            "not": {
                                "type": "relation_exists",
                                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                "relation_members": {
                                    "criteria": {
                                        "any": [
                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImVerkehrsbereich" },
                                            {
                                                "all": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                                    { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2030", "2040", "2050", "2060", "2080"] }
                                                ]
                                            }
                                        ]
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        },

        "checks": {
            "any": [
                {
                    "relations": {
                        "loop_info": { "type": "any" },
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                {
                                    "relation_members": {
                                        "loop_info": { "type": "any" },
                                        "checks": {
                                            "any": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImVerkehrsbereich" },
                                                {
                                                    "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                                        { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2010", "2011", "2012", "2013", "2070", "2090"] }
                                                    ]
                                                }
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    }
                },

                {
                    "way_nodes": {
                        "loop_info": { "type": "any" },
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Straßenverkehrsanlage" },
                                { "type": "tag_equals", "tag_key": "art", "value": "2000" },
                                {
                                    "type": "spatial_compare",
                                    "operator": "covered_by",
                                    "data_set_filter": {
                                        "criteria": {
                                            "all": [
                                                {
                                                    "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Bahnstrecke", "AX_Fahrbahnachse",
                                                                                                           "AX_Fahrwegachse", "AX_WegPfadSteig", "AX_Gleis"]
                                                },
                                                {
                                                    "not": {
                                                        "type": "relation_exists",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                                        "relation_members": {
                                                            "criteria": {
                                                                "any": [
                                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImVerkehrsbereich" },
                                                                    {
                                                                        "all": [
                                                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                                                            { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2030", "2040", "2050", "2060", "2080"] }
                                                                        ]
                                                                    }
                                                                ]
                                                            }
                                                        }
                                                    }
                                                }
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            ]
        }
    }',
    'Gewässerachse kreuzt Verkehrsweg ohne BauwerkImVerkehrsbereich, BauwerkImGewaesserbereich oder Furt.')
ON CONFLICT (id) DO NOTHING;
