INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57002.G.b.002_F.b.002',
    'geometry-check',
    'AX_SchifffahrtslinieFaehrverkehr',
    '{
        "checks": {
            "way_nodes": {
                "conditions": {
                    "any": [
                        { "type": "way_node_compare", "index": "1" },
                        { "type": "way_node_compare", "index": "-1" }
                    ]
                },
                "checks": {
                    "any": [
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_EinrichtungenFuerDenSchiffsverkehr" },
                                { "type": "tag_equals", "tag_key": "art", "value": "1460" }
                            ]
                        },
                        {
                            "type": "spatial_compare",
                            "operator": "touches",
                            "data_set_filter": {
                                "criteria": {
                                    "any": [
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_EinrichtungenFuerDenSchiffsverkehr" },
                                                { "type": "tag_equals", "tag_key": "art", "value": "1460" }
                                            ]
                                        },
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                { "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": ["7101", "7102"] }
                                            ]
                                        },
                                        {   "type": "tag_equals", "tag_key": "object_type", "value": "AX_SchifffahrtslinieFaehrverkehr" }
                                    ]
                                }
                            }
                        }
                    ]
                }
            }
        }
    }',
    'Der Start- und Endpunkt von ''AX_SchifffahrtslinieFaehrverkehr'' liegt immer an ''AX_EinrichtungenFuerDenSchiffsverkehr'' mit ''art'' 1460 , ''AX_Gebietsgrenze'' mit ''artDerGebietsgrenze'' 7101 oder 7102 oder einem weiteren Objekt ''AX_SchifffahrtslinieFaehrverkehr''.')
ON CONFLICT (id) DO NOTHING;