INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.G.b.004',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "1470" },
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operators": ["covered_by", "intersects"],
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Hafenbecken" },
                            {
                                "all": [
                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SonstigesRecht" },
                                    { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "9450" }
                                ]
                            }
                        ]
                    }
                }
            }
        }
    }',
    'Ein Wasserliegeplatz darf nicht in ''AX_Hafenbecken'' oder ''AX_SonstigesRecht'' mit ''artDerFestlegung'' 9450 liegen oder schneiden.')
ON CONFLICT (id) DO NOTHING;