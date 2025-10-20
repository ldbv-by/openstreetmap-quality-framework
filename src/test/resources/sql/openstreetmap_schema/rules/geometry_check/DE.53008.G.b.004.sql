INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.G.b.004',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "1470" },
        "checks": {
            "all": [
                {
                    "not": {
                        "type": "spatial_compare",
                        "operators": ["covered_by", "intersects"],
                        "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Hafenbecken" } } }
                    }
                },
                {
                    "not": {
                        "type": "spatial_compare",
                        "operators": ["covered_by", "intersects"],
                        "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_SonstigesRecht", "artDerFestlegung": "9450" } } }
                    }
                }
            ]
        }
    }',
    'Ein Wasserliegeplatz darf nicht in ''AX_Hafenbecken'' oder ''AX_SonstigesRecht'' mit ''artDerFestlegung'' 9450 liegen oder schneiden.')
ON CONFLICT (id) DO NOTHING;