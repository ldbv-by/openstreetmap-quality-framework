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
                                    "reference_feature_role": "over",
                                    "operator": "equals_topo",
                                    "self_check": true,
                                    "data_set_filter": {
                                        "criteria": { "type": "tag_in", "tag_key": "object_type", "values": ["AA_hatDirektUnten", "AX_Strassenachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_WegPfadSteig"] },
                                        "memberFilter": { "role": "under" }
                                    }
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
                                    "reference_feature_role": "over",
                                    "operator": "contains",
                                    "self_check": true,
                                    "data_set_filter": {
                                        "memberFilter": { "role": "under" }
                                    }
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