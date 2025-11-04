INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.G.b.003_F.b.003',
    'geometry-check',
    'AX_Gewaessermerkmal',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "LineString" },
                { "type": "tag_equals", "tag_key": "art", "value": "1620" }
            ]
        },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": {
                        "criteria": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                                {
                                    "any": [
                                        { "type": "tag_equals", "tag_key": "funktion", "value": "8230" },
                                        { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                                    ]
                                }
                            ]
                        }
                    }
                },
                {
                    "type": "spatial_compare",
                    "operator": "equals_topo",
                    "data_set_filter": {
                        "criteria": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                                { "not": { "type": "tag_exists", "tag_key": "funktion" } }
                            ]
                        }
                    }
                }
            ]
        }
    }',
    'Ein linienförmiges Objekt ''AX_Gewaessermerkmal'' mit ''art'' 1620 darf nur innerhalb von ''AX_Fliessgewaesser'' ohne ''funktion'' 8300 oder geometrieidentisch auf ''AX_Gewaesserachse'' ohne ''funktion'' 8300 liegen.')
ON CONFLICT (id) DO NOTHING;