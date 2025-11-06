INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41002.F.b.001',
    'geometry-check',
    'AX_IndustrieUndGewerbeflaeche',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_IndustrieUndGewerbeflaeche", "AX_Meer", "AX_Hafenbecken" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                                {
                                    "any": [
                                        { "type": "tag_equals", "tag_key": "funktion", "value": "8300" },
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                                    ]
                                }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_StehendesGewaesser" },
                                { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Das Objekt ''AX_IndustrieUndGewerbeflaeche'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_IndustrieUndGewerbeflaeche'', ''AX_Hafenbecken'', ''AX_Meer'', ''AX_Fliessgewaesser'' ohne ''funktion'' oder mit ''funktion'' 8300 oder ''AX_StehendesGewaesser'' ohne ''funktion'' überlagern.')
ON CONFLICT (id) DO NOTHING;

