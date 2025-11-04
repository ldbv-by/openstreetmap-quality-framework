INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.F.d.001',
    'geometry-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "1630" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                        {
                            "any": [
                                { "not": { "type": "tag_equals", "tag_key": "funktion", "value": "8300" } },
                                { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein ''AX_Gewaessermerkmal'' mit ''art'' 1630 darf nur innerhalb ''AX_Fliessgewaesser'' ohne ''funktion'' 8300 liegen.')
ON CONFLICT (id) DO NOTHING;
