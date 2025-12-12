INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57002.G.b.001_F.b.001',
    'geometry-check',
    'AX_SchifffahrtslinieFaehrverkehr',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Fliessgewaesser", "AX_Hafenbecken", "AX_StehendesGewaesser", "AX_Meer"] }
            }
        }
    }',
    'Ein Objekt ''AX_SchifffahrtslinieFaehrverkehr'' liegt immer innerhalb einem Objekt ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'' oder ''AX_Meer''.')
ON CONFLICT (id) DO NOTHING;
