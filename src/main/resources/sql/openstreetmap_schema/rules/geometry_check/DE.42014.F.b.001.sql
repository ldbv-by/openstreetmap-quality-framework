INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42014.F.b.001',
    'geometry-check',
    'AX_Bahnstrecke',
    '{
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
                            "any": [
                                {
                                    "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Bahnstrecke", "AX_SchifffahrtslinieFaehrverkehr" ]
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
        }
    }',
    'Bahnstrecke ist nicht an das Verkehrsnetz angeschlossen.')
ON CONFLICT (id) DO NOTHING;