INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75012.G.b.002',
    'geometry-check',
    'AA_Objekt',
    '{
        "conditions": {
            "any": [
                {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesTeilgebiet" },
                        { "type": "tag_equals", "tag_key": "hierarchiestufe", "value": "1" }
                    ]
                },
                {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesGebiet" },
                        {
                            "type": "spatial_compare",
                            "operator": "covers_by_multiline_as_polygon",
                            "data_set_filter": {
                                "criteria": {
                                    "all": [
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesTeilgebiet" },
                                        { "type": "tag_equals", "tag_key": "hierarchiestufe", "value": "1" }
                                    ]
                                }
                            }
                        }
                    ]
                }
            ]
        },

        "checks": {
            "all": [
                {
                    "any": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesGebiet" },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesTeilgebiet" },
                                {
                                    "not": {
                                        "type": "spatial_compare",
                                        "operator": "overlaps_by_multiline_as_polygon",
                                        "data_set_filter": {
                                            "criteria": {
                                                "all": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesTeilgebiet" },
                                                    { "type": "tag_equals", "tag_key": "hierarchiestufe", "value": "1" }
                                                ]
                                             }
                                        }
                                    }
                                }

                            ]
                        }
                    ]
                },
                {
                    "any": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesTeilgebiet" },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesGebiet" },
                                {
                                    "type": "spatial_compare",
                                    "operator": "equals_topo_by_multiline_as_polygon",
                                    "data_set_filter": {
                                        "aggregator": "union",
                                        "criteria": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesTeilgebiet" },
                                                { "type": "tag_equals", "tag_key": "hierarchiestufe", "value": "1" }
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    }',
    'Kommunales Teilgebiet liegt nicht deckungsgleich auf Kommunalem Gebiet.')
ON CONFLICT (id) DO NOTHING;


