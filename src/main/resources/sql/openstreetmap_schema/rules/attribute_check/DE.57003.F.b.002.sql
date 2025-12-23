INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57003.F.b.002',
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
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Fliessgewaesser"] },
                        { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "current:fliessrichtung" }
                    ]
                }
            }
        }
    }',
    'Die Gewässerstationierungsachse hat die falsche Fließrichtung.')
ON CONFLICT (id) DO NOTHING;