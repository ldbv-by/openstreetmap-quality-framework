INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61001.R.a.002_003',
    'attribute-check',
    'AX_BoeschungKliff',
    '{
        "checks": {
            "all": [
                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1" },
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strukturlinie3D" },
                                { "type": "tag_equals", "tag_key": "art", "value": "1210" }
                            ]
                        }
                    }
                },
                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1" },
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strukturlinie3D" },
                                { "type": "tag_equals", "tag_key": "art", "value": "1220" }
                            ]
                        }
                    }
                },
                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1" },
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strukturlinie3D" },
                                { "type": "tag_equals", "tag_key": "art", "value": "1230" }
                            ]
                        }
                    }
                }
            ]
        }
    }',
    'Eine ''Böschung, Kliff'' besteht mindestens aus je einem REO ''Strukturlinie3D'' mit (ART 1210 oder ART 1220) und ART 1230.')
ON CONFLICT (id) DO NOTHING;
