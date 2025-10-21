INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.G.b.003',
    'geometry-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "art", "value": "2000" },
                { "type": "geom_type", "value": "Point" }
            ]
        },
        "checks": {
            "all": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Gewaesserachse" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Strassenachse|AX_Fahrwegachse|AX_WegPfadSteig" } } }
                }
            ]
        }
    }',
    'Das Objekt mit ''art'' 2000 liegt am Schnittpunkt einer ''AX_Gewaesserachse'' mit ''AX_Strassenachse'', ''AX_Fahrwegachse'' oder ''AX_WegPfadSteig''.')
ON CONFLICT (id) DO NOTHING;