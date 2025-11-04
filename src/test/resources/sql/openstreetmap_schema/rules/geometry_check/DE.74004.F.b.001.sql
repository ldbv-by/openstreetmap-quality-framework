INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.74004.F.b.001',
    'geometry-check',
    'AX_Insel',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "surrounded_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Insel", "AX_Fliessgewaesser", "AX_Hafenbecken", "AX_StehendesGewaesser", "AX_Meer" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein Objekt ''AX_Insel'' muss vollständig an ein ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'', ''AX_Meer'', ''AX_Insel'' oder an eine ''AX_Gebietsgrenze'' mit ''artDerGebietsgrenze'' 7102 grenzen.')
ON CONFLICT (id) DO NOTHING;
