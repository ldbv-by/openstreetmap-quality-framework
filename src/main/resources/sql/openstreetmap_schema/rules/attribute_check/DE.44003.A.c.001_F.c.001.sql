INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44003.A.c.001_F.c.001',
    'attribute-check',
    'AX_Kanal',
    '{
        "checks": {
            "all": [
                {
                    "relation_members": {
                        "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                        "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
                    }
                },

                {
                    "any": [
                        {
                            "type": "spatial_compare",
                            "operator": "covers",
                            "data_set_filter": {
                                "aggregator": "union",
                                "criteria": {
                                    "all": [
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserstationierungsachse" },
                                        { "not": { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "3001" } },
                                        { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
                                    ]
                                }
                            }
                        },
                        {
                            "not": {
                                "type": "spatial_compare",
                                "operator": "covers",
                                "data_set_filter": {
                                    "aggregator": "union",
                                    "criteria": {
                                        "all": [
                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserstationierungsachse" },
                                            { "not": { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "3001" } }
                                        ]
                                    }
                                }
                            }
                        }
                    ]
                },

                {
                    "relation_members": {
                        "checks": {
                            "any": [
                                {
                                    "relations": {
                                        "loop_info": { "type": "none" },
                                        "checks": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                                {
                                                    "relation_members": {
                                                        "loop_info": { "type": "any" },
                                                        "checks": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich"] }
                                                    }
                                                }
                                            ]
                                        }
                                    }
                                },
                                {
                                    "relations": {
                                        "loop_info": { "type": "all" },
                                        "conditions": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                                {
                                                    "relation_members": {
                                                        "loop_info": { "type": "any" },
                                                        "checks": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_BauwerkImVerkehrsbereich", "AX_BauwerkImGewaesserbereich"] }
                                                    }
                                                }
                                            ]
                                        },
                                        "checks": {
                                            "relation_members": {
                                                "loop_info": { "type": "any" },
                                                "checks": {
                                                    "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserstationierungsachse" },
                                                        { "type": "tag_equals", "tag_key": "gewaesserkennzahl", "value": "base:gewaesserkennzahl" }
                                                    ]
                                                }
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
    }',
    'Die zu ''AX_Kanal'' gehörenden ''AX_Gewaesserachse'' dürfen keine Fliessrichtung haben.')
ON CONFLICT (id) DO NOTHING;