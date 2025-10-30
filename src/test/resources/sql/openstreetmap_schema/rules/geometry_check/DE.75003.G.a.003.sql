INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.DE.75003.G.a.003',
    'geometry-check',
    'AX_KommunalesGebiet',
    '{
        "checks": {
            "all": [
                {
                    "relation_members": {
                        "all": [
                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                            { "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": [ "7101", "7102", "7104", "7105", "7106", "7107" ] }
                        ]
                    }
                },
                {
                    "type": "spatial_compare",
                    "operator": "surrounded_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_KommunalesGebiet|AX_Gebietsgrenze", "artDerGebietsgrenze": "not_exists|7101|7102" } } }
                }
            ]
        }
    }',
    'Die Members von ''AX_KommunalesGebiet'' dürfen nur ''AX_Gebietsgrenze'' mit ''artDerGebietsgrenze'' 7101, 7102, 7104, 7105, 7106 oder 7107 sein. Zudem muss ''AX_KommunalesGebiet'' muss lückenlos und flächendeckend sein.')
ON CONFLICT (id) DO NOTHING;