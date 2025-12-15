INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44004.G.b.001',
    'geometry-check',
    'AX_Gewaesserachse',
    '{
        "conditions": {
            "all": [
                {
                    "relations": {
                        "loop_info": { "type": "none" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                    }
                },
                {
                    "type": "spatial_compare",
                    "operator": "intersects",
                    "data_set_filter": {
                        "criteria": {
                            "all": [
                                { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Fliessgewaesser", "AX_Hafenbecken", "AX_StehendesGewaesser", "AX_Meer"] },
                                {
                                    "not": {
                                        "type": "relation_exists",
                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                    }
                                }
                            ]
                        }
                    }
                }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "touches_endpoint_only",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Fliessgewaesser", "AX_Hafenbecken", "AX_StehendesGewaesser", "AX_Meer"] },
                        {
                            "not": {
                                "type": "relation_exists",
                                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                            }
                        }
                    ]
                }
            }
        }
    }',
    'Gewässerachse überlagert Fließgewässer, Hafenbecken, Stehendes Gewässer oder Meer.')
ON CONFLICT (id) DO NOTHING;
