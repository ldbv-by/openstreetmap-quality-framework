INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41008.F.b.001',
    'geometry-check',
    'AX_SportFreizeitUndErholungsflaeche',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "type": "tag_in", "tag_key": "object_type", "values": [ "AX_SportFreizeitUndErholungsflaeche", "AX_Fliessgewaesser", "AX_StehendesGewaesser", "AX_Meer", "AX_Hafenbecken" ]
                }
            }
        }
    }',
    'Das Objekt ''AX_SportFreizeitUndErholungsflaeche'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_SportFreizeitUndErholungsflaeche'', ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_Meer''  oder ''AX_StehendesGewaesser überlagern.')
ON CONFLICT (id) DO NOTHING;
