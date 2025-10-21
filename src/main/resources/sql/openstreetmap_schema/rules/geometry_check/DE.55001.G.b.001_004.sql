INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.G.b.001_004',
    'geometry-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": { "type": "tag_in", "tag_key": "art", "values": ["1630", "1640", "1650", "1660", "1700"] },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser|AX_Hafenbecken|AX_StehendesGewaesser||AX_Meer" } } }
        }
    }',
    'Ein ''AX_Gewaessermerkmal'' mit ''art'' 1630, 1640, 1650, 1660 oder 1700 liegen innerhalb eines ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'' oder ''AX_Meer''.')
ON CONFLICT (id) DO NOTHING;
