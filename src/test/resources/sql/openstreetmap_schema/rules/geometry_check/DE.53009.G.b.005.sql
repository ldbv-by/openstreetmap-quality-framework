INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.b.005',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                {
                    "any": [
                        { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "2030", "to_value": "2060" },
                        { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "2080" }
                    ]
                },
                { "any": [ { "type": "geom_type", "value": "LineString"}, { "type": "geom_type", "value": "Polygon"} ] }
            ]
        },
        "checks": {
            "any": [
                {
                    "all": [
                        { "type": "geom_type", "value": "LineString" },
                        {
                            "relations": {
                                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                "checks": {
                                    "type": "spatial_compare",
                                    "reference_feature_role": "under",
                                    "operator": "equals_topo",
                                    "aggregator": "union",
                                    "data_set_filter": { "memberFilter": { "role": "over", "objectTypes": ["AX_Strassenachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_WegPfadSteig"] } },
                                    "self_check": true
                                }
                            }
                        }
                    ]
                },
                {
                    "all": [
                        { "type": "geom_type", "value": "Polygon" },
                        {
                            "relations": {
                                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                "checks": {
                                    "type": "spatial_compare",
                                    "reference_feature_role": "under",
                                    "operator": "contains",
                                    "data_set_filter": {
                                        "memberFilter": { "role": "over" }
                                    },
                                    "self_check": true
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }',
    'Bei ''bauwerksfunktion'' 2030 bis 2060, 2080 und linienförmiger Modellierung müssen die Geometrien der HDU Relations identisch sein. Bei flächenförmiger Modellierung müssen die ''over'' in ''under'' enthalten sein.')
ON CONFLICT (id) DO NOTHING;