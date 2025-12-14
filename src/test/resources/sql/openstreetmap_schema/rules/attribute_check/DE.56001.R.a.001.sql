INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.56001.R.a.001',
    'attribute-check',
    'AX_Netzknoten',
    '{
        "checks": {
            "all": [
                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "0" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Ast" }
                    }
                },

                {
                    "relation_members": {
                        "loop_info": { "type": "count", "minCount": "1" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Nullpunkt" }
                    }
                }
            ]
        }
    }',
    'Ein Netzknoten besteht aus einem oder mehreren Nullpunkten und optional einem oder mehreren Ästen.')
ON CONFLICT (id) DO NOTHING;
