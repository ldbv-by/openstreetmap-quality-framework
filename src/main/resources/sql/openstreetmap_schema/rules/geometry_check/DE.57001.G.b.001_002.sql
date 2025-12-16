INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57001.G.b.001_002',
    'geometry-check',
    'AX_Wasserspiegelhoehe',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Fliessgewaesser", "AX_Gewaesserachse", "AX_Hafenbecken", "AX_StehendesGewaesser" ] },
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
    }',
    'Ein Objekt ''AX_Wasserspiegelhoehe'' liegt immer auf einem Objekt ''AX_Fliessgewaesser'', ''AX_Gewaesserachse'', ''AX_Hafenbecken'' oder ''AX_StehendesGewaesser''.')
ON CONFLICT (id) DO NOTHING;
