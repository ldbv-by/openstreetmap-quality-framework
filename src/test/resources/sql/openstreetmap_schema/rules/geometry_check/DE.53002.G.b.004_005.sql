INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.G.b.004_005',
    'geometry-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": {
            "all": [
                { "type": "tag_in", "tag_key": "art", "values": ["3000", "3001", "3002", "3003"] },
                { "type": "geom_type", "value": "Point" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Strassenachse" } } }
        }
    }',
    'Ein Objekt mit der ''art'' 3000, 3001, 3002 oder 3003 muss auf einem Objekt ''AX_Strassenachse'' liegen.')
ON CONFLICT (id) DO NOTHING;