INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.G.b.002',
    'geometry-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "conditions": {
            "all" : [
                { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1880" },
                { "type": "geom_type", "value": "LineString" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "equals_topo",
            "aggregator": "union",
            "data_set_filter": {
                "criteria": {
                    "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrbahnachse", "AX_Fahrwegachse",
                                                                           "AX_WegPfadSteig", "AX_Bahnstrecke"]
                }
            }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1880 bei linienförmiger Modellierung muss ein Objekt ''AX_Strassenachse'', ''AX_Fahrbahnachse'', ''AX_Fahrwegachse'', ''AX_WegPfadSteig'' oder''AX_Bahnstrecke'' überlagern.')
ON CONFLICT (id) DO NOTHING;