INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41005.F.b.001',
    'geometry-check',
    'AX_TagebauGrubeSteinbruch',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_TagebauGrubeSteinbruch", "AX_Fliessgewaesser", "AX_Hafenbecken", "AX_Meer" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_StehendesGewaesser" },
                                {
                                    "any": [
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } },
                                        { "type": "tag_equals", "tag_key": "funktion", "value": "8640" }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Das Objekt ''AX_TagebauGrubeSteinbruch'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_TagebauGrubeSteinbruch'', ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_Meer''  oder ''AX_StehendesGewaesser'' ohne ''funktion'' oder mit ''funktion'' 8640 überlagern.')
ON CONFLICT (id) DO NOTHING;
