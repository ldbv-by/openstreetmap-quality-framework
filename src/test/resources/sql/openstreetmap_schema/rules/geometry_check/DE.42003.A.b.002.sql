INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42003.A.b.002',
    'geometry-check',
    'AX_Strassenachse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" },
        "checks": {
            "any": [
                {
                    "all": [
                        {
                            "way_nodes": {
                                "conditions": { "type": "way_node_compare", "index": "1" },
                                "checks": {
                                    "any": [
                                        {
                                            "type": "spatial_compare",
                                            "operator": "touches",
                                            "data_set_filter": {
                                                "any": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                    { "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SchifffahrtslinieFaehrverkehr" },
                                                        { "type": "tag_equals", "tag_key": "art", "value": "1710" }
                                                    ]},
                                                    { "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                        { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                                    ]}
                                                ]
                                            }
                                        },
                                        {
                                            "type": "spatial_compare",
                                            "operator": "touches",
                                            "data_set_filter": {
                                                "all": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                                                    {
                                                        "type": "relation_exists",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                                                        "relation_members": {
                                                            "all": [
                                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                                { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" }
                                                            ]
                                                      }
                                                    }
                                                ]
                                            }
                                        }
                                    ]
                                }
                            }
                        },
                        {
                            "way_nodes": {
                                "conditions": { "type": "way_node_compare", "index": "-1" },
                                "checks": {
                                    "any": [
                                        {
                                            "type": "spatial_compare",
                                            "operator": "touches",
                                            "data_set_filter": {
                                                "any": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                    { "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SchifffahrtslinieFaehrverkehr" },
                                                        { "type": "tag_equals", "tag_key": "art", "value": "1710" }
                                                    ]},
                                                    { "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                        { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                                    ]}
                                                ]
                                            }
                                        },
                                        {
                                            "type": "spatial_compare",
                                            "operator": "touches",
                                            "data_set_filter": {
                                                "all": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                                                    {
                                                        "type": "relation_exists",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                                                        "relation_members": {
                                                            "all": [
                                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                                { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" }
                                                            ]
                                                      }
                                                    }
                                                ]
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                },
                {
                    "relations": {
                        "loop_info": { "type": "any" },
                        "conditions": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                                { "type": "tag_equals", "tag_key": "fahrbahntrennung", "value": "2000" }
                            ]
                        },
                        "checks": {
                            "relation_members": {
                                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
                                "checks": {
                                    "any": [
                                        {
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
                                                        "all": [
                                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                            { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" },
                                                            { "not": { "type": "tag_equals", "tag_key": "identifikator:UUID", "value": "base:identifikator:UUID" } },
                                                            {
                                                                "type": "relation_exists",
                                                                "criteria": {
                                                                    "all": [
                                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
                                                                        { "not": { "type": "tag_equals", "tag_key": "fahrbahntrennung", "value": "2000" } }
                                                                    ]
                                                                }
                                                            }
                                                        ]
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "all": [
                                                {
                                                    "way_nodes": {
                                                        "conditions": { "type": "way_node_compare", "index": "1" },
                                                        "checks": {
                                                            "type": "spatial_compare",
                                                            "operator": "touches",
                                                            "data_set_filter": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                                                        }
                                                    }
                                                },
                                                {
                                                    "way_nodes": {
                                                        "conditions": { "type": "way_node_compare", "index": "-1" },
                                                        "checks": {
                                                            "type": "spatial_compare",
                                                            "operator": "touches",
                                                            "data_set_filter": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                                                        }
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            ]

        }
    }',
    'Das Objekt ''AX_Bahnverkehr'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_Bahnverkehr'', ''AX_Meer'' oder ''AX_StehendesGewaesser'' ohne ''funktion'' oder mit ''funktion'' 8631 überlagern.')
ON CONFLICT (id) DO NOTHING;
