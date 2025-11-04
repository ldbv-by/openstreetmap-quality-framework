INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.52003.F.b.001',
    'geometry-check',
    'AX_Schleuse',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Fliessgewaesser", "AX_Hafenbecken", "AX_StehendesGewaesser", "AX_Meer" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Schiffsverkehr" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "5620" }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein Objekt ''AX_Schleuse'' muss auf ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'', ''AX_Meer'' oder ''AX_Schiffsverkehr'' mit ''funktion'' 5620 liegen.')
ON CONFLICT (id) DO NOTHING;
