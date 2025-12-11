INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61001.G.c.001',
    'geometry-check',
    'AX_BoeschungKliff',
    '{
        "checks": {
            "relation_members": {
                "loop_info": { "type": "count", "minCount": "0", "maxCount": "1" },
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strukturlinie3D" },
                        { "type": "tag_equals", "tag_key": "art", "value": "1250" }
                    ]
                }
            }
        }
    }',
    'Es ist nur ein Gefällewechsel innerhalb ''AX_BoeschungKliff'' möglich.')
ON CONFLICT (id) DO NOTHING;