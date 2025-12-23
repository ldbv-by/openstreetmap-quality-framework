INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57003.F.c.004',
    'attribute-check',
    'AX_Gewaesserstationierungsachse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "2000" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wasserlauf", "AX_Kanal"] },
                        { "type": "tag_equals", "tag_key": "gewaesserkennzahl", "value": "current:gewaesserkennzahl" },
                        { "type": "tag_equals", "tag_key": "name", "value": "current:name" }
                    ]
                }
            }
       }
    }',
    'Die Gewässerstationierungsachse hat abweichende GWK/NAM vom unterlegten ZUSO.')
ON CONFLICT (id) DO NOTHING;