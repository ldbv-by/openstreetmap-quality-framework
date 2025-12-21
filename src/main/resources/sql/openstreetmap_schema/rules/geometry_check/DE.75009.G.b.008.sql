INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75009.G.b.008',
    'geometry-check',
    'AA_REO',
    '{
        "conditions": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                        { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                    ]
                }
            }
        },

        "checks": {
            "relations": {
                "loop_info": { "type": "all" },
                "conditions": { "type": "tag_exists", "tag_key": "identifikator:UUID" },
                "checks": {
                    "all": [
                        { "type": "tag_starts_with", "tag_key": "identifikator:UUID", "value": "base:identifikator:UUID", "substring_start": "0", "substring_length": "4" },
                        {
                            "relation_members": {
                                "checks": {
                                    "type": "spatial_compare",
                                    "operator": "covered_by",
                                    "data_set_filter": {
                                        "aggregator": "union",
                                        "criteria": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                                { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                                            ]
                                        }
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        }
    }',
    'Auf der Landesgrenze gibt es falsche ZUSO-Bildungen.')
ON CONFLICT (id) DO NOTHING;