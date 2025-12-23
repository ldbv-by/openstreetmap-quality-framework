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
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                        {
                            "not": {
                                "type": "relation_exists",
                                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Wasserlauf" },
                                "relation_members": {
                                    "criteria": {
                                        "all": [
                                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                                            { "not": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "current:fliessrichtung" } }
                                        ]
                                    }
                                }
                            }
                        }
                    ]
                }
            }
       }
    }',
    'Die Gewässerstationierungsachse hat die falsche Fließrichtung.')
ON CONFLICT (id) DO NOTHING;