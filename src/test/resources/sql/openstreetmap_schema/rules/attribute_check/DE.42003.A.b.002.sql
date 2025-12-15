INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42003.A.b.002',
    'attribute-check',
    'AX_Strassenachse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" },
        "checks": {
            "any": [
                {
                    "way_nodes": {
                        "loop_info": { "type": "all" },
                        "conditions": {
                            "any": [
                                { "type": "way_node_compare", "index": "1" },
                                { "type": "way_node_compare", "index": "-1" }
                            ]
                        },
                        "checks": {
                            "type": "spatial_compare",
                            "operator": "touches",
                            "data_set_filter": {
                                "criteria": {
                                    "any": [
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" }
                                            ]
                                        },
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                                                { "type": "relation_exists",
                                                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                                                "relation_members": {
                                                    "criteria": {
                                                        "all": [
                                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                            { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" }
                                                        ]
                                                    }
                                                }
                                                }
                                            ]
                                        },
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SchifffahrtslinieFaehrverkehr" },
                                                { "type": "tag_equals", "tag_key": "art", "value": "1710" }
                                            ]
                                        },
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                            ]
                                        }
                                    ]
                                }
                            }
                        }
                    }
                },

                {
                    "relations": {
                        "loop_info": { "type": "any" },
                        "conditions": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                                { "type": "tag_equals", "tag_key": "fahrbahntrennung", "value": "2000" }
                            ]
                        },
                        "checks": {
                            "relation_members": {
                                "loop_info": { "type": "any" },
                                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                                "checks": {
                                    "any": [
                                        {
                                            "way_nodes": {
                                                "loop_info": { "type": "any" },
                                                "conditions": {
                                                    "any": [
                                                        { "type": "way_node_compare", "index": "1" },
                                                        { "type": "way_node_compare", "index": "-1" }
                                                    ]
                                                },
                                                "checks": {
                                                    "type": "spatial_compare",
                                                    "operator": "touches",
                                                    "data_set_filter": {
                                                        "criteria": {
                                                            "all": [
                                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                                { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" },
                                                                { "type": "tag_equals", "tag_key": "identifikator:UUID", "value": "base:identifikator:UUID" },
                                                                {
                                                                    "not": {
                                                                        "type": "relation_exists",
                                                                        "criteria": {
                                                                            "all": [
                                                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                                                                                { "type": "tag_equals", "tag_key": "fahrbahntrennung", "value": "2000" }
                                                                            ]
                                                                        }
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "way_nodes": {
                                                "loop_info": { "type": "all" },
                                                "conditions": {
                                                    "any": [
                                                        { "type": "way_node_compare", "index": "1" },
                                                        { "type": "way_node_compare", "index": "-1" }
                                                    ]
                                                },
                                                "checks": {
                                                    "type": "spatial_compare",
                                                    "operator": "touches",
                                                    "data_set_filter": {
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                                                    }
                                                }
                                            }
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
    'Das Netz des überörtlichen Durchgangsverkehrs BVB 1000 ist unterbrochen.')
ON CONFLICT (id) DO NOTHING;
