INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.73009.R.a.001',
    'attribute-check',
    'AX_Verwaltungsgemeinschaft',
    '{
        "checks": {
            "relation_members": {
                "loop_info": { "type": "count", "minCount": "2" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gemeinde" }
            }
        }
    }',
    'Eine ''Verwaltungsgemeinschaft'' besteht aus mehreren NREO ''Gemeinde''.')
ON CONFLICT (id) DO NOTHING;
