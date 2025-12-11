INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.G.b.001',
    'geometry-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "conditions": { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "1800", "to_value": "1870" },
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
                                    "data_set_filter": {
                                        "aggregator": "union",
                                        "memberFilter": {
                                            "role": "over",
                                            "objectTypes": ["AX_Strassenachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_Gewaesserachse", "AX_WegPfadSteig", "AX_Gleis"]
                                        }
                                    },
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
                                    "data_set_filter": { "aggregator": "union", "memberFilter": { "role": "over" } },
                                    "self_check": true
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }',
    'Bei ''bauwerksfunktion'' 1800 bis 1870 und linienförmiger Modellierung müssen die Geometrien der HDU Relations identisch sein. Bei flächenförmiger Modellierung müssen die ''over'' in ''under'' enthalten sein.')
ON CONFLICT (id) DO NOTHING;
