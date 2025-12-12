INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75009.F.a.003',
    'geometry-check',
    'AX_Gebietsgrenze',
    '{
        "conditions": {
            "all": [
                { "not": { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7105" } },
                {
                    "type": "spatial_compare",
                    "operator": "touches",
                    "max_match_count": "2",
                    "data_set_filter": {
                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" }
                    }
                }
            ]
        },
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "touches",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                            { "not": { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "current:artDerGebietsgrenze" } }
                        ]
                    }
                }
            }
        }
    }',
    'Die AGZ-Werte der angrenzenden Gebietsgrenze sind nicht gleichartig.')
ON CONFLICT (id) DO NOTHING;


