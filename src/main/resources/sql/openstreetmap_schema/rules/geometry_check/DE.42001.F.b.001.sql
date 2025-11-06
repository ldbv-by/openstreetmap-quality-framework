INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42001.F.b.001',
    'geometry-check',
    'AX_Strassenverkehr',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Meer" },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_StehendesGewaesser" },
                                {
                                    "any": [
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } },
                                        { "type": "tag_equals", "tag_key": "funktion", "value": "8631" }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Das Objekt ''AX_Strassenverkehr'' mit ''istWeitereNutzung'' muss ein Objekt ''AX_Meer''oder ''AX_StehendesGewaesser'' ohne ''funktion'' oder mit ''funktion'' 8631 überlagern.')
ON CONFLICT (id) DO NOTHING;
