INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57003.A.c.001',
    'attribute-check',
    'AX_Gewaesserstationierungsachse',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "2000" },
                {
                    "any": [
                        {
                            "relations": {
                                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                "checks": {
                                    "type": "spatial_compare",
                                    "reference_feature_role": "over",
                                    "operator": "within",
                                    "self_check": true,
                                    "data_set_filter": { "aggregator": "union", "memberFilter": { "role": "under" } }
                                }
                            }
                        },
                        {
                            "all": [
                                {
                                    "not": {
                                        "relations": {
                                            "loop_info": { "type": "all" },
                                            "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                        }
                                    }
                                },
                                {
                                    "type": "spatial_compare",
                                    "operator": "within",
                                    "data_set_filter": {
                                        "aggregator": "union",
                                        "criteria": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                                                { "type": "tag_equals", "tag_key": "funktion", "value": "8300" },
                                                { "not": { "type": "relation_exists", "object_type": "AA_hatDirektUnten" } }
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
    }',
    'Eine Gewässerstationierungsachse mit AGA 2000, die (vollständig) in einem oder mehreren Fließgewässern mit FKT 8300 liegt, hat FLR ''FALSE''')
ON CONFLICT (id) DO NOTHING;