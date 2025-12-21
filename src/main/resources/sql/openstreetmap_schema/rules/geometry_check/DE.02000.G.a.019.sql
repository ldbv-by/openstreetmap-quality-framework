INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.02000.G.a.019',
    'geometry-check',
    'AA_REO',
    '{
        "conditions": {
            "all": [
                { "not": { "type": "tag_exists", "tag_key": "istWeitereNutzung" } },
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
                "operator": "equals_topo",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_equals", "tag_key": "object_type", "value": "current:object_type" },
                            {
                                "not": {
                                    "type": "relation_exists",
                                    "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                                }
                            },
                            { "not": { "type": "tag_exists", "tag_key": "istWeitereNutzung" } }
                        ]
                    }
                }
            }
        }
    }',
    'Doppeltes REO kann nur in Zusammenhang mit hDU-Relation vorkommen.')
ON CONFLICT (id) DO NOTHING;