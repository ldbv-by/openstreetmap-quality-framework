INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.52003.F.b.001',
    'geometry-check',
    'AX_Schleuse',
    '{
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser|AX_Hafenbecken|AX_StehendesGewaesser|AX_Meer" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Schiffsverkehr", "funktion": "5620" } } }
                }
            ]
        }
    }',
    'Ein Objekt ''AX_Schleuse'' muss auf ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'', ''AX_Meer'' oder ''AX_Schiffsverkehr'' mit ''funktion'' 5620 liegen.')
ON CONFLICT (id) DO NOTHING;
