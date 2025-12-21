INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.02000.G.a.025',
    'geometry-check',
    'AA_REO',
    '{
        "conditions": {
            "all": [
                { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Gewaesserstationierungsachse", "AX_Sickerstrecke"] },
                {
                    "not": {
                        "relations": {
                            "loop_info": { "type": "any" },
                            "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                        }
                    }
                }
            ]
        },

        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "crosses",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Gewaesserstationierungsachse", "AX_Sickerstrecke"] },
                            {
                                "not": {
                                    "type": "relation_exists",
                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                }
                            }
                        ]
                    }
                }
            }
        }
    }',
    'Fehlende REO Bildung.')
ON CONFLICT (id) DO NOTHING;