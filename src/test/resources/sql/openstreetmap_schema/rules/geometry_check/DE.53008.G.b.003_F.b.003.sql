INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.G.b.003_F.b.003',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "art", "value": "1460" },
                { "not": { "type": "geom_type", "value": "Point" } }
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
                                    "relation_master_role": "over",
                                    "operator": "equals_topo",
                                    "relation_compare_role": "under",
                                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Strassenachse|AX_Fahrwegachse|AX_Bahnstrecke|AX_WegPfadSteig|AX_Gleis" } } }
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
                                    "relation_master_role": "over",
                                    "operator": "contains",
                                    "relation_compare_role": "under"
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }',
    'Bei ''art'' 1460 und linienförmiger Modellierung müssen die Geometrien der HDU Relations identisch sein. Bei flächenförmiger Modellierung müssen die ''over'' in ''under'' enthalten sein.')
ON CONFLICT (id) DO NOTHING;
