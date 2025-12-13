INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44007.F.b.001',
    'geometry-check',
    'AX_Meer',
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
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_EinrichtungenFuerDenSchiffsverkehr" },
                                                        { "type": "tag_equals", "tag_key": "art", "value": "1460" }
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
    'Ein Meer darf keine gemeinsamen Kanten mit Verkehrsachsen führen, wenn nicht eine Relation zu einem Bauwerk besteht.')
ON CONFLICT (id) DO NOTHING;
