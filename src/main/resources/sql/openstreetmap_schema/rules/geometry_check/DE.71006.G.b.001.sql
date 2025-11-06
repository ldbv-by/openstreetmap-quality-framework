INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71006.G.b.001',
    'geometry-check',
    'AX_NaturUmweltOderBodenschutzrecht',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "1653" },
        "checks": {
            "any": [
                {
                    "all": [
                        { "type": "geom_type", "value": "Point" },
                        {
                            "any": [
                                {
                                    "type": "spatial_compare",
                                    "operator": "covered_by",
                                    "data_set_filter": {
                                        "criteria": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Vegetationsmerkmal" },
                                                { "type": "tag_in", "tag_key": "bewuchs", "values": ["1011", "1012"] }
                                            ]
                                        }
                                    }
                                },
                                {
                                    "type": "spatial_compare",
                                    "operator": "equals_topo",
                                    "data_set_filter": {
                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_FelsenFelsblockFelsnadel" }
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    "all": [
                        { "type": "geom_type", "value": "LineString" },
                        {
                            "any": [
                                {
                                    "type": "spatial_compare",
                                    "operator": "covered_by",
                                    "data_set_filter": {
                                        "criteria": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Vegetationsmerkmal" },
                                                { "type": "tag_in", "tag_key": "bewuchs", "values": ["1100", "1210", "1220", "1230"] }
                                            ]
                                        }
                                    }
                                },
                                {
                                    "type": "spatial_compare",
                                    "operator": "equals_topo",
                                    "data_set_filter": {
                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_FelsenFelsblockFelsnadel" }
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    "all": [
                        { "type": "geom_type", "value": "Polygon" },
                        {
                            "type": "spatial_compare",
                            "operator": "covered_by",
                            "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_FelsenFelsblockFelsnadel" } }
                        }
                    ]
                }
            ]
        }
    }',
    'Ein Objekt ''AX_NaturUmweltOderBodenschutzrecht'' mit ''artDerFestlegung'' 1653 muss über einem ''AX_Vegetationsmerkmal'' oder ''AX_FelsenFelsblockFelsnadel'' liegen.')
ON CONFLICT (id) DO NOTHING;