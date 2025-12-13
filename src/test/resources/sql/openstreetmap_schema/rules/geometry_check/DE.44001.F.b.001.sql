INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44001.F.b.001',
    'geometry-check',
    'AX_Fliessgewaesser',
    '{
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "intersects",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrbahnachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_WegPfadSteig", "AX_Gleis"] },
                            {
                                "not": {
                                    "type": "relation_exists",
                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                    "relation_members": {
                                        "criteria": {
                                            "any": [
                                                { "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich"] },
                                                {
                                                    "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenverkehrsanlage" },
                                                        { "type": "tag_equals", "tag_key": "art", "value": "2000" }
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
        }
    }',
    'Ein Fließgewässer darf keine gemeinsamen Kanten mit Verkehrsachsen führen, wenn nicht eine Relation zu einem Bauwerk oder einer Straßenverkehrsanlage besteht.')
ON CONFLICT (id) DO NOTHING;
