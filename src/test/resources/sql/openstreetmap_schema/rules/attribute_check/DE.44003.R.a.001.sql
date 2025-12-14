INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44003.R.a.001',
    'attribute-check',
    'AX_Kanal',
    '{
        "checks": {
            "relation_members": {
                "loop_info": { "type": "count", "minCount": "1" },
                "checks": {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Gewaesserachse", "AX_Fliessgewaesser" ] },
                        { "type": "tag_equals", "tag_key": "funktion", "value": "8300" }
                    ]
                }
            }
        }
    }',
    '''Kanal'' besteht aus einem oder mehreren REO ''Fließgewässer'' mit FKT 8300 oder einem oder mehreren REO ''Gewässerachse'' mit FKT 8300 oder einem oder mehreren REO ''Fließgewässer'' mit FKT 8300 und einem oder mehreren REO ''Gewässerachse'' mit FKT 8300.')
ON CONFLICT (id) DO NOTHING;
