INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41007.F.b.001',
    'geometry-check',
    'AX_FlaecheBesondererFunktionalerPraegung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_FlaecheBesondererFunktionalerPraegung", "AX_StehendesGewaesser", "AX_Hafenbecken" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                                { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Das Objekt ''AX_FlaecheBesondererFunktionalerPraegung'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_FlaecheBesondererFunktionalerPraegung'', ''AX_Fliessgewaesser'' ohne ''funktion'', ''AX_Hafenbecken'' oder ''AX_StehendesGewaesser'' überlagern.')
ON CONFLICT (id) DO NOTHING;