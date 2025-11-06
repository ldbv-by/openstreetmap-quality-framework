INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42015.F.b.001',
    'geometry-check',
    'AX_Flugverkehr',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Flugverkehr", "AX_Meer" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_StehendesGewaesser" },
                                {
                                    "any": [
                                        { "type": "tag_in", "tag_key": "funktion", "values": ["8630", "8631"] },
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Das Objekt ''AX_Flugverkehr'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_Flugverkehr'', ''AX_Meer'' oder ''AX_StehendesGewaesser'' ohne ''funktion'' oder mit ''funktion'' 8630 oder 8631 überlagern.')
ON CONFLICT (id) DO NOTHING;