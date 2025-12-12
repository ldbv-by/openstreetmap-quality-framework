INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57002.F.c.001',
    'geometry-check',
    'AX_SchifffahrtslinieFaehrverkehr',
    '{
        "conditions": { "type": "tag_in", "tag_key": "art", "values": ["1710", "1720"] },
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
                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_SchifffahrtslinieFaehrverkehr", "AX_Strassenachse", "AX_Fahrbahnachse", "AX_Fahrwegachse", "AX_Bahnstrecke" ]
                        }
                    }
                }
            }
        }
    }',
    'Der Start- und Endpunkt von ''AX_SchifffahrtslinieFaehrverkehr'' mit ''art'' 1710 oder 1720 muss an ''AX_SchifffahrtslinieFaehrverkehr'', ''AX_Strassenachse'', ''AX_Fahrbahnachse'', ''AX_Fahrwegachse'' oder ''AX_Bahnstrecke'' anschließen.')
ON CONFLICT (id) DO NOTHING;
