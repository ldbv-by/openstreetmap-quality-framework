INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41009.F.b.001',
    'geometry-check',
    'AX_Friedhof',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Friedhof", "AX_Meer" ] },
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
    'Das Objekt ''AX_Friedhof'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_Friedhof'', ''AX_Meer'' oder ''AX_StehendesGewaesser'' ohne ''funktion'' überlagern.')
ON CONFLICT (id) DO NOTHING;
