INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44002.R.a.001',
    'attribute-check',
    'AX_Wasserlauf',
    '{
        "checks": {
            "relation_members": {
                "loop_info": { "type": "count", "minCount": "1" },
                "checks": { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Gewaesserachse", "AX_Fliessgewaesser" ] }
            }
        }
    }',
    'Das ''Wasserlauf'' besteht aus einem oder mehreren REO ''Fließgewässer'' oder einem oder mehreren REO ''Gewässerachse'' oder einem oder mehreren REO ''Fließgewässer'' und einem oder mehreren REO ''Gewässerachse''.')
ON CONFLICT (id) DO NOTHING;
