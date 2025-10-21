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
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Bahnstrecke" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_SeilbahnSchwebebahn", "bahnkategorie": "2500" } } }
                }
            ]
        }
    }',
    'Eine punktförmige ''AX_Bahnverkehrsanlage'' überlagert eine ''AX_Bahnstrecke'' oder ''AX_SeilbahnSchwebebahn'' mit ''bahnkategorie'' 2500.')
ON CONFLICT (id) DO NOTHING;
