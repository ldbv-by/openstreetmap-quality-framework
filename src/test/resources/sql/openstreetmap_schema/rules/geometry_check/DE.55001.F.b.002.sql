INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.F.b.002',
    'geometry-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "1610" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_StehendesGewaesser" }
            }
        }
    }',
    'Ein Objekt ''AX_Gewaessermerkmal'' mit der ''art'' 1610 darf nur innerhalb von ''AX_StehendesGewaesser'' liegen.')
ON CONFLICT (id) DO NOTHING;