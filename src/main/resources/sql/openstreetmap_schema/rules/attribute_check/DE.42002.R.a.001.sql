INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.R.a.001',
    'attribute-check',
    'AX_Strasse',
    '{
        "checks": {
            "all": [
                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "0" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                    }
                },

                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" }
                    }
                }
            ]
        }
    }',
    'Eine ''Straße'' besteht aus einem oder mehreren REO ''Straßenachse'' oder einem oder mehreren REO ''Straßenachse'' und einem oder mehreren REO ''Fahrbahnachse''.')
ON CONFLICT (id) DO NOTHING;
