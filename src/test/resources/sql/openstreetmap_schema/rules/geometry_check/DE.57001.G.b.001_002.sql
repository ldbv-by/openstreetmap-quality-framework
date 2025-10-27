INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57001.G.b.001_002',
    'geometry-check',
    'AX_Wasserspiegelhoehe',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser|AX_Gewaesserachse|AX_Hafenbecken|AX_StehendesGewaesser" } } }
        }
    }',
    'Ein Objekt ''AX_Wasserspiegelhoehe'' liegt immer auf einem Objekt ''AX_Fliessgewaesser'', ''AX_Gewaesserachse'', ''AX_Hafenbecken'' oder ''AX_StehendesGewaesser''.')
ON CONFLICT (id) DO NOTHING;
