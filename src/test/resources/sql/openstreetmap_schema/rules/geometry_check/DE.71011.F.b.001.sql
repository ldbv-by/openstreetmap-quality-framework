INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71011.F.b.001',
    'geometry-check',
    'AX_SonstigesRecht',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "9450" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Hafen",
                                                                            "AX_Fliessgewaesser",
                                                                            "AX_StehendesGewaesser",
                                                                            "AX_Meer" ]
                }
            }
        }
    }',
    'Ein Objekt ''AX_SonstigesRecht'' mit ''artDerFestlegung'' 9450 liegt immer innerhalb von ''AX_Hafen'', ''AX_Fliessgewaesser'', ''AX_StehendesGewaesser'' oder ''AX_Meer''.')
ON CONFLICT (id) DO NOTHING;