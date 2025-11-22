INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61003.F.b.002',
    'geometry-check',
    'AX_DammWallDeich',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "funktion", "value": "3001" },
        "checks": {
            "all": [
                {
                    "any": [
                        { "not": {
                            "type": "spatial_compare",
                            "operator": "intersects",
                            "data_set_filter": {
                                "criteria": {
                                    "all": [
                                        {
                                            "any": [
                                                { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Strassenachse", "AX_Fahrwegachse", "AX_Fahrbahnachse", "AX_Bahnstrecke", "AX_Gleis" ] },
                                                {
                                                    "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_WegPfadSteig" },
                                                        { "type": "tag_in", "tag_key": "art", "values": [ "1106", "1110" ] }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            "any": [
                                                {
                                                    "not": {
                                                        "type": "relation_exists",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                                    }
                                                },
                                                {
                                                    "type": "relation_exists",
                                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                                    "relation_members": {
                                                        "role": "under",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_DammWallDeich" }
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        }},
                        {
                            "type": "spatial_compare",
                            "operator": "touches",
                            "data_set_filter": {
                                "criteria": {
                                    "all": [
                                        {
                                            "any": [
                                                { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Strassenachse", "AX_Fahrwegachse", "AX_Fahrbahnachse", "AX_Bahnstrecke", "AX_Gleis" ] },
                                                {
                                                    "all": [
                                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_WegPfadSteig" },
                                                        { "type": "tag_in", "tag_key": "art", "values": [ "1106", "1110" ] }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            "any": [
                                                {
                                                    "not": {
                                                        "type": "relation_exists",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                                    }
                                                },
                                                {
                                                    "type": "relation_exists",
                                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                                    "relation_members": {
                                                        "role": "under",
                                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_DammWallDeich" }
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                },
                {
                    "not": {
                        "type": "spatial_compare",
                        "operator": "intersects",
                        "data_set_filter": {
                            "criteria": {
                                "all": [
                                    {
                                        "any": [
                                            { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Strassenverkehr", "AX_Bahnverkehr" ] }
                                        ]
                                    },
                                    {
                                        "any": [
                                            {
                                                "not": {
                                                    "type": "relation_exists",
                                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                                }
                                            },
                                            {
                                                "type": "relation_exists",
                                                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                                "relation_members": {
                                                    "role": "under",
                                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_DammWallDeich" }
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    }
                }
            ]
        }
    }',
    'Ein Objekt ''AX_DammWallDeich'' mit ''funktion'' 3001 darf keinen Verkehrsweg führen.')
ON CONFLICT (id) DO NOTHING;