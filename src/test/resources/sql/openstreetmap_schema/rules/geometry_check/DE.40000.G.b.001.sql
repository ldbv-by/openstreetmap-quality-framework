INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40000.G.b.001',
    'geometry-check',
    'AA_Objekt',
    '{
        "conditions": {
            "all": [
                { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Bahnstrecke", "AX_Fahrbahnachse", "AX_Fahrwegachse", "AX_Strassenachse"] },
                {
                    "type": "spatial_compare",
                    "operator": "equals_topo",
                    "data_set_filter": {
                        "criteria": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Bahnstrecke", "AX_Fahrbahnachse", "AX_Fahrwegachse", "AX_Strassenachse",
                                                                                             "AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich", "AX_DammWallDeich"] }
                    }
                }
            ]
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
                                        "role": "under",
                                        "checks": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich", "AX_DammWallDeich"] }
                                    }
                                }
                            ]
                        }
                    }
                },

                {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Bahnstrecke", "AX_Fahrbahnachse", "AX_Strassenachse"] },
                        {
                            "not": {
                                "type": "spatial_compare",
                                "operator": "equals_topo",
                                "data_set_filter": {
                                    "criteria": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Fahrwegachse",
                                                                                                        "AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich", "AX_DammWallDeich",
                                                                                                        "current:object_type"] }
                                }
                            }
                        }
                    ]
                },

                {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Bahnstrecke", "AX_Fahrbahnachse", "AX_Strassenachse", "AX_Fahrwegachse"] },
                        {
                            "relations": {
                                "loop_info": { "type": "none" },
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                            }
                        },
                        {
                            "type": "spatial_compare",
                            "operator": "equals_topo",
                            "data_set_filter": {
                                "criteria": {
                                    "all": [
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImVerkehrsbereich" },
                                        { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1900", "1880"] }
                                    ]
                                }
                            }
                        }
                    ]
                }
            ]
        }

    }',
    'Geometrieidentische Überlagerung darf nur mit HDU vorkommen.')
ON CONFLICT (id) DO NOTHING;
