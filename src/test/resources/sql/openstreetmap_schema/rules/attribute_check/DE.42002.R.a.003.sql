INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.R.a.003',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "fahrbahntrennung", "value": "2000" },
        "checks": {
            "any": [
                {
                    "all": [
                        {
                            "relation_members": {
                                "loop_info": { "type": "count", "minCount": "2" },
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                            }
                        },

                        {
                            "relation_members": {
                                "loop_info": { "type": "count", "minCount": "1", "maxCount": "1" },
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" }
                            }
                        }
                    ]
                },

                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1", "maxCount": "1" },
                        "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                        "checks": {
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
                        }
                    }
                },

                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1", "maxCount": "1" },
                        "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                        "checks": {
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
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                            ]
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ]
        }
    }',
    'Strasse mit FTR 2000 muss aus Strassenachse und mehreren Fahrbahnachsen bestehen.')
ON CONFLICT (id) DO NOTHING;
