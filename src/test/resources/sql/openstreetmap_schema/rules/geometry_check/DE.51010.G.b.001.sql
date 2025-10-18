INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51010.G.b.001',
    'geometry-check',
    'AX_EinrichtungInOeffentlichenBereichen',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "1410" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Strassenachse|AX_Bahnstrecke" } } }
        }
    }',
    'Ein Objekt mit der ''art'' 1410 liegt immer auf einem Objekt ''AX_Strassenachse'' oder ''AX_Bahnstrecke''.')
ON CONFLICT (id) DO NOTHING;
