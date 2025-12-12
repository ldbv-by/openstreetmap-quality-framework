INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57003.F.b.001',
    'geometry-check',
    'AX_Gewaesserstationierungsachse',
    '{
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "any": [
                        {   "type": "tag_in", "tag_key": "object_type", "values": ["AX_Fliessgewaesser", "AX_StehendesGewaesser", "AX_Hafenbecken", "AX_Meer", "AX_Insel" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2131", "2010", "2012", "2070", "2090"] }
                            ]
                        },
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
    'Eine Gewässerstationierungsachse darf nur in einem oder mehreren räumlich aneinandergrenzenden Objekten Fließgewässer, stehendem Gewässer, Hafenbecken, Meer, Insel, Bauwerk im Gewässerbereich liegen oder auf Grenze des Bundeslandes verlaufen.')
ON CONFLICT (id) DO NOTHING;
