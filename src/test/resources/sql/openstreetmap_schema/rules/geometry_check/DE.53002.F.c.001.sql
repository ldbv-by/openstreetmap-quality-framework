INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.F.c.001',
    'geometry-check',
    'AA_hatDirektUnten',
    '{
        "conditions": {
            "all": [
                {
                    "relation_members": {
                        "loop_info": { "type": "any" },
                        "role": "over",
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" }
                    }
                },
                {
                    "relation_members": {
                        "loop_info": { "type": "any" },
                        "role": "under",
                        "checks": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "2010", "to_value": "2013" }
                            ]
                        }
                    }
                }
            ]
        },
        "checks": {
            "not": {
                "type": "spatial_compare",
                "reference_feature_role": "over",
                "operator": "covers",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenverkehrsanlage" },
                            { "type": "tag_equals", "tag_key": "art", "value": "2000" }
                        ]
                    }
                }
            }
        }
    }',
    'Furt darf ein unter der Erdoberfläche verlaufendes Gewässer nicht überlagern.')
ON CONFLICT (id) DO NOTHING;