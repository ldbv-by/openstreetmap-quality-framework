INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75009.F.a.002',
    'geometry-check',
    'AX_Gebietsgrenze',
    '{
        "checks": {
            "all": [
                {
                    "type": "spatial_compare",
                    "operator": "touches",
                    "min_match_count": "2",
                    "data_set_filter": {
                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" }
                    }
                },
                {
                    "not": {
                        "type": "spatial_compare",
                        "operator": "crosses",
                        "data_set_filter": {
                            "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" }
                        }
                    }
                }
            ]
        }
    }',
    'Gebietsgrenze ist unterbrochen bzw. überschneidet sich.')
ON CONFLICT (id) DO NOTHING;


