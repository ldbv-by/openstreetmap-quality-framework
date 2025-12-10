INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53004.G.b.002',
    'geometry-check',
    'AX_Bahnverkehrsanlage',
    '{
        "conditions": { "type": "geom_type", "value": "Point" },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Bahnstrecke" } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": {
                        "criteria": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SeilbahnSchwebebahn" },
                                { "type": "tag_equals", "tag_key": "bahnkategorie", "value": "2500" }
                            ]
                        }
                    }
                }
            ]
        }
    }',
    'Eine punktförmige ''AX_Bahnverkehrsanlage'' überlagert eine ''AX_Bahnstrecke'' oder ''AX_SeilbahnSchwebebahn'' mit ''bahnkategorie'' 2500.')
ON CONFLICT (id) DO NOTHING;
