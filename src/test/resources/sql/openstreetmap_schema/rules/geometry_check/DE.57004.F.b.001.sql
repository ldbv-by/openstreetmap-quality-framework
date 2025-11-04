INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57004.F.b.001',
    'geometry-check',
    'AX_Sickerstrecke',
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
                    "type": "spatial_compare",
                    "operator": "touches",
                    "data_set_filter": {
                        "criteria": {
                            "any": [
                                {
                                    "all": [
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                        { "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": ["7102"] }
                                    ]
                                },
                                {
                                    "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Sickerstrecke", "AX_Gewaesserachse", "AX_Gewaesserstationierungsachse" ]
                                }
                            ]
                        }
                    }
                }
            }
        }
    }',
    'Der Start- und Endpunkt von ''AX_Sickerstrecke'' muss immer an eine ''AX_Sickerstrecke'', ''AX_Gewaesserachse'', ''AX_Gewaesserstationierungsachse'' oder ''AX_Gebietgrenze'' mit ''artDerGebietsgrenze'' 7102 anschließen.')
ON CONFLICT (id) DO NOTHING;

