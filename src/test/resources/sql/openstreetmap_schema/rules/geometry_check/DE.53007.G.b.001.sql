INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53007.G.b.001',
    'geometry-check',
    'AX_Flugverkehrsanlage',
    '{
        "conditions": { "type": "tag_between", "tag_key": "art", "from_value": "1310", "to_value": "1330" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Flugverkehr" } } }
        }
    }',
    'Ein Objekt mit ''art'' 1310 bis 1330 überlagert immer ein Objekt ''AX_Flugverkehr''.')
ON CONFLICT (id) DO NOTHING;
