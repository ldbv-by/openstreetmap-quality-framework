INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51005.G.b.001_F.c.001_F.b.001',
    'geometry-check',
    'AX_Leitung',
    '{
        "checks": {
            "all": [
                {
                    "way_nodes": {
                        "conditions": {
                            "all": [
                                { "not": { "type": "way_node_compare", "index": "1" } },
                                { "not": { "type": "way_node_compare", "index": "-1" } }
                            ]
                        },
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkOderAnlageFuerIndustrieUndGewerbe" },
                                { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1251" }
                            ]
                        }
                    }
                },
                {
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
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkOderAnlageFuerIndustrieUndGewerbe" },
                                        { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1251" }
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
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                        { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                                    ]
                                                },
                                                {
                                                    "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_IndustrieUndGewerbeflaeche" },
                                                        { "type": "tag_in", "tag_key": "funktion", "values": [ "2530", "2540" ] }
                                                    ]
                                                }
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            ]
        }
    }',
    'Ein Objekt ''AX_Leitung'' muss an einen Knickpunkt stets einen Freileitungsmast haben und an einen Freileitungsmast, der Landesgrenze oder in einem Kraftwerk oder einer Umspannstation beginnen und enden.')
ON CONFLICT (id) DO NOTHING;