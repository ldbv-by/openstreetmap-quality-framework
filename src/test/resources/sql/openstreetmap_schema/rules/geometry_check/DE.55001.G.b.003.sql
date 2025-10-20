INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.55001.G.b.003',
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
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser", "funktion": "8230|not_exists" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "equals_topo",
                    "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Gewaesserachse", "funktion": "not_exists" } } }
                }
            ]
        }
    }',
    'Ein linienförmiges Objekt ''AX_Gewaessermerkmal'' mit ''art'' 1620 darf nur innerhalb von ''AX_Fliessgewaesser'' ohne ''funktion'' 8300 oder geometrieidentisch auf ''AX_Gewaesserachse'' ohne ''funktion'' 8300 liegen.')
ON CONFLICT (id) DO NOTHING;